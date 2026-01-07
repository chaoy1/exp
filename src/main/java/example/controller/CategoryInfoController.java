package example.controller;

import example.common.Result;
import example.entity.CategoryInfo;
import example.service.CategoryInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category_info")
public class CategoryInfoController {

    @Autowired
    private CategoryInfoService categoryInfoService;

    // 分页查询接口
    @GetMapping("/page_query")
    public Result<IPage<CategoryInfo>> getCategoryPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type) {
        IPage<CategoryInfo> pageData = categoryInfoService.getCategoryPage(current, size, name, type);
        // 返回成功结果，包含分页数据
        return Result.success(pageData);
    }

    // 新增分类接口
    @PostMapping("/add")
    public Result<String> addCategory(@RequestBody CategoryInfo categoryInfo) {
        String result = categoryInfoService.addCategory(categoryInfo);
        if (result.startsWith("新增成功")) {
            return Result.success(result);
        } else {
            return Result.fail(result);
        }
    }

    // 编辑分类接口
    @PutMapping("/update")
    public Result<String> updateCategory(@RequestBody CategoryInfo categoryInfo) {
        String result = categoryInfoService.updateCategory(categoryInfo);
        if (result.startsWith("更新成功")) {
            return Result.success(result);
        } else {
            return Result.fail(result);
        }
    }

    // 删除分类接口
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        String result = categoryInfoService.deleteCategory(id);
        if (result.startsWith("删除成功")) {
            return Result.success(result);
        } else {
            return Result.fail(result);
        }
    }

    // 查询分类详情接口
    @GetMapping("/query/{id}")
    public Result<CategoryInfo> getCategoryDetail(@PathVariable Long id) {
        CategoryInfo categoryInfo = categoryInfoService.getCategoryById(id);
        if (categoryInfo != null) {
            // 如果查询到详情，返回成功结果，包含分类信息
            return Result.success(categoryInfo);
        } else {
            // 如果未查询到，可以返回失败结果
            return Result.fail("未找到ID为 [" + id + "] 的分类信息");
        }
    }
}