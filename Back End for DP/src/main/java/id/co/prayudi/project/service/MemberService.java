/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.prayudi.project.service;

import id.co.prayudi.project.component.EmployeeMember;
import id.co.prayudi.project.component.ProjectMember;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import id.co.prayudi.project.repository.employeeMemberRepository;
import id.co.prayudi.project.repository.projectMemberRepository;
import java.util.stream.Collectors;

/**
 *
 * @author Denny
 */

@Service
public class MemberService {
    
    @Autowired
    private employeeMemberRepository EmployeeRepository;
    
    @Autowired
    private projectMemberRepository ProjectRepository;   
    
    public EmployeeMember placeEmployee(@Valid @RequestBody EmployeeMember request){
       return EmployeeRepository.save(request);        
    }    
    
    public List<EmployeeMember> findAllEmployee(){
       return EmployeeRepository.findAll();
    }
    
    public List<ProjectMember> findAllProjectMemberStream(){
        List<ProjectMember> productsList = ProjectRepository.findAll();
        List<ProjectMember> filtered =
                    productsList
                        .stream()
                        .filter(p -> p.getProjectName().startsWith("B"))
                        .collect(Collectors.toList());
        return filtered;     
    }
    
    public List<List> findSumProjectService(){
        return ProjectRepository.findSumEmployeeMember();
    }
    public Optional<EmployeeMember> updateEmployee(@Valid @RequestBody EmployeeMember request){
       EmployeeMember emp = new EmployeeMember();
       emp.setId(request.getId());
       emp.setName(request.getName());
       emp.setEmail(request.getEmail());
       emp.setGender(request.getGender());
       emp.setPhone(request.getPhone());
       List<ProjectMember> getProject = ProjectRepository.findByIdEmployee(request.getId());
       emp.setProject(getProject);
       EmployeeRepository.save(emp); 
       int i=0;
       for(ProjectMember st : getProject) {            
          ProjectMember project = new ProjectMember();
          project.setId(st.getId());
          project.setProjectName(request.getProject().get(i).getProjectName());
          project.setLocation(request.getProject().get(i).getLocation());
          project.setTools(request.getProject().get(i).getTools());
          project.setYear(request.getProject().get(i).getYear());
          ProjectRepository.save(project);   
          i++;
       }     
       return EmployeeRepository.findById(request.getId());  
    }
    
    public void deleteEmployee(@Valid @RequestBody EmployeeMember request){
       EmployeeRepository.deleteById(request.getId());      
    }
       
    public Optional<EmployeeMember> getEmployeeById(@PathVariable(value="id") Integer id){
       return EmployeeRepository.findById(id);
    }
}
