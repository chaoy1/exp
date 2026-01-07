package example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import example.entity.CurrencyTypeInfo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CurrencyTypeInfoService extends IService<CurrencyTypeInfo> {

    /**
     * 分页查询币种
     * @param current 当前页
     * @param size 每页大小
     * @param name 币种名称 (模糊查询)
     * @return 分页结果
     */
    IPage<CurrencyTypeInfo> getCurrencyTypePage(int current, int size, String name);

    /**
     * 新增币种
     * @param currencyTypeInfo 币种信息
     * @return 操作结果信息
     */
    String addCurrencyType(CurrencyTypeInfo currencyTypeInfo);

    /**
     * 编辑币种
     * @param currencyTypeInfo 币种信息
     * @return 操作结果信息
     */
    String updateCurrencyType(CurrencyTypeInfo currencyTypeInfo);

    /**
     * 删除币种
     * @param id 币种ID
     * @return 操作结果信息
     */
    String deleteCurrencyType(Long id);

    /**
     * 根据ID查询币种详情
     * @param id 币种ID
     * @return 币种信息
     */
    CurrencyTypeInfo getCurrencyTypeById(Long id);
}