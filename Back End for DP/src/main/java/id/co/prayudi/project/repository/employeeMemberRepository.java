/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.prayudi.project.repository;

import id.co.prayudi.project.component.EmployeeMember;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Denny
 */
public interface employeeMemberRepository  extends JpaRepository<EmployeeMember, Integer> {
    
}
