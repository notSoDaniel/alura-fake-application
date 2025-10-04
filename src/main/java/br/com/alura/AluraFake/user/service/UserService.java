package br.com.alura.AluraFake.user.service;

import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public UserService(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

}