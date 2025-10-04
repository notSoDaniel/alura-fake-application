package br.com.alura.AluraFake.user.controller;

import br.com.alura.AluraFake.user.dto.InstructorReportDTO;
import br.com.alura.AluraFake.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instructor")
public class InstructorController {

    private final UserService userService;

    public InstructorController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<InstructorReportDTO> getInstructorReport(@PathVariable Long id) {
        InstructorReportDTO report = userService.generateInstructorReport(id);
        return ResponseEntity.ok(report);
    }
}