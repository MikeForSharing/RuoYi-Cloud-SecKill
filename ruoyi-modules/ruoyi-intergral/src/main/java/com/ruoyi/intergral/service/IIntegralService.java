package com.ruoyi.intergral.service;

import com.ruoyi.intergral.api.model.OperateIntergralVo;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * @Author: Mike
 */
@LocalTCC
public interface IIntegralService {
    /**
     * 增加积分
     */
    @TwoPhaseBusinessAction(name = "decrIntergralTry", commitMethod = "decrIntergralCommit", rollbackMethod = "decrIntergralRollback")
    void decrIntergralTry(@BusinessActionContextParameter(paramName = "operateIntergralVo") OperateIntergralVo operateIntergralVo, BusinessActionContext context);
    void decrIntergralCommit(BusinessActionContext context);
    void decrIntergralRollback(BusinessActionContext context);
    void decrIntergral(OperateIntergralVo vo);
    void incrIntergral(OperateIntergralVo vo);
}
