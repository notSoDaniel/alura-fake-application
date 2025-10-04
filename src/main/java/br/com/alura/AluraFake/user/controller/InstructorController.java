package br.com.alura.AluraFake.user.controller;

import br.com.alura.AluraFake.user.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instructor")
public class InstructorController {

    private final UserService userService;

    public InstructorController(UserService userService) {
        this.userService = userService;
    }

}