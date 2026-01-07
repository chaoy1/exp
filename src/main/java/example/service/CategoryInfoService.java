package example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import example.entity.CategoryInfo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CategoryInfoService extends IService<CategoryInfo> {

    /**
     * 分页查询分类
     * @param current 当前页
     * @param size 每页大小
     * @param name 分类名称 (模糊查询)
     * @param type 分类类型 (精确查询)
     * @return 分页结果
     */
    IPage<CategoryInfo> getCategoryPage(int current, int size, String name, String type);

    /**
     * 新增分类
     * @param categoryInfo 分类信息
     * @return 操作结果信息
     */
    String addCategory(CategoryInfo categoryInfo);

    /**
     * 编辑分类
     * @param categoryInfo 分类信息
     * @return 操作结果信息
     */
    String updateCategory(CategoryInfo categoryInfo);

    /**
     * 删除分类
     * @param id 分类ID
     * @return 操作结果信息
     */
    String deleteCategory(Long id);

    /**
     * 根据ID查询分类详情
     * @param id 分类ID
     * @return 分类信息
     */
    CategoryInfo getCategoryById(Long id);
}