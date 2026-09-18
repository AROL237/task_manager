package com.cova.core.INTERFACE;

import com.cova.core.dto.CreateTaskDto;
import com.cova.core.dto.Filter;
import com.cova.core.dto.TaskDto;
import com.cova.core.entities.Task;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    Task createTask(CreateTaskDto task);

    Task getTaskById(Long id);

    Task updateTask(Long id, CreateTaskDto dto);

    void deleteTask(Long id);
    PageImpl<TaskDto> getTask(Filter search, Pageable pageable);

}
