package com.ruoyi.intergral.constant;

import com.ruoyi.common.core.web.domain.CodeMsg;

/**
 * @Author: zhangJiang
 */


public class IntergralCodeMsg extends CodeMsg {
    private IntergralCodeMsg(Integer code, String msg){
        super(code,msg);
    }
    public static final IntergralCodeMsg OP_INTERGRAL_ERROR = new IntergralCodeMsg(500601,"操作积分失败");
    public static final IntergralCodeMsg INTERGRAL_NOT_ENOUGH = new IntergralCodeMsg(500602,"积分余额不足");
}
