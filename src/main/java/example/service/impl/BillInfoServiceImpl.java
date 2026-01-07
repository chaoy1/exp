package example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import example.entity.BillInfo;
import example.entity.LedgerInfo;
import example.mapper.BillInfoMapper;
import example.mapper.LedgerInfoMapper;
import example.service.BillInfoService;
import example.service.LedgerInfoService; // 假设你有这个服务来更新账本统计
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillInfoServiceImpl extends ServiceImpl<BillInfoMapper, BillInfo> implements BillInfoService {

    @Autowired
    private BillInfoMapper billInfoMapper;
    @Autowired
    private LedgerInfoMapper ledgerInfoMapper;
    @Autowired
    private LedgerInfoService ledgerInfoService;

    @Override
    @Transactional
    public void addBill(BillInfo billInfo) {
        // 1. 验证账本是否存在
        LedgerInfo ledger = ledgerInfoMapper.selectById(billInfo.getLedgerInfoId());
        if (ledger == null) {
            throw new RuntimeException("关联的账本不存在。");
        }

        // 2. 设置创建时间
        billInfo.setCreatedTime(LocalDateTime.now());

        // 3. 保存账单
        this.save(billInfo);

        // 4. 更新账本统计信息 (收入、支出、笔数)
        updateLedgerStatistics(billInfo.getLedgerInfoId(), billInfo.getAmount(), billInfo.getType());
    }

    @Override
    @Transactional
    public void deleteBill(Long id) {
        // 1. 获取待删除的账单详情，用于后续更新统计
        BillInfo billToDelete = billInfoMapper.selectBillWithLedgerInfo(id);
        if (billToDelete == null) {
            throw new RuntimeException("账单不存在，无法删除。");
        }

        // 2. 删除账单
        this.removeById(id);

        // 3. 更新账本统计信息 (减少对应的收入/支出、笔数)
        updateLedgerStatisticsOnDelete(billToDelete.getLedgerInfoId(), billToDelete.getAmount(), billToDelete.getType());
    }

    @Override
    public void updateBill(BillInfo billInfo) {
        // 1. 获取数据库中原始账单信息
        BillInfo originalBill = this.getById(billInfo.getId());
        if (originalBill == null) {
            throw new RuntimeException("账单不存在，无法更新。");
        }

        // 2. 检查账本是否被修改 (要求不允许修改账本)
        if (!originalBill.getLedgerInfoId().equals(billInfo.getLedgerInfoId())) {
            throw new RuntimeException("不允许修改账单所属的账本。");
        }

        LambdaUpdateWrapper<BillInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BillInfo::getId, billInfo.getId())
                .set(BillInfo::getType, billInfo.getType())
                .set(BillInfo::getCategory, billInfo.getCategory())
                .set(BillInfo::getAmount, billInfo.getAmount())
                .set(BillInfo::getRemark, billInfo.getRemark())
                .set(BillInfo::getCreatedTime, LocalDateTime.now());

        this.update(updateWrapper);

        updateLedgerStatisticsOnUpdate(originalBill, billInfo);
    }

    @Override
    public BillInfo getBillDetail(Long id) {
        return billInfoMapper.selectBillWithLedgerInfo(id);
    }

    @Override
    public Page<BillInfo> getBillPage(int current, int size, Long ledgerInfoId, String type, Long category) {
        // 1. 验证账本是否存在 (可选)
        if (ledgerInfoId != null) {
            LedgerInfo ledger = ledgerInfoMapper.selectById(ledgerInfoId);
            if (ledger == null) {
                throw new RuntimeException("关联的账本不存在。");
            }
        }

        // 2. 创建分页对象
        Page<BillInfo> page = new Page<>(current, size);

        // 3. 构建查询条件，仅查询账单表的字段
        LambdaQueryWrapper<BillInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (ledgerInfoId != null) {
            queryWrapper.eq(BillInfo::getLedgerInfoId, ledgerInfoId);
        }
        if (type != null && !type.trim().isEmpty()) {
            queryWrapper.eq(BillInfo::getType, type);
        }
        if (category != null) {
            queryWrapper.eq(BillInfo::getCategory, category);
        }
        queryWrapper.orderByDesc(BillInfo::getCreatedTime); // 按创建时间降序

        // 4. 执行分页查询，获取基础账单信息（只包含 ID 和账单表字段）
        Page<BillInfo> basePage = this.page(page, queryWrapper);

        // 5. 获取当前页的账单ID列表
        List<Long> billIds = basePage.getRecords().stream()
                .map(BillInfo::getId)
                .collect(Collectors.toList());

        // 6. 如果当前页有数据，根据 ID 列表查询详情（包含账本信息）
        if (!billIds.isEmpty()) {
            // 为每个 ID 调用 getBillDetail 方法，该方法会返回包含账本信息的完整对象
            // 这会产生 N 次查询，但实现简单
            List<BillInfo> detailedRecords = billIds.stream()
                    .map(this::getBillDetail) // 调用上面的 getBillDetail 方法
                    .collect(Collectors.toList());

            // 重新构建分页对象，设置包含账本信息的记录列表
            Page<BillInfo> resultPage = new Page<>();
            resultPage.setRecords(detailedRecords);
            resultPage.setTotal(basePage.getTotal());
            resultPage.setCurrent(basePage.getCurrent());
            resultPage.setSize(basePage.getSize());
            resultPage.setPages(basePage.getPages());
            return resultPage;
        } else {
            // 如果当前页没有数据，返回空的分页对象
            Page<BillInfo> emptyPage = new Page<>();
            emptyPage.setRecords(Collections.emptyList());
            emptyPage.setTotal(0);
            emptyPage.setCurrent(current);
            emptyPage.setSize(size);
            emptyPage.setPages(0);
            return emptyPage;
        }
    }

    // 辅助方法：更新账本统计 (新增账单时)
    private void updateLedgerStatistics(Long ledgerId, BigDecimal amount, String type) {
        BigDecimal incomeIncrement = "INCOME".equalsIgnoreCase(type) ? amount : BigDecimal.ZERO;
        BigDecimal expenditureIncrement = "EXPENDITURE".equalsIgnoreCase(type) ? amount : BigDecimal.ZERO;
        Integer numberIncrement = 1;
        ledgerInfoService.updateLedgerStatisticsWithIncrement(ledgerId, incomeIncrement, expenditureIncrement, numberIncrement);
    }

    // 辅助方法：更新账本统计 (删除账单时)
    private void updateLedgerStatisticsOnDelete(Long ledgerId, BigDecimal amount, String type) {
        BigDecimal incomeDecrement = "INCOME".equalsIgnoreCase(type) ? amount.negate() : BigDecimal.ZERO; // 取负数
        BigDecimal expenditureDecrement = "EXPENDITURE".equalsIgnoreCase(type) ? amount.negate() : BigDecimal.ZERO; // 取负数
        Integer numberDecrement = -1;
        ledgerInfoService.updateLedgerStatisticsWithIncrement(ledgerId, incomeDecrement, expenditureDecrement, numberDecrement);
    }

    // 辅助方法：更新账本统计 (编辑账单时)
    private void updateLedgerStatisticsOnUpdate(BillInfo originalBill, BillInfo updatedBill) {
        // 计算差值
        BigDecimal originalIncome = "INCOME".equalsIgnoreCase(originalBill.getType()) ? originalBill.getAmount() : BigDecimal.ZERO;
        BigDecimal originalExpenditure = "EXPENDITURE".equalsIgnoreCase(originalBill.getType()) ? originalBill.getAmount() : BigDecimal.ZERO;
        BigDecimal updatedIncome = "INCOME".equalsIgnoreCase(updatedBill.getType()) ? updatedBill.getAmount() : BigDecimal.ZERO;
        BigDecimal updatedExpenditure = "EXPENDITURE".equalsIgnoreCase(updatedBill.getType()) ? updatedBill.getAmount() : BigDecimal.ZERO;

        BigDecimal incomeDiff = updatedIncome.subtract(originalIncome);
        BigDecimal expenditureDiff = updatedExpenditure.subtract(originalExpenditure);
        Integer numberDiff = 0;

        ledgerInfoService.updateLedgerStatisticsWithIncrement(originalBill.getLedgerInfoId(), incomeDiff, expenditureDiff, numberDiff);
    }
}