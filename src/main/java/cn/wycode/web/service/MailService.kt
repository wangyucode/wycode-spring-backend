package cn.wycode.web.service

import org.apache.commons.logging.LogFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class MailService(private val mailSender: JavaMailSender) {

    private val log = LogFactory.getLog(this.javaClass)

    @Async
    fun sendSimpleMail(sendTo: String, subject: String, content: String) {
        val message = SimpleMailMessage()
        message.setFrom("wangyu@wycode.cn")
        message.setTo(sendTo)
        message.setSubject(subject)
        message.setText(content)
        try {
            mailSender.send(message)
        } catch (e: Exception) {
            log.error("邮件发送失败！", e)
        }
    }
}