package com.codX.pos.service.impl;

import com.codX.pos.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendPasswordResetOtp(String email, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "AurionX POS System");
            helper.setTo(email);
            helper.setSubject("Password Reset OTP - AurionX POS");

            String htmlContent = buildPasswordResetEmailContent(otp);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Password reset OTP sent successfully to: {}", email);

        } catch (Exception e) {
            log.error("Failed to send password reset OTP to: {}", email, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendWelcomeEmail(String email, String firstName, String temporaryPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "AurionX POS System");
            helper.setTo(email);
            helper.setSubject("Welcome to AurionX POS - Account Created");

            String htmlContent = buildWelcomeEmailContent(firstName, temporaryPassword);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", email);

        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", email, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildPasswordResetEmailContent(String otp) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Password Reset OTP</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                        <h1 style="color: white; margin: 0; font-size: 28px;">AurionX POS</h1>
                        <p style="color: white; margin: 10px 0 0 0; font-size: 16px;">Password Reset Request</p>
                    </div>
                    
                    <div style="background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px; border: 1px solid #e9ecef;">
                        <h2 style="color: #495057; margin-top: 0;">Reset Your Password</h2>
                        
                        <p>You have requested to reset your password for your AurionX POS account.</p>
                        
                        <div style="background: white; padding: 20px; border-radius: 8px; text-align: center; margin: 20px 0; border: 2px dashed #667eea;">
                            <h3 style="margin: 0; color: #495057;">Your OTP Code</h3>
                            <div style="font-size: 32px; font-weight: bold; color: #667eea; letter-spacing: 8px; margin: 15px 0;">%s</div>
                            <p style="margin: 0; color: #6c757d; font-size: 14px;">This code expires in 10 minutes</p>
                        </div>
                        
                        <div style="background: #fff3cd; border: 1px solid #ffeaa7; border-radius: 6px; padding: 15px; margin: 20px 0;">
                            <p style="margin: 0; color: #856404;"><strong>Security Notice:</strong></p>
                            <ul style="margin: 10px 0 0 0; color: #856404;">
                                <li>Never share this OTP with anyone</li>
                                <li>AurionX staff will never ask for your OTP</li>
                                <li>If you didn't request this, please ignore this email</li>
                            </ul>
                        </div>
                        
                        <p style="margin-top: 30px; color: #6c757d; font-size: 14px;">
                            If you have any questions, please contact our support team.
                        </p>
                        
                        <hr style="border: none; border-top: 1px solid #e9ecef; margin: 30px 0;">
                        
                        <div style="text-align: center; color: #6c757d; font-size: 12px;">
                            <p>© 2024 AurionX POS System. All rights reserved.</p>
                            <p>This is an automated message, please do not reply to this email.</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """, otp);
    }

    private String buildWelcomeEmailContent(String firstName, String temporaryPassword) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Welcome to AurionX POS</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                        <h1 style="color: white; margin: 0; font-size: 28px;">Welcome to AurionX POS!</h1>
                        <p style="color: white; margin: 10px 0 0 0; font-size: 16px;">Your account has been created</p>
                    </div>
                    
                    <div style="background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px; border: 1px solid #e9ecef;">
                        <h2 style="color: #495057; margin-top: 0;">Hello %s!</h2>
                        
                        <p>Your account has been successfully created in the AurionX POS system.</p>
                        
                        <div style="background: white; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #667eea;">
                            <h3 style="margin: 0 0 15px 0; color: #495057;">Your Login Credentials</h3>
                            <p style="margin: 5px 0;"><strong>Temporary Password:</strong> <code style="background: #f8f9fa; padding: 4px 8px; border-radius: 4px; font-family: monospace;">%s</code></p>
                        </div>
                        
                        <div style="background: #d4edda; border: 1px solid #c3e6cb; border-radius: 6px; padding: 15px; margin: 20px 0;">
                            <p style="margin: 0; color: #155724;"><strong>Important:</strong> Please change your password after your first login for security purposes.</p>
                        </div>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="http://localhost:3000/login" style="background: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 6px; display: inline-block; font-weight: bold;">Login to Your Account</a>
                        </div>
                        
                        <p style="margin-top: 30px; color: #6c757d; font-size: 14px;">
                            If you have any questions, please contact your system administrator.
                        </p>
                        
                        <hr style="border: none; border-top: 1px solid #e9ecef; margin: 30px 0;">
                        
                        <div style="text-align: center; color: #6c757d; font-size: 12px;">
                            <p>© 2024 AurionX POS System. All rights reserved.</p>
                            <p>This is an automated message, please do not reply to this email.</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """, firstName, temporaryPassword);
    }
}
