package org.example.springdatajpademo.ProjectEmplyoee.controller;

import jakarta.validation.Valid;
import org.example.springdatajpademo.ProjectEmplyoee.dto.EmployeeResponseDTO;
import org.example.springdatajpademo.ProjectEmplyoee.dto.ProjectRequestDTO;
import org.example.springdatajpademo.ProjectEmplyoee.dto.ProjectResponseDTO;
import org.example.springdatajpademo.ProjectEmplyoee.dto.ProjectUpdateDTO;
import org.example.springdatajpademo.ProjectEmplyoee.model.Employee;
import org.example.springdatajpademo.ProjectEmplyoee.model.Project;
import org.example.springdatajpademo.ProjectEmplyoee.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mtm/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @GetMapping("/sorted")
    public ResponseEntity<List<ProjectResponseDTO>> getAllSorted() {
        return ResponseEntity.ok(projectService.getAllSorted());
    }
    @GetMapping("/page/{pno}/{psize}")
    public ResponseEntity<List<ProjectResponseDTO>> getAllByPage(@PathVariable int pno,@PathVariable int psize) {
        return ResponseEntity.ok(projectService.getAllByPage(pno,psize));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> findAll(){
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> findByid(@PathVariable Integer id){
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> addProject(@Valid @RequestBody ProjectRequestDTO project){
        return ResponseEntity.status(201).body(projectService.saveProject(project));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id){
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/assign/{pid}/{eid}")
    public ResponseEntity assignMore(@PathVariable int pid,@PathVariable int eid){
        projectService.assignMore(pid,eid);

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/unassign/{pid}/{eid}")
    public ResponseEntity unassign(@PathVariable int pid,@PathVariable int eid){
        projectService.unassign(pid,eid);

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/findemp/{pname}")
    public ResponseEntity<List<EmployeeResponseDTO>> findEmpByPro(@PathVariable String pname){
        return ResponseEntity.ok(projectService.findEmpByProjectName(pname));
    }

    @GetMapping("/findproject/{eid}")
    public ResponseEntity<List<ProjectResponseDTO>> findEmpByPro(@PathVariable long eid){
        return ResponseEntity.ok(projectService.findProjectByEmpId(eid));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> update(@PathVariable int pid, @Valid @RequestBody ProjectUpdateDTO projectUpdateDTO){
        return ResponseEntity.status(201).body(projectService.updateProject(pid,projectUpdateDTO));
    }




//    For 1 exception : can do it like this

//    @ExceptionHandler(EmpNotFound.class)
//    public ResponseEntity handler1(){
//        return  ResponseEntity.notFound().build();
//    }
//
}
