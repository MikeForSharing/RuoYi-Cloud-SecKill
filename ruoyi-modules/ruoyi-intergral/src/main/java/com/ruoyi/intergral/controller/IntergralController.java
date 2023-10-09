package com.ruoyi.intergral.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.intergral.api.model.OperateIntergralVo;
import com.ruoyi.intergral.service.IIntegralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 积分支付
 *
 * @Author: Mike
 */
@RestController
@RequestMapping("/intergral" )
public class IntergralController extends BaseController {


    @Autowired
    private IIntegralService integralService;
    @RequestMapping("/decrIntergral")
    public R<String> decrIntergral(@RequestBody OperateIntergralVo vo){
        integralService.decrIntergralTry(vo,null);
        return R.ok("扣除积分操作成功");
    }

    @RequestMapping("/incrIntergral")
    public R<String> incrIntergral(@RequestBody OperateIntergralVo vo){
        integralService.incrIntergral(vo);
        return R.ok("增加积分操作成功");
    }


    @GetMapping("/test" )
    public R<List<String>> test() {
        return R.ok();
    }
}
