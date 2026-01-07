package example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import example.entity.CurrencyTypeInfo;
import example.mapper.CurrencyTypeInfoMapper;
import example.mapper.LedgerInfoMapper;
import example.service.CurrencyTypeInfoService;
import example.mapper.CurrencyTypeInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CurrencyTypeInfoServiceImpl extends ServiceImpl<CurrencyTypeInfoMapper, CurrencyTypeInfo> implements CurrencyTypeInfoService {
    @Autowired
    private CurrencyTypeInfoMapper currencyTypeInfoMapper;

    @Autowired
    private LedgerInfoMapper ledgerInfoMapper;

    @Override
    public IPage<CurrencyTypeInfo> getCurrencyTypePage(int current, int size, String name) {
        Page<CurrencyTypeInfo> page = new Page<>(current, size);

        LambdaQueryWrapper<CurrencyTypeInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like(CurrencyTypeInfo::getName, name); // 模糊查询名称
        }
        queryWrapper.orderByDesc(CurrencyTypeInfo::getId); // 按ID降序排列

        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务
    public String addCurrencyType(CurrencyTypeInfo currencyTypeInfo) {
        // 1. 检查名称是否重复
        LambdaQueryWrapper<CurrencyTypeInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurrencyTypeInfo::getName, currencyTypeInfo.getName());
        if (this.count(queryWrapper) > 0) {
            return "新增失败：币种名称 [" + currencyTypeInfo.getName() + "] 已存在！";
        }

        // 2. 设置创建时间等字段
        currencyTypeInfo.setCreatedTime(LocalDateTime.now());
        currencyTypeInfo.setCreatedTime(LocalDateTime.now());

        // 3. 保存到数据库
        boolean saveResult = this.save(currencyTypeInfo);
        if (saveResult) {
            return "新增成功";
        } else {
            return "新增失败：数据库保存错误";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务
    public String updateCurrencyType(CurrencyTypeInfo currencyTypeInfo) {
        // 1. 检查要更新的币种是否存在
        CurrencyTypeInfo existingCurrency = this.getById(currencyTypeInfo.getId());
        if (existingCurrency == null) {
            return "更新失败：ID为 [" + currencyTypeInfo.getId() + "] 的币种不存在！";
        }

        // 2. 检查新名称是否与其他币种重复 (排除当前更新的记录)
        LambdaQueryWrapper<CurrencyTypeInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurrencyTypeInfo::getName, currencyTypeInfo.getName())
                .ne(CurrencyTypeInfo::getId, currencyTypeInfo.getId()); // 排除自身
        if (this.count(queryWrapper) > 0) {
            return "更新失败：币种名称 [" + currencyTypeInfo.getName() + "] 已存在！";
        }

        // 3. 设置更新时间
        currencyTypeInfo.setCreatedTime(LocalDateTime.now());

        // 4. 更新数据库
        boolean updateResult = this.updateById(currencyTypeInfo);
        if (updateResult) {
            return "更新成功";
        } else {
            return "更新失败：数据库更新错误";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务
    public String deleteCurrencyType(Long id) {
        // 1. 检查币种是否存在
        CurrencyTypeInfo existingCurrency = this.getById(id);
        if (existingCurrency == null) {
            return "删除失败：ID为 [" + id + "] 的币种不存在！";
        }

        // 2. 检查币种是否被其他表使用 (例如账本表)
        if (checkIfUsed(id)) {
            return "删除失败：该币种正在被使用，无法删除！";
        }

        // 3. 删除币种
        boolean removeResult = this.removeById(id);
        if (removeResult) {
            return "删除成功";
        } else {
            return "删除失败：数据库删除错误";
        }
    }

    @Override
    public CurrencyTypeInfo getCurrencyTypeById(Long id) {
        return this.getById(id);
    }

    // 辅助方法：检查币种是否被使用
    // 先根据 ID 获取 CurrencyTypeInfo 的 name，然后去 LedgerInfo 表中查找
    private boolean checkIfUsed(Long currencyTypeId) {
        // 首先根据 ID 获取 CurrencyTypeInfo 对象，以获取其 name
        CurrencyTypeInfo currencyType = this.getById(currencyTypeId);
        if (currencyType == null) {
            System.out.println("警告：在 checkIfUsed 中找不到 ID 为 [" + currencyTypeId + "] 的币种信息！");
            return false;
        }

        String currencyTypeName = currencyType.getName();

        // 创建查询条件，检查 ledger_info 表中是否有记录的 currency_type 字段等于该币种的名称
        LambdaQueryWrapper<example.entity.LedgerInfo> ledgerQueryWrapper = new LambdaQueryWrapper<>();
        ledgerQueryWrapper.eq(example.entity.LedgerInfo::getCurrencyType, currencyTypeName);

        // 执行查询，如果 count > 0，则说明该币种名称正在被某个账本使用
        long count = ledgerInfoMapper.selectCount(ledgerQueryWrapper);
        return count > 0;
    }
}