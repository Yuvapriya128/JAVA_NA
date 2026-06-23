package org.example.springdatajpademo.ProjectEmplyoee.service;

import org.example.springdatajpademo.ProjectEmplyoee.dto.EmployeeResponseDTO;
import org.example.springdatajpademo.ProjectEmplyoee.dto.ProjectRequestDTO;
import org.example.springdatajpademo.ProjectEmplyoee.dto.ProjectResponseDTO;
import org.example.springdatajpademo.ProjectEmplyoee.dto.ProjectUpdateDTO;

import java.util.List;

public interface ProjectService {
    ProjectResponseDTO saveProject(ProjectRequestDTO project);
    List<ProjectResponseDTO> getAllProjects();
    ProjectResponseDTO getProjectById(Integer id);
    ProjectResponseDTO updateProject(Integer id, ProjectUpdateDTO project);
    void deleteProject(Integer id);

    void assignMore(int pid, long eid);

    void unassign(int pid, long eid);

    List<EmployeeResponseDTO> findEmpByProjectName(String name);

    List<ProjectResponseDTO> findProjectByEmpId(long id);

    public List<ProjectResponseDTO> getAllSorted();
    public List<ProjectResponseDTO> getAllByPage(int pageno,int pagesize);

}
