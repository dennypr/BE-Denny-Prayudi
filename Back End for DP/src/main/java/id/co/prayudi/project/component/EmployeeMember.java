/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.prayudi.project.component;

/**
 *
 * @author Denny
 */

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 *
 * @author Denny
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "employee")
public class EmployeeMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     
    private int id;     
    private String name; 
    private String email;     
    private String gender;    
    private String phone;  
    @OneToMany(targetEntity = ProjectMember.class, cascade = CascadeType.ALL)
    @JoinColumn(name="id_employee", referencedColumnName = "id")
    private List<ProjectMember> project;
  

}
