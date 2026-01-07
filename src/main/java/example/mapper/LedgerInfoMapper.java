package example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import example.entity.LedgerInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LedgerInfoMapper extends BaseMapper<LedgerInfo> {
}