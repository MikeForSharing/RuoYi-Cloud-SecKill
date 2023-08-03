package com.ruoyi.rabbit.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RabbitMapper {
    /**
     * 通过邮箱激活码查询用户
     *
     * @param code 邮箱激活码
     * @return 用户ID
     */
    public List<String> selectUserIdByEmailCode(String code);

    /**
     * 根据用户ID更新用户状态
     *
     * @param userId 用户ID
     * @return 用户名
     */
    public int updateUserStatusByUserId(Long userId);

    /**
     * 更新邮件激活码
     *
     * @param emailCode 邮件激活码
     * @param userId    用户ID
     * @return 保存结果
     */
    public int updateEmailCode(@Param("emailCode" ) String emailCode, @Param("userId" ) Long userId);

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteUserById(Long userId);
}
