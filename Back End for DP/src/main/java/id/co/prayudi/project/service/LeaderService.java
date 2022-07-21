/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.prayudi.project.service;

import id.co.prayudi.project.component.EmployeeLeader;
import id.co.prayudi.project.component.ProjectLeader;
import id.co.prayudi.project.repository.employeeLeaderRepository;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import id.co.prayudi.project.repository.projectLeaderRepository;
import java.util.stream.Collectors;


/**
 *
 * @author Denny
 */

@Service
public class LeaderService {
    
    @Autowired
    private employeeLeaderRepository EmployeeLeaderRepository;
    
    @Autowired
    private projectLeaderRepository ProjectLeaderRepository;   
    
    public EmployeeLeader placeEmployeeLeader(@Valid @RequestBody EmployeeLeader request){
       return EmployeeLeaderRepository.save(request);        
    }    
    
    public List<EmployeeLeader> findAllEmployeeLeader(){
       return EmployeeLeaderRepository.findAll();
    }
    
    public List<ProjectLeader> findAllEmployeeStream(){
        List<ProjectLeader> productsList = ProjectLeaderRepository.findAll();
        List<ProjectLeader> filtered =
                    productsList
                        .stream()
                        .filter(p -> p.getProjectName().startsWith("B"))
                        .collect(Collectors.toList());
        return filtered;     
    }
    
     public List<List> findSumEmployeeLeaderService(){
        return ProjectLeaderRepository.findSumEmployeeLeader();
    }
    public Optional<EmployeeLeader> updateEmployeeLeader(@Valid @RequestBody EmployeeLeader request){
       EmployeeLeader emp = new EmployeeLeader();
       emp.setId(request.getId());
       emp.setNameLeader(request.getNameLeader());
       emp.setEmailLeader(request.getEmailLeader());
       emp.setGenderLeader(request.getGenderLeader());
       emp.setPhoneLeader(request.getPhoneLeader());
       List<ProjectLeader> getProjects = ProjectLeaderRepository.findByIdEmployeeLeader(request.getId());
       emp.setProjectLeader(getProjects);
       EmployeeLeaderRepository.save(emp); 
       int i=0;
       for(ProjectLeader st : getProjects) {            
          ProjectLeader project = new ProjectLeader();
          project.setId(st.getId());
          project.setProjectName(request.getProjectLeader().get(i).getProjectName());
          project.setProjectLocation(request.getProjectLeader().get(i).getProjectLocation());
          project.setProjectTools(request.getProjectLeader().get(i).getProjectTools());
          project.setProjectYear(request.getProjectLeader().get(i).getProjectYear());
          ProjectLeaderRepository.save(project);   
          i++;
       }     
       return EmployeeLeaderRepository.findById(request.getId());  
    }
    
    public void deleteEmployeeLeader(@Valid @RequestBody EmployeeLeader request){
       EmployeeLeaderRepository.deleteById(request.getId());      
    }
       
    public Optional<EmployeeLeader> getEmployeeByIdLeader(@PathVariable(value="id") Integer id){
       return EmployeeLeaderRepository.findById(id);
    }
}
