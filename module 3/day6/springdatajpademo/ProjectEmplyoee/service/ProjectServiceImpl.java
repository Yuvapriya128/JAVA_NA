package org.example.springdatajpademo.ProjectEmplyoee.service;

import org.example.springdatajpademo.ProjectEmplyoee.exceptions.EmpNotFound;
import org.example.springdatajpademo.ProjectEmplyoee.exceptions.ProjectNotFound;
import org.example.springdatajpademo.ProjectEmplyoee.model.Employee;
import org.example.springdatajpademo.ProjectEmplyoee.model.Project;
import org.example.springdatajpademo.ProjectEmplyoee.repository.EmployeeRepo;
import org.example.springdatajpademo.ProjectEmplyoee.repository.ProjectRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Override
    public Project saveProject(Project project) {
        return projectRepo.save(project);
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    @Override
    public Project getProjectById(Integer id) {
        return projectRepo.findById(id).orElseThrow(()-> new ProjectNotFound("Project is not found"));
    }

    @Override
    public Project updateProject(Integer id, Project project) {
        return null;
//        Project existingProject = projectRepo.findById(id).get();
//        existingProject.setName(project.getName());
//        existingProject.setEmployeeList(project.getEmployeeList());
//        return projectRepo.save(existingProject);
    }

    @Override
    public void deleteProject(Integer id) {
        projectRepo.deleteById(id);
    }

    @Override
    public void assignMore(int pid, long eid) {
        Project temp=projectRepo.findById(pid).orElseThrow(()-> new ProjectNotFound("Project not found"));
        Employee tempe=employeeRepo.findById(eid).orElseThrow(()-> new EmpNotFound("Employee not found"));

        temp.getEmployeeList().add(tempe);
        projectRepo.save(temp);


    }

    @Override
    public void unassign(int pid, long eid){
        Project projecttemp =projectRepo.findById(pid).orElseThrow(()-> new ProjectNotFound("Project not found"));
        Employee tempe=employeeRepo.findById(eid).orElseThrow(()-> new EmpNotFound("Employee not found"));
        projecttemp.getEmployeeList().remove(tempe);
        projectRepo.save(projecttemp);
    }

    @Override
    public List<Employee> findEmpByProjectName(String name){
       Project project= projectRepo.findByName(name);
       if(project==null){
           throw new ProjectNotFound("Project Name not found");
       }
       return project.getEmployeeList();
    }

    @Override
    public List<Project> findProjectByEmpId(long id){
        Employee employee=employeeRepo.findById(id).orElseThrow(()-> new EmpNotFound("Employee not found"));

        return employee.getProject();
    }
}