package com.ruoyi.rabbit.service;

import com.ruoyi.system.api.domain.SysUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface RabbitService {
    /**
     * 判定帐户是否激活
     *
     * @param code 用户激活码
     * @return 激活结果
     */
    public Boolean isActive(String code);

    /**
     * 更新邮件激活码
     *
     * @param emailCode 邮件激活码
     * @return 保存结果
     */
    public int updateEmailCode(String emailCode,Long userId);

    /**
     * 激活账户
     *
     * @param userId 用户id
     * @return 保存结果
     */
    public int activeUser(Long userId);

    /**
     * 以html的格式发送邮件
     * @param user 用户
     * @return  邮件激活码
     */
    public String sendMailHtml(SysUser user);

    /**
     * 以html的格式发送邮件
     * @param message 消息内容
     * @param delayType 延迟时间类型
     * @return  邮件激活码
     */
    public void sendMQ(String message,Integer delayType);

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteUserById(Long userId);
}
