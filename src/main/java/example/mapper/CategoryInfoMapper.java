package example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import example.entity.CategoryInfo;

@Mapper
public interface CategoryInfoMapper extends BaseMapper<CategoryInfo> {
}