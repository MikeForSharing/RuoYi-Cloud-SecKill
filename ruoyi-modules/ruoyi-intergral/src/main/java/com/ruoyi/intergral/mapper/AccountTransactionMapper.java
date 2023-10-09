package com.ruoyi.intergral.mapper;
import com.ruoyi.intergral.api.model.AccountTransaction;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: Mike
 */
public interface AccountTransactionMapper {
    /**
     * 插入事务日志,用于幂等性控制
     * @param accountTransaction
     */
    void insert(AccountTransaction accountTransaction);

    /**
     * 获取之前阶段提交的事务日志对象
     * @param txId
     * @param actionId
     * @return
     */
    AccountTransaction get(@Param("txId") String txId, @Param("actionId") Long actionId);

    /**
     * 更新事务日志状态
     * @param txId
     * @param actionId
     * @param changeState
     * @param checkState
     * @return
     */
    int updateAccountTransactionState(@Param("txId") String txId, @Param("actionId") Long actionId, @Param("changeState") int changeState, @Param("checkState") int checkState);
}
