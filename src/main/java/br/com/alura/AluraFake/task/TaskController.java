package br.com.alura.AluraFake.task; // Mantendo o pacote original do seu arquivo

import br.com.alura.AluraFake.task.dto.NewMultipleChoiceTaskRequest;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskRequest;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskRequest;
import br.com.alura.AluraFake.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/task/new/opentext")
    public ResponseEntity<Void> newOpenTextExercise(@RequestBody @Valid NewOpenTextTaskRequest dto) {
        taskService.createOpenTextTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity<Void> newSingleChoice(@RequestBody @Valid NewSingleChoiceTaskRequest dto) {
        taskService.createSingleChoiceTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity<Void> newMultipleChoice(@RequestBody @Valid NewMultipleChoiceTaskRequest dto) {
        taskService.createMultipleChoiceTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}