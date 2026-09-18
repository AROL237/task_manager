package com.cova.core.controller;

import com.cova.core.Constants;
import com.cova.core.INTERFACE.TaskService;
import com.cova.core.dto.ApiResponse;
import com.cova.core.dto.CreateTaskDto;
import com.cova.core.dto.Filter;
import com.cova.core.dto.TaskDto;
import com.cova.core.entities.Task;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/tasks")
@Slf4j
public class TaskController {

    @Autowired
    private TaskService taskService;


    @PostMapping
    public ResponseEntity<ApiResponse<Task>> addTask(@RequestBody @Valid CreateTaskDto dto) {
        log.info("addTask dto {}", dto);
        Task task = taskService.createTask(dto);
        ApiResponse<Task> out = new ApiResponse<>(HttpStatus.OK.value(), Constants.SUCCESS, true, LocalDateTime.now(), task);
        return ResponseEntity.status(HttpStatus.OK).body(out);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Task>> updateTask(@PathVariable() Long id, @Valid @RequestBody CreateTaskDto dto) {
        log.info("updateTask dto {}  body: {}", id, dto);
        Task task = taskService.updateTask(id, dto);
        ApiResponse<Task> out = new ApiResponse<>(HttpStatus.OK.value(), Constants.SUCCESS, true, LocalDateTime.now(), task);
        return ResponseEntity.status(HttpStatus.OK).body(out);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<PageImpl<TaskDto>>> getTask(
            @RequestParam(name = "search") String search,
            Pageable pageable
    ) {
        log.info("getTask search_key= {}  pageable  {}", search, pageable);
        Filter searchDto = null;



        try {
            ObjectMapper mapper = new ObjectMapper();
            searchDto = mapper.readValue(search, Filter.class);
            log.info("searchDto {}", searchDto);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }

        PageImpl<TaskDto> taskPage = taskService.getTask(searchDto, pageable);

        ApiResponse<PageImpl<TaskDto>> out = new ApiResponse<>(HttpStatus.OK.value(), Constants.SUCCESS, true, LocalDateTime.now(), taskPage);
        return ResponseEntity.status(HttpStatus.OK).body(out);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteTask(@PathVariable() long id) {
        log.info("delete task id {}", id);

        taskService.deleteTask(id);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(HttpStatus.OK.value(), Constants.SUCCESS, true, LocalDateTime.now(), null));
    }
}
