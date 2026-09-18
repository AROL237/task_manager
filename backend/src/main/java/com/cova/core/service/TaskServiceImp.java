package com.cova.core.service;

import com.cova.core.INTERFACE.TaskService;
import com.cova.core.INTERFACE.UserService;
import com.cova.core.dto.CreateTaskDto;
import com.cova.core.dto.Filter;
import com.cova.core.dto.TaskDto;
import com.cova.core.entities.Task;
import com.cova.core.entities.User;
import com.cova.core.entities.repo.TaskRepo;
import com.cova.core.entities.repo.TaskSpec;
import com.cova.core.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImp implements TaskService {


    private final UserService userService;
    private final TaskRepo taskRepo;


    @Override
    public Task createTask(CreateTaskDto dto) {
        log.info("creating task {}", dto);
        User user = currentUser();
        try {
            Task task = new Task();
            task.setUser(user);
            task.setTitle(dto.title().trim());
            task.setStatus(dto.status());

            if (dto.description() != null)
                task.setDescription(dto.description().trim());

            return taskRepo.save(task);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Task getTaskById(Long id) {
        log.info("searching task by id {}", id);
        User user = currentUser();
        Specification<Task> spec = Specification.unrestricted();
        if (id != null && id > 0) {
            spec = spec.and(TaskSpec.hasId(id));
            spec = spec.and(TaskSpec.ownedBy(user.getEmail()));
        } else
            throw new ApiException(HttpStatus.BAD_REQUEST, "id is null");
        try {

            return taskRepo.findBy(spec, FluentQuery.FetchableFluentQuery::firstValue);
        } catch (NoSuchElementException e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Task not found id" + id);
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request ID id null");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Task updateTask(Long id, CreateTaskDto dto) {
        User user = currentUser();
        log.info("updating task id: {}  body: {}", id, dto);
        Specification<Task> spec = Specification.where(TaskSpec.ownedBy(user.getEmail()));

        if (id != null && id > 0) {
            spec = spec.and(TaskSpec.hasId(id));
        } else
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request ID id null");

        if (!taskRepo.exists(spec))
            throw new ApiException(HttpStatus.NOT_FOUND, "Task not found id" + id);

        try {
            Task task = this.getTaskById(id);
            task.setTitle(dto.title().trim());
            task.setStatus(dto.status());
            if (dto.description() != null)
                task.setDescription(dto.description().trim());
            return taskRepo.save(task);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTask(Long id) {

        User user = currentUser();
        Specification<Task> spec = Specification.unrestricted();
        if (id != null && id > 0) {
            spec = spec.and(TaskSpec.hasId(id));
            spec = spec.and(TaskSpec.ownedBy(user.getEmail()));
        } else
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request ID id null");


        log.info("deleting task {}", id);
        try {

            if (taskRepo.exists(spec)) {
                taskRepo.deleteById(id);
            } else {
                throw new ApiException(HttpStatus.NOT_FOUND, "Task doesn't exist id:" + id);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageImpl<TaskDto> getTask(Filter search, Pageable pageable) {

        User user = currentUser();

        Specification<Task> spec = Specification.where(TaskSpec.ownedBy(user.getUsername()));
        try {
            if (search != null) {
//                if (search.id() != null && !search.id().isBlank())
                if (search.id() != null)
                    spec = spec.and(TaskSpec.hasId(search.id()));
//                if (search.status() != null && !search.status().isBlank())
                if (search.status() != null)
                    spec = spec.and(TaskSpec.hastStatus(search.status()));
                if (search.title() != null && !search.title().isEmpty())
                    spec = spec.and(TaskSpec.hasTitle(search.title()));
                if (search.description() != null && !search.description().isEmpty())
                    spec = spec.and(TaskSpec.hasDescription(search.description()));
            }

            Page<Task> tasks = taskRepo.findAll(spec, pageable);
//
            List<TaskDto> dos = tasks.getContent().stream().map(t->new TaskDto().apply(t)).toList();

            return new PageImpl<>(dos, pageable, tasks.getTotalElements());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static @Nullable User currentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user;
    }
}
