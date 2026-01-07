package example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import example.entity.LedgerInfo;
import example.mapper.BillInfoMapper;
import example.mapper.LedgerInfoMapper;
import example.service.LedgerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class LedgerInfoServiceImpl extends ServiceImpl<LedgerInfoMapper, LedgerInfo> implements LedgerInfoService {

    @Autowired
    private BillInfoMapper billInfoMapper;

    @Override
    @Transactional // 开启事务，确保删除账本和账单的一致性
    public void addLedger(LedgerInfo ledgerInfo) {
        // 1. 检查账本名称是否重复
        LambdaQueryWrapper<LedgerInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LedgerInfo::getName, ledgerInfo.getName());
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("账本名称已存在，请更换名称。");
        }

        // 2. 设置创建时间
        ledgerInfo.setCreatedTime(LocalDateTime.now());

        // 3. 保存账本
        this.save(ledgerInfo);
    }

    @Override
    public void updateLedger(LedgerInfo ledgerInfo) {
        // 1. 获取数据库中原始账本信息
        LedgerInfo originalLedger = this.getById(ledgerInfo.getId());
        if (originalLedger == null) {
            throw new RuntimeException("账本不存在，无法更新。");
        }

        // 2. 检查账本名称是否被修改，如果修改了则检查新名称是否重复
        if (!originalLedger.getName().equals(ledgerInfo.getName())) {
            LambdaQueryWrapper<LedgerInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(LedgerInfo::getName, ledgerInfo.getName())
                    .ne(LedgerInfo::getId, ledgerInfo.getId()); // 排除当前更新的账本
            long count = this.count(queryWrapper);
            if (count > 0) {
                throw new RuntimeException("账本名称已存在，请更换名称。");
            }
        }

        // 3. 检查币种是否被修改 (要求不允许修改币种)
        if (!originalLedger.getCurrencyType().equals(ledgerInfo.getCurrencyType())) {
            throw new RuntimeException("不允许修改账本的币种。");
        }

        // 4. 设置更新时间
        ledgerInfo.setCreatedTime(LocalDateTime.now());

        // 5. 只更新允许修改的字段 (排除 currencyType 和 createdTime)
        LambdaUpdateWrapper<LedgerInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LedgerInfo::getId, ledgerInfo.getId())
                .set(LedgerInfo::getName, ledgerInfo.getName())
                .set(LedgerInfo::getDescription, ledgerInfo.getDescription())
                .set(LedgerInfo::getCreatedTime, LocalDateTime.now())
                .set(LedgerInfo::getNumber, ledgerInfo.getNumber())
                .set(LedgerInfo::getIncome, ledgerInfo.getIncome())
                .set(LedgerInfo::getCreatedBy, ledgerInfo.getCreatedBy());
        // 6. 执行更新
        this.update(updateWrapper);
    }

    @Override
    public Page<LedgerInfo> getLedgerPage(int current, int size, String name, String createdBy) {
        Page<LedgerInfo> page = new Page<>(current, size);

        LambdaQueryWrapper<LedgerInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.trim().isEmpty()) {
            // 模糊查询账本名
            queryWrapper.like(LedgerInfo::getName, name);
        }
        if (createdBy != null && !createdBy.trim().isEmpty()) {
            // 精确查询创建人
            queryWrapper.eq(LedgerInfo::getCreatedBy, createdBy);
        }
        queryWrapper.orderByDesc(LedgerInfo::getCreatedTime); // 按创建时间降序

        return this.page(page, queryWrapper);
    }

    @Override
    public java.util.List<LedgerInfo> getLedgerList(String createdBy) {
        LambdaQueryWrapper<LedgerInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (createdBy != null && !createdBy.trim().isEmpty()) {
            queryWrapper.eq(LedgerInfo::getCreatedBy, createdBy);
        }
        queryWrapper.orderByDesc(LedgerInfo::getCreatedTime); // 按创建时间降序
        return this.list(queryWrapper);
    }

    @Override
    public LedgerInfo getLedgerById(Long id) {
        return this.getById(id);
    }

    @Override
    @Transactional // 开启事务，确保删除账本和账单的一致性
    public void deleteLedger(Long id) {
        // 1. 检查账本是否存在
        LedgerInfo ledger = this.getById(id);
        if (ledger == null) {
            throw new RuntimeException("账本不存在，无法删除。");
        }

        // 2. 删除所有关联的账单信息 (根据 ledgerId)
        billInfoMapper.deleteByLedgerId(id); // 使用 BillInfoMapper 删除

        // 3. 删除账本本身
        this.removeById(id);
    }

    @Override
    public void updateLedgerStatisticsWithIncrement(Long ledgerId, BigDecimal incomeIncrement, BigDecimal expenditureIncrement, Integer numberIncrement) {
        // 使用 update wrapper 原子性地更新统计字段
        LambdaUpdateWrapper<LedgerInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LedgerInfo::getId, ledgerId)
                .setSql("income = income + " + incomeIncrement) // 使用 setSql 来执行加法操作
                .setSql("expenditure = expenditure + " + expenditureIncrement)
                .setSql("number = number + " + numberIncrement)
                .set(LedgerInfo::getCreatedTime, LocalDateTime.now());

        this.update(updateWrapper);
    }
}