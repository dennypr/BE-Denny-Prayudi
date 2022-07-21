/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.prayudi.project.repository;

import id.co.prayudi.project.component.ProjectMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Denny
 */

public interface projectMemberRepository extends JpaRepository<ProjectMember, Integer> {
   @Query(value =" Select * from project where id_employee = ?1 ",nativeQuery = true)
   public List<ProjectMember> findByIdEmployee(Integer id_employee); 
   
   @Query(value =" delete from project where id_employee = ?1 ",nativeQuery = true)
   public Integer deleteProject(Integer id_employee); 
   
   @Query(value ="Select a.name, a.email, count(b.id) from employee a inner join project b on a.id=b.id_employee"+
           " group by  a.name, a.email ",nativeQuery = true)
   public List<List> findSumEmployeeMember(); 
   
}
