package example.controller;

import example.common.Result; // 确保导入路径正确
import example.entity.CurrencyTypeInfo;
import example.service.CurrencyTypeInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/currency_type_info") // 使用 /currencyType 作为基础路径
public class CurrencyTypeInfoController {

    @Autowired
    private CurrencyTypeInfoService currencyTypeInfoService;

    // 分页查询接口
    @GetMapping("/page_query")
    public Result<IPage<CurrencyTypeInfo>> getCurrencyTypePage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) { // 支持按名称模糊查询
        IPage<CurrencyTypeInfo> pageData = currencyTypeInfoService.getCurrencyTypePage(current, size, name);
        return Result.success(pageData);
    }

    // 新增币种接口
    @PostMapping("/add")
    public Result<String> addCurrencyType(@RequestBody CurrencyTypeInfo currencyTypeInfo) {
        String result = currencyTypeInfoService.addCurrencyType(currencyTypeInfo);
        if (result.startsWith("新增成功")) {
            return Result.success(result);
        } else {
            return Result.fail(result);
        }
    }

    // 编辑币种接口
    @PutMapping("/update")
    public Result<String> updateCurrencyType(@RequestBody CurrencyTypeInfo currencyTypeInfo) {
        String result = currencyTypeInfoService.updateCurrencyType(currencyTypeInfo);
        if (result.startsWith("更新成功")) {
            return Result.success(result);
        } else {
            return Result.fail(result);
        }
    }

    // 删除币种接口
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteCurrencyType(@PathVariable Long id) {
        String result = currencyTypeInfoService.deleteCurrencyType(id);
        if (result.startsWith("删除成功")) {
            return Result.success(result);
        } else {
            return Result.fail(result);
        }
    }

    // 查询币种详情接口
    @GetMapping("/query/{id}")
    public Result<CurrencyTypeInfo> getCurrencyTypeDetail(@PathVariable Long id) {
        CurrencyTypeInfo currencyTypeInfo = currencyTypeInfoService.getCurrencyTypeById(id);
        if (currencyTypeInfo != null) {
            return Result.success(currencyTypeInfo);
        } else {
            return Result.fail("未找到ID为 [" + id + "] 的币种类型信息");
        }
    }
}