package example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import example.entity.LedgerInfo;
import java.math.BigDecimal;

public interface LedgerInfoService {
    // 新增账本
    void addLedger(LedgerInfo ledgerInfo);

    // 编辑账本 (不允许修改币种)
    void updateLedger(LedgerInfo ledgerInfo);

    // 分页查询账本 (支持名称模糊查询)
    Page<LedgerInfo> getLedgerPage(int current, int size, String name, String createdBy);

    // 获取账本列表 (可选按创建人筛选)
    java.util.List<LedgerInfo> getLedgerList(String createdBy);

    // 获取账本详情
    LedgerInfo getLedgerById(Long id);

    // 删除账本 (及关联账单)
    void deleteLedger(Long id);

    void updateLedgerStatisticsWithIncrement(Long ledgerId, BigDecimal incomeIncrement, BigDecimal expenditureIncrement, Integer numberIncrement);
}