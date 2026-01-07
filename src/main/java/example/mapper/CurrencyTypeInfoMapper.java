package example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import example.entity.CurrencyTypeInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CurrencyTypeInfoMapper extends BaseMapper<CurrencyTypeInfo> {
}