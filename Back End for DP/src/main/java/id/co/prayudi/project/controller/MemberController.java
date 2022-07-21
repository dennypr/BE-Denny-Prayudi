/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.prayudi.project.controller;

import id.co.prayudi.project.component.EmployeeMember;
import id.co.prayudi.project.component.ProjectMember;
import id.co.prayudi.project.service.MemberService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Denny
 */

@RestController
@RequestMapping("/v1")
public class MemberController {
    
    @Autowired
    private MemberService OctomobileService;
    
    @PostMapping("/employee/create")
    public EmployeeMember placeEmployeeController(@Valid @RequestBody EmployeeMember request){
       return OctomobileService.placeEmployee(request);        
    }      
    
    @GetMapping("/employee/filter")   
    public List<ProjectMember> findAllProjectMemberStreamController(){
       return OctomobileService.findAllProjectMemberStream();
    }
    
    @GetMapping("/employee/sumproject")   
    public List<List> findSumProjectController(){
       return OctomobileService.findSumProjectService();
    }
    
    @GetMapping("/employee")   
    public List<EmployeeMember> findAllEmployeeController(){
       return OctomobileService.findAllEmployee();
    }
    
    @PutMapping("/employee/update")   
    public Optional<EmployeeMember> updateEmployeeController(@Valid @RequestBody EmployeeMember request){
       return OctomobileService.updateEmployee(request);  
    }
    
    @DeleteMapping("/employee/delete")   
    public void deleteEmployeeController(@Valid @RequestBody EmployeeMember request){
       OctomobileService.deleteEmployee(request);      
    }
    
    @GetMapping("/employee/{id}")    
    public Optional<EmployeeMember> getEmployeeByIdController(@PathVariable(value="id") Integer id){
       return OctomobileService.getEmployeeById(id);
    }
}
