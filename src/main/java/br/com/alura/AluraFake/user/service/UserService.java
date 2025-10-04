package br.com.alura.AluraFake.user.service;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.dto.CourseReportItemDTO;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.user.dto.InstructorReportDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public UserService(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public InstructorReportDTO generateInstructorReport(Long instructorId) {
        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + instructorId));

        if (user.getRole() != Role.INSTRUCTOR) {
            throw new IllegalArgumentException("User with id " + instructorId + " is not an instructor.");
        }

        List<Course> courses = courseRepository.findByInstructor(user);

        List<CourseReportItemDTO> courseReportItems = courses.stream()
                .map(CourseReportItemDTO::new)
                .collect(Collectors.toList());

        long totalPublishedCourses = courses.stream()
                .filter(course -> course.getStatus() == Status.PUBLISHED)
                .count();

        return new InstructorReportDTO(courseReportItems, totalPublishedCourses);
    }
}