package example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import example.entity.BillInfo;

public interface BillInfoService {
    // 添加账单
    void addBill(BillInfo billInfo);

    // 删除账单
    void deleteBill(Long id);

    // 编辑账单 (不允许修改账本)
    void updateBill(BillInfo billInfo);

    // 获取账单详情 (携带账本信息)
    BillInfo getBillDetail(Long id);

    // 分页查询账单 (携带账本信息)
    Page<BillInfo> getBillPage(int current, int size, Long ledgerInfoId, String type, Long category);
}