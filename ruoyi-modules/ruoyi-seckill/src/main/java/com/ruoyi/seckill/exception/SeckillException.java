package com.ruoyi.seckill.exception;

import com.ruoyi.common.core.exception.base.BaseException;
import com.ruoyi.common.core.web.domain.CodeMsg;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: zhangJiang
 * @Date: 2023/1/3
 * @Time： 9:21
 * @Version： 1.0.0
 */
public class SeckillException extends BaseException {

    private CodeMsg codeMsg;

    public SeckillException(String defaultMessage) {
        super(defaultMessage);
    }

    public SeckillException(CodeMsg codeMsg){
        this.codeMsg = codeMsg;
    }
}
