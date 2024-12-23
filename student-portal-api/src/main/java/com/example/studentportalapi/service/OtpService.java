package com.example.studentportalapi.service;

import com.example.studentportalapi.model.Student;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    private static final long OTP_EXPIRY_DURATION = 5 * 60 * 1000; // 5 minutes in milliseconds

    private final StudentService studentService;
    private final MailService mailService;

    public OtpService(StudentService studentService, MailService mailService) {
        this.studentService = studentService;
        this.mailService = mailService;
    }


    public boolean validateOtp(String email, String otp) {
        Optional<Student> studentOptional = studentService.findStudentByEmail(email);

        if (studentOptional.isEmpty()) {
            logger.warn("Student not found for email: {}", email);
            return false;
        }

        Student student = studentOptional.get();

        // Check OTP and expiry
        if (student.getOtpCode() == null || student.getOtpExpiry() == null) {
            logger.warn("No OTP found or OTP expired for email: {}", email);
            return false;
        }

        if (System.currentTimeMillis() > student.getOtpExpiry() || !student.getOtpCode().equals(otp)) {
            logger.warn("Invalid or expired OTP for email: {}", email);
            return false;
        }

        // Clear OTP after validation
        student.setOtpCode(null);
        student.setOtpExpiry(null);
        studentService.registerStudent(student);

        logger.info("Successfully validated OTP for email: {}", email);
        return true;
    }

    public boolean sendOtpToEmail(String email) throws IOException, MessagingException {
        Optional<Student> studentOptional = studentService.findStudentByEmail(email);

        if (studentOptional.isEmpty()) {
            logger.warn("Student not found for email: {}", email);
            return false;
        }

        Student student = studentOptional.get();

        // Generate OTP and set expiry
        String otp = String.valueOf((int) (Math.random() * 9000) + 1000); // 4-digit OTP
        long expiryTimestamp = System.currentTimeMillis() + OTP_EXPIRY_DURATION;

        student.setOtpCode(otp);
        student.setOtpExpiry(expiryTimestamp);
        studentService.registerStudent(student);

        logger.info("Generated OTP for email: {} - OTP: {}", email, otp);

        // Send OTP via email
        mailService.sendOtpEmail(email, "Your OTP Code", otp);
        return true;
    }

}
