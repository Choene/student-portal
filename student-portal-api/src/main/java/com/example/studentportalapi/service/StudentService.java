package com.example.studentportalapi.service;

import com.example.studentportalapi.model.Student;
import com.example.studentportalapi.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Student registerStudent(Student student) {
        return studentRepository.save(student);
    }

    public Optional<Student> findStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    public Optional<Student> login(String email, String password) {
        Optional<Student> student = studentRepository.findByEmail(email);

        if (student.isPresent() && passwordEncoder.matches(password, student.get().getPassword())) {
            return student;
        }
        return Optional.empty();
    }

    public String generateAndSendOtp(Student student) {
        // Generate a 6-digit OTP
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000); 
        student.setOtpCode(otp);
        // OTP valid for 5 minutes
        student.setOtpExpiry(System.currentTimeMillis() + 5 * 60 * 1000);
        studentRepository.save(student);

        // Simulate sending OTP via email (TOTO: replace with actual email sending logic)
        System.out.println("Sending OTP: " + otp + " to " + student.getEmail());

        return otp;
    }

    public boolean verifyOtp(Student student, String otp) {
        if (student.getOtpCode() != null && student.getOtpCode().equals(otp) &&
                student.getOtpExpiry() != null && student.getOtpExpiry() > System.currentTimeMillis()) {
            // Clear OTP after successful verification
            student.setOtpCode(null); 
            student.setOtpExpiry(null);
            studentRepository.save(student);
            return true;
        }
        return false;
    }

}
