package example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import example.entity.LedgerInfo;
import example.service.LedgerInfoService;
import example.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ledger_info")
public class LedgerInfoController {

    @Autowired
    private LedgerInfoService ledgerInfoService;

    // 新增账本
    @PostMapping("/add")
    public Result<String> addLedger(@RequestBody LedgerInfo ledgerInfo) {
        try {
            ledgerInfoService.addLedger(ledgerInfo);
            return Result.success("账本新增成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    // 编辑账本
    @PutMapping("/update")
    public Result<String> updateLedger(@RequestBody LedgerInfo ledgerInfo) {
        try {
            ledgerInfoService.updateLedger(ledgerInfo);
            return Result.success("账本更新成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    // 分页查询账本
    @GetMapping("/page_query")
    public Result<Page<LedgerInfo>> getLedgerPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String createdBy // 可选的按创建人筛选
    ) {
        Page<LedgerInfo> pageData = ledgerInfoService.getLedgerPage(current, size, name, createdBy);
        return Result.success(pageData);
    }

    // 获取账本列表
    @GetMapping("/list")
    public Result<List<LedgerInfo>> getLedgerList(
            @RequestParam(required = false) String createdBy // 可选的按创建人筛选
    ) {
        List<LedgerInfo> list = ledgerInfoService.getLedgerList(createdBy);
        return Result.success(list);
    }

    // 获取账本详情
    @GetMapping("/query/{id}")
    public Result<LedgerInfo> getLedgerById(@PathVariable Long id) {
        LedgerInfo ledgerInfo = ledgerInfoService.getLedgerById(id);
        if (ledgerInfo != null) {
            return Result.success(ledgerInfo);
        } else {
            return Result.fail("账本不存在");
        }
    }

    // 删除账本
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteLedger(@PathVariable Long id) {
        try {
            ledgerInfoService.deleteLedger(id);
            return Result.success("账本删除成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}