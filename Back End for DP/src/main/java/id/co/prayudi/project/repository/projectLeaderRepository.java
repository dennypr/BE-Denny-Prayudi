/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.prayudi.project.repository;

import id.co.prayudi.project.component.ProjectLeader;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Denny
 */
public interface projectLeaderRepository extends JpaRepository<ProjectLeader, Integer> {
   @Query(value =" Select * from leader_project where id_employee_leader = ?1 ",nativeQuery = true)
   public List<ProjectLeader> findByIdEmployeeLeader(Integer id_employee); 
   
   @Query(value =" delete from leader_project where id_employee_leader = ?1 ",nativeQuery = true)
   public Integer deleteProjectLeader(Integer id_employee); 
   
   @Query(value ="Select a.name_leader, a.email_leader, count(b.id) from employee_leader a inner join leader_project b on a.id=b.id_employee_leader"+
           " group by  a.name_leader, a.email_leader ",nativeQuery = true)
   public List<List> findSumEmployeeLeader(); 
}
