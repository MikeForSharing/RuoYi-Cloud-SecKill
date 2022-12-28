package com.ruoyi.rabbit.service.imp;

import com.ruoyi.rabbit.enums.DelayTypeEnum;
import com.ruoyi.rabbit.mapper.RabbitMapper;
import com.ruoyi.rabbit.producer.DelayMessageProducer;
import com.ruoyi.rabbit.service.RabbitService;
import com.ruoyi.system.api.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class RabbitServiceImpl implements RabbitService {

    @Resource
    private RabbitMapper rabbitMapper;

    @Resource
    private JavaMailSender jms;

    //读取配置文件邮箱账号参数
    @Value("${spring.mail.username}")
    private String sender;

    @Resource
    private DelayMessageProducer producer;

    /**
     * 判定帐户是否激活
     * @param code 用户激活码
     * @return 激活结果
     */
    @Override
    public Boolean isActive(String code) {
        List<String> userIdList = rabbitMapper.selectUserIdByEmailCode(code);
        return userIdList.size() != 0 ? true : false;
    }

    /**
     * 通过用户ID删除用户
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserById(Long userId)
    {
        return rabbitMapper.deleteUserById(userId);
    }

    /**
     * 更新邮件激活码
     *
     * @param emailCode 邮件激活码
     * @return 保存结果
     */
    @Override
    public int updateEmailCode(String emailCode, Long userId) {
        int rows = rabbitMapper.updateEmailCode(emailCode, userId);
        return rows;
    }

    /**
     * 激活账户
     *
     * @param userId 用户id
     * @return 更新结果
     */
    @Override
    public int activeUser(Long userId) {
        return rabbitMapper.updateUserStatusByUserId(userId);
    }

    /**
     * 以html的格式发送邮件
     * @param user 用户
     * @return  邮件激活码
     */
    @Override
    public String sendMailHtml(SysUser user) {
        String code = UUID.randomUUID().toString().replace("-", "");
        MimeMessage mimeMessage = jms.createMimeMessage();
        try {
            // 建立邮件消息
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            // 发送者
            messageHelper.setFrom(sender);
            // 接收者
            messageHelper.setTo(user.getEmail());
            // 发送的标题
            messageHelper.setSubject("邮箱激活验证");
            // 发送的内容
            String msg = "<p>您好，感谢您注册账户！</p>" +
                    "<p><h1>此邮件为官方激活邮件！请点击下面链接完成激活操作！</h1><h3><a href='http://localhost:9204/rabbitmq/activeAccount?activeCode=" + code + "&userId="+user.getUserId()+"'>点击此处</a></h3></p>" +
                    "<p>如果不是本人操作，请忽略。</p>";
            messageHelper.setText(msg, true);
            // 发送邮件
            jms.send(mimeMessage);
            log.info("已发送的邮件激活码是：" + code);
        } catch (Exception e) {
            log.error("发送邮件失败");
            System.out.println(e);
            return "-1";
        }
        return code;
    }

    /**
     * 向消息队列发消息
     * @param message 消息体
     * @param delayType 延迟类型
     */
    @Override
    public void sendMQ(String message, Integer delayType) {
        producer.send(message, Objects.requireNonNull(DelayTypeEnum.getDelayTypeEnum(delayType)));

    }
}
