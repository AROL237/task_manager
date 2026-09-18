package com.cova.core.entities.repo;

import com.cova.core.entities.Task;
import com.cova.core.entities.User;
import org.springframework.data.jpa.domain.Specification;


public class TaskSpec {


    public static Specification<Task> ownedBy(String ownerEmail) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.lower(root.get("user").get("email")), ownerEmail.toLowerCase());
    }

    public static Specification<Task> hasTitle(String title) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Task> hastStatus(Boolean  status) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Task> hasDescription(String description) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),   "%"+description.toLowerCase() + "%");
    }

    public static Specification<Task> hasId(Long id) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), id);
    }

}
