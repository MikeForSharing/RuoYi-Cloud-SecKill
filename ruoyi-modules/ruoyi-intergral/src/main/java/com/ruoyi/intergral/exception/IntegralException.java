package com.ruoyi.intergral.exception;

import com.ruoyi.common.core.exception.base.BaseException;
import com.ruoyi.common.core.web.domain.CodeMsg;

/**
 * @Author: zhangJiang
 */
public class IntegralException extends BaseException {

    private CodeMsg codeMsg;

    public IntegralException(String defaultMessage) {
        super(defaultMessage);
    }

    public IntegralException(CodeMsg codeMsg){
        this.codeMsg = codeMsg;
    }
}
