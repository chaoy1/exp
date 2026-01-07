package example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import example.entity.BillInfo;
import example.service.BillInfoService;
import example.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bill_info")
public class BillInfoController {

    @Autowired
    private BillInfoService billInfoService;

    // 添加账单
    @PostMapping("/add")
    public Result<String> addBill(@RequestBody BillInfo billInfo) {
        try {
            billInfoService.addBill(billInfo);
            return Result.success("账单添加成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    // 删除账单
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteBill(@PathVariable Long id) {
        try {
            billInfoService.deleteBill(id);
            return Result.success("账单删除成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    // 编辑账单
    @PutMapping("/update")
    public Result<String> updateBill(@RequestBody BillInfo billInfo) {
        try {
            billInfoService.updateBill(billInfo);
            return Result.success("账单更新成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    // 获取账单详情 (携带账本信息)
    @GetMapping("/query/{id}")
    public Result<BillInfo> getBillDetail(@PathVariable Long id) {
        BillInfo billInfo = billInfoService.getBillDetail(id);
        if (billInfo != null) {
            return Result.success(billInfo);
        } else {
            return Result.fail("账单不存在");
        }
    }

    // 分页查询账单 (携带账本信息)
    @GetMapping("/page_query")
    public Result<Page<BillInfo>> getBillPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long ledgerInfoId, // 依赖账本ID查询
            @RequestParam(required = false) String type,       // 收入/支出类型筛选
            @RequestParam(required = false) Long category      // 分类ID筛选
    ) {
        try {
            Page<BillInfo> pageData = billInfoService.getBillPage(current, size, ledgerInfoId, type, category);
            return Result.success(pageData);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}