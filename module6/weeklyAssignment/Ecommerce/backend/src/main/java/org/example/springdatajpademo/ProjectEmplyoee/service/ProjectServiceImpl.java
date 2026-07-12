package org.example.springdatajpademo.ProjectEmplyoee.service;

import org.example.springdatajpademo.ProjectEmplyoee.dto.EmployeeResponseDTO;
import org.example.springdatajpademo.ProjectEmplyoee.dto.ProjectRequestDTO;
import org.example.springdatajpademo.ProjectEmplyoee.dto.ProjectResponseDTO;
import org.example.springdatajpademo.ProjectEmplyoee.dto.ProjectUpdateDTO;
import org.example.springdatajpademo.ProjectEmplyoee.exceptions.EmpNotFound;
import org.example.springdatajpademo.ProjectEmplyoee.exceptions.ProjectNotFound;
import org.example.springdatajpademo.ProjectEmplyoee.model.Employee;
import org.example.springdatajpademo.ProjectEmplyoee.model.Project;
import org.example.springdatajpademo.ProjectEmplyoee.repository.EmployeeRepo;
import org.example.springdatajpademo.ProjectEmplyoee.repository.ProjectRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    private Project mapToEntity(ProjectRequestDTO projectRequestDTO){
        Project project=new Project();
        project.setName(projectRequestDTO.getName());
        return project;
    }
    private ProjectResponseDTO mapToResponse(Project project){
        return new ProjectResponseDTO(project.getId(), project.getName());
    }


    @Override
    public ProjectResponseDTO saveProject(ProjectRequestDTO project) {
        return mapToResponse(projectRepo.save(mapToEntity(project)));
    }

    @Override
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepo.findAll().stream().map((e)->mapToResponse(e)).toList();
    }

    @Override
    public ProjectResponseDTO getProjectById(Integer id) {
        Project p= projectRepo.findById(id).orElseThrow(()-> new ProjectNotFound("Project is not found"));
        return mapToResponse(p);
    }

    @Override
    public ProjectResponseDTO updateProject(Integer id, ProjectUpdateDTO project) {

        Project existingProject = projectRepo.findById(id).orElseThrow(()->new EmpNotFound("Employee not found"));

        existingProject.setName(project.getName());

        return mapToResponse(projectRepo.save(existingProject));
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
    public List<EmployeeResponseDTO> findEmpByProjectName(String name){
       Project project= projectRepo.findByName(name);
       if(project==null){
           throw new ProjectNotFound("Project Name not found");
       }

       return project.getEmployeeList().stream()
               .map((e)->new EmployeeResponseDTO(
                       e.getId(),e.getName(),e.getDesignation(),e.getDepartment(),e.getEmail()
               ))
               .toList();
    }

    @Override
    public List<ProjectResponseDTO> findProjectByEmpId(long id){
        Employee employee=employeeRepo.findById(id).orElseThrow(()-> new EmpNotFound("Employee not found"));

        return employee.getProject().stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<ProjectResponseDTO> getAllSorted() {
        return projectRepo.findAll(
                Sort.by("name")
        ).stream().map((p)->mapToResponse(p)).toList();
    }


    @Override
    public List<ProjectResponseDTO> getAllByPage(int pageno, int pagesize) {
        return projectRepo.findAll(
                PageRequest.of(pageno,pagesize)
        ).stream().map((p)->mapToResponse(p)).toList();
    }
}