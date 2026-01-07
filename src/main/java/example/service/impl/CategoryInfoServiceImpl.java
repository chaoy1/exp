package example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import example.entity.CategoryInfo;
import example.mapper.BillInfoMapper;
import example.mapper.CategoryInfoMapper;
import example.service.CategoryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CategoryInfoServiceImpl extends ServiceImpl<CategoryInfoMapper, CategoryInfo> implements CategoryInfoService {

    @Autowired
    private CategoryInfoMapper categoryInfoMapper;

    @Autowired
    private BillInfoMapper billInfoMapper;

    @Override
    public IPage<CategoryInfo> getCategoryPage(int current, int size, String name, String type) {
        Page<CategoryInfo> page = new Page<>(current, size);

        LambdaQueryWrapper<CategoryInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.trim().isEmpty()) {
            // 模糊查询分类名
            queryWrapper.like(CategoryInfo::getName, name);
        }
        if (type != null && !type.trim().isEmpty()) {
            // 精确查询分类类型
            queryWrapper.eq(CategoryInfo::getType, type);
        }
        queryWrapper.orderByDesc(CategoryInfo::getId);

        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addCategory(CategoryInfo categoryInfo) {
        // 1. 检查名称是否重复
        LambdaQueryWrapper<CategoryInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CategoryInfo::getName, categoryInfo.getName());
        if (this.count(queryWrapper) > 0) {
            return "新增失败：分类名称 [" + categoryInfo.getName() + "] 已存在！";
        }

        // 2. 设置创建时间等字段
        categoryInfo.setCreatedTime(LocalDateTime.now());

        // 3. 保存到数据库
        boolean saveResult = this.save(categoryInfo);
        if (saveResult) {
            return "新增成功";
        } else {
            return "新增失败：数据库保存错误";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateCategory(CategoryInfo categoryInfo) {
        // 1. 检查要更新的分类是否存在
        CategoryInfo existingCategory = this.getById(categoryInfo.getId());
        if (existingCategory == null) {
            return "更新失败：ID为 [" + categoryInfo.getId() + "] 的分类不存在！";
        }

        // 2. 检查新名称是否与其他分类重复 (排除当前更新的记录)
        LambdaQueryWrapper<CategoryInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CategoryInfo::getName, categoryInfo.getName())
                .ne(CategoryInfo::getId, categoryInfo.getId());
        if (this.count(queryWrapper) > 0) {
            return "更新失败：分类名称 [" + categoryInfo.getName() + "] 已存在！";
        }

        // 3. 设置更新时间
        categoryInfo.setCreatedTime(LocalDateTime.now());

        // 4. 更新数据库
        boolean updateResult = this.updateById(categoryInfo);
        if (updateResult) {
            return "更新成功";
        } else {
            return "更新失败：数据库更新错误";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteCategory(Long id) {
        // 1. 检查分类是否存在
        CategoryInfo existingCategory = this.getById(id);
        if (existingCategory == null) {
            return "删除失败：ID为 [" + id + "] 的分类不存在！";
        }

        // 2. 检查分类是否被其他表（如账单表）使用
        if (checkIfUsed(id)) {
            return "删除失败：该分类正在被使用，无法删除！";
        }

        // 3. 删除分类
        boolean removeResult = this.removeById(id);
        if (removeResult) {
            return "删除成功";
        } else {
            return "删除失败：数据库删除错误";
        }
    }

    @Override
    public CategoryInfo getCategoryById(Long id) {
        // 直接通过ID查询详情
        return this.getById(id);
    }

    // 辅助方法：检查分类是否被使用
    // 根据提供的 BillInfo 实体，其关联分类的字段名为 'category'
    private boolean checkIfUsed(Long categoryId) {
        // 创建查询条件，检查 bill_info 表中是否有记录的 category 字段等于传入的 categoryId
        LambdaQueryWrapper<example.entity.BillInfo> billQueryWrapper = new LambdaQueryWrapper<>();
        billQueryWrapper.eq(example.entity.BillInfo::getCategory, categoryId); // 使用 BillInfo 的 getCategory() 方法

        // 执行查询，如果 count > 0，则说明该分类正在被使用
        long count = billInfoMapper.selectCount(billQueryWrapper);
        return count > 0;
    }
}