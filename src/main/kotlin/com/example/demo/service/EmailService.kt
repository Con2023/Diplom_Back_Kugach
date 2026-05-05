package com.example.demo.service

import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    @Value("\${spring.mail.username}") private val fromEmail: String,
) {
    fun sendPasswordResetCode(
        toEmail: String,
        code: String,
    ) {
        val subject = "Код для сброса пароля"
        val htmlContent = "<h2>Ваш код для сброса пароля</h2><p style='font-size: 24px;'>$code</p>"
        try {
            val message: MimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")
            helper.setFrom(fromEmail)
            helper.setTo(toEmail)
            helper.setSubject(subject)
            helper.setText(htmlContent, true)
            mailSender.send(message)
        } catch (e: MessagingException) {
            throw RuntimeException("Ошибка при отправке письма", e)
        }
    }
}
