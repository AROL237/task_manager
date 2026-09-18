package com.cova.core.entities.repo;

import com.cova.core.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepo extends  JpaSpecificationExecutor<Task> ,JpaRepository<Task,Long> {

    Task findTaskById(String id);
}
