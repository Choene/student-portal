package com.example.studentportalapi.service;

import com.example.studentportalapi.model.Student;
import com.example.studentportalapi.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public Student registerStudent(Student student) {
        // Save new student
        return studentRepository.save(student);
    }

    public Optional<Student> findStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    public Optional<Student> login(String email, String password) {
        // Check if student exists
        Optional<Student> student = studentRepository.findByEmail(email);

        // Compare provided password (plain text for now; will add BCrypt later)
        if (student.isPresent() && student.get().getPassword().equals(password)) {
            return student;
        }
        return Optional.empty();
    }
}
