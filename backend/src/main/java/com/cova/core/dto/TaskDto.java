package com.cova.core.dto;

import com.cova.core.entities.Task;
import com.fasterxml.jackson.annotation.JsonSerializeAs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.internal.Function;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskDto implements Serializable, Function<Task,TaskDto> {

    private Long id;
    private String title;
    private String description;
    private Boolean status;

//    public void   setTask(Task task) {
//        this.id = String.valueOf(task.getId());
//        this.title = task.getTitle();
//        this.description = task.getDescription();
//        this.status = String.valueOf(task.getStatus());
//    }


    @Override
    public TaskDto apply(Task key) {
       return new TaskDto(key.getId(),key.getTitle(), key.getDescription(),key.getStatus());
    }
}
