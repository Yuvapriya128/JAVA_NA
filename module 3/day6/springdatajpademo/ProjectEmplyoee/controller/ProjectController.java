package org.example.springdatajpademo.ProjectEmplyoee.controller;

import jakarta.validation.Valid;
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

    @GetMapping
    public ResponseEntity<List<Project>> findAll(){
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> findByid(@PathVariable Integer id){
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PostMapping
    public ResponseEntity<Project> addProject(@Valid @RequestBody Project project){
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
    public ResponseEntity<List<Employee>> findEmpByPro(@PathVariable String pname){
        return ResponseEntity.ok(projectService.findEmpByProjectName(pname));
    }

    @GetMapping("/findproject/{eid}")
    public ResponseEntity<List<Project>> findEmpByPro(@PathVariable long eid){
        return ResponseEntity.ok(projectService.findProjectByEmpId(eid));
    }




//    For 1 exception : can do it like this

//    @ExceptionHandler(EmpNotFound.class)
//    public ResponseEntity handler1(){
//        return  ResponseEntity.notFound().build();
//    }
//
}
