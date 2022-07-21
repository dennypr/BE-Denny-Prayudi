/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.prayudi.project.controller;

import id.co.prayudi.project.component.EmployeeLeader;
import id.co.prayudi.project.component.ProjectLeader;
import id.co.prayudi.project.service.LeaderService;
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
public class LeaderController {
    
    @Autowired
    private LeaderService OctomobileLeaderService;
    
    @PostMapping("/leader/create")
    public EmployeeLeader placeEmployeeLeaderController(@Valid @RequestBody EmployeeLeader request){
       return OctomobileLeaderService.placeEmployeeLeader(request);        
    }      
    
    @GetMapping("/leader/filter")   
    public List<ProjectLeader> findAllEmployeeLeaderStreamController(){
       return OctomobileLeaderService.findAllEmployeeStream();
    }
    
    @GetMapping("/leader/sumproject")   
    public List<List> findSumProjectLeaderController(){
       return OctomobileLeaderService.findSumEmployeeLeaderService();
    }
    
    @GetMapping("/leader")   
    public List<EmployeeLeader> findAllEmployeeLeaderController(){
       return OctomobileLeaderService.findAllEmployeeLeader();
    }
    
    @PutMapping("/leader/update")   
    public Optional<EmployeeLeader> updateEmployeeLeaderController(@Valid @RequestBody EmployeeLeader request){
       return OctomobileLeaderService.updateEmployeeLeader(request);  
    }
    
    @DeleteMapping("/leader/delete")   
    public void deleteEmployeeLeaderController(@Valid @RequestBody EmployeeLeader request){
       OctomobileLeaderService.deleteEmployeeLeader(request);      
    }
    
    @GetMapping("/leader/{id}")    
    public Optional<EmployeeLeader> getEmployeeByIdLeaderController(@PathVariable(value="id") Integer id){
       return OctomobileLeaderService.getEmployeeByIdLeader(id);
    }
}
