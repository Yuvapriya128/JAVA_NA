package org.example.springdatajpademo.ProjectEmplyoee.repository;

import org.example.springdatajpademo.ProjectEmplyoee.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepo  extends JpaRepository<Project,Integer> {
    Project findByName(String name);
}
