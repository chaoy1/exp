package example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import example.entity.BillInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BillInfoMapper extends BaseMapper<BillInfo> {
    // 根据 ledgerId 删除所有关联的账单
    @Delete("DELETE FROM bill_info WHERE ledger_info_id = #{ledgerId}")
    int deleteByLedgerId(@Param("ledgerId") Long ledgerId);


    // 查询账单详情时，连带账本信息
    @Select("SELECT b.*, l.name AS ledgerName, l.currency_type AS ledgerCurrencyType " +
            "FROM bill_info b " +
            "JOIN ledger_info l ON b.ledger_info_id = l.id " +
            "WHERE b.id = #{id}")
    BillInfo selectBillWithLedgerInfo(@Param("id") Long id);

    @Select("SELECT b.*, l.name AS ledgerName, l.currency_type AS ledgerCurrencyType " +
            "FROM bill_info b " +
            "JOIN ledger_info l ON b.ledger_info_id = l.id " +
            "WHERE b.ledger_info_id = #{ledgerId} " +
            "AND (#{type} IS NULL OR b.type = #{type}) " +
            "AND (#{category} IS NULL OR b.category = #{category}) " +
            "ORDER BY b.created_time DESC")
    List<BillInfo> selectBillsWithLedgerInfoByLedger(
            @Param("ledgerId") Long ledgerId,
            @Param("type") String type,
            @Param("category") Long category
    );
}
