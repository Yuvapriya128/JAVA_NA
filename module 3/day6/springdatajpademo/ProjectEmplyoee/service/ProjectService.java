package org.example.springdatajpademo.ProjectEmplyoee.service;

import org.example.springdatajpademo.ProjectEmplyoee.model.Employee;
import org.example.springdatajpademo.ProjectEmplyoee.model.Project;

import java.util.List;

public interface ProjectService {
    Project saveProject(Project project);
    List<Project> getAllProjects();
    Project getProjectById(Integer id);
    Project updateProject(Integer id, Project project);
    void deleteProject(Integer id);

    void assignMore(int pid, long eid);

    void unassign(int pid, long eid);

    List<Employee> findEmpByProjectName(String name);

    List<Project> findProjectByEmpId(long id);
}
