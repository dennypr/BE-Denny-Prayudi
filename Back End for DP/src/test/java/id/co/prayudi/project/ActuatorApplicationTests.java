package id.co.prayudi.project;
import id.co.prayudi.project.component.EmployeeLeader;
import id.co.prayudi.project.component.EmployeeMember;
import id.co.prayudi.project.component.ProjectLeader;
import id.co.prayudi.project.component.ProjectMember;
import java.util.List;
import javax.ws.rs.core.Application;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActuatorApplicationTests {
    @Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;
        
    private String getRootUrl() {
		return "http://localhost:" + port;
	}
        
	@Test
	void contextLoads() {
	}
        
        @Test
	public void testfindAllEmployee() {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/v1/employee",
				HttpMethod.GET, entity, String.class);

		Assert.assertNotNull(response.getBody());
	}

        @Test
	public void testplaceEmployee() {
		EmployeeMember employee = new EmployeeMember();
		employee.setName("octomobile");
		employee.setEmail("octomobile@gmail.com");
		employee.setGender("male");
		employee.setPhone("098764545");	
                ProjectMember project = new ProjectMember();
                project.setProjectName("Migrasi Daabase");
                project.setLocation("Divisi IT");
                project.setTools("J2EE");
                project.setYear("2022");
                employee.setProject((List<ProjectMember>) project);
		ResponseEntity<EmployeeMember> postResponse = restTemplate.postForEntity(getRootUrl() + "/v1/employee/create", employee, EmployeeMember.class);
		Assert.assertNotNull(postResponse);
		Assert.assertNotNull(postResponse.getBody());
	}
        
        @Test
	public void testupdateEmployee() {		
		EmployeeMember employee = restTemplate.getForObject(getRootUrl() + "/v1/employee/update", EmployeeMember.class);
		employee.setId(4);
                employee.setName("octomobile");
		employee.setEmail("octomobile@gmail.com");
		employee.setGender("male");
		employee.setPhone("098764545");	
                ProjectMember project = new ProjectMember();
                project.setProjectName("Migrasi Daabase");
                project.setLocation("Divisi IT");
                project.setTools("J2EE");
                project.setYear("2022");
                employee.setProject((List<ProjectMember>) project);
		restTemplate.put(getRootUrl() + "/v1/employee/update", employee);
		EmployeeMember updatedUser = restTemplate.getForObject(getRootUrl() + "/v1/employee/update", EmployeeMember.class);
		Assert.assertNotNull(updatedUser);
	}
        
        @Test
	public void testgetEmployeeById() {
		EmployeeMember user = restTemplate.getForObject(getRootUrl() + "/v1/employee/4", EmployeeMember.class);
		System.out.println(user.getName());
		Assert.assertNotNull(user);
	}
        
        @Test
	public void testdeleteEmployee() {
		EmployeeMember user = restTemplate.getForObject(getRootUrl() + "/v1/employee/delete", EmployeeMember.class);
		user.setId(4);
		user.setName("octomobile");
		user.setEmail("octomobile@gmail.com");
		user.setGender("male");
		user.setPhone("098764545");	
                Assert.assertNotNull(user);

		restTemplate.delete(getRootUrl() + "/v1/employee/delete" );

		try {
			user = restTemplate.getForObject(getRootUrl() + "/v1/employee/delete", EmployeeMember.class);
		} catch (final HttpClientErrorException e) {
			Assert.assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
		}
	}
               
        @Test
	public void testfindAllEmployeeLeader() {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/v1/leader",
				HttpMethod.GET, entity, String.class);

		Assert.assertNotNull(response.getBody());
	}
        
        @Test
	public void testplaceEmployeeLeader() {
		EmployeeLeader employee = new EmployeeLeader();
		employee.setNameLeader("octomobilexxxzzzz");
		employee.setEmailLeader("octomobilezzxxx@gmail.com");
		employee.setGenderLeader("male");
		employee.setPhoneLeader("098764545");	
                ProjectLeader project = new ProjectLeader();
                project.setProjectName("Migrasi Daabasexxx");
                project.setProjectLocation("Divisi IT xx");
                project.setProjectTools("J2EE");
                project.setProjectYear("2022");
                employee.setProjectLeader((List<ProjectLeader>) project);
		ResponseEntity<EmployeeMember> postResponse = restTemplate.postForEntity(getRootUrl() + "/v1/leader/create", employee, EmployeeMember.class);
		Assert.assertNotNull(postResponse);
		Assert.assertNotNull(postResponse.getBody());
	}
        
        @Test
	public void testupdateEmployeeLeader() {		
		EmployeeLeader employee = restTemplate.getForObject(getRootUrl() + "/v1/leader/update", EmployeeLeader.class);
		employee.setId(10);
                employee.setNameLeader("octomobilexxx");
		employee.setEmailLeader("octomobilexxx@gmail.com");
		employee.setGenderLeader("male");
		employee.setPhoneLeader("098764545");	
                ProjectLeader project = new ProjectLeader();
                project.setProjectName("Migrasi Daabase");
                project.setProjectLocation("Divisi IT");
                project.setProjectTools("J2EE");
                project.setProjectYear("2022");
                employee.setProjectLeader((List<ProjectLeader>) project);
		restTemplate.put(getRootUrl() + "/v1/leader/update", employee);
		EmployeeMember updatedUser = restTemplate.getForObject(getRootUrl() + "/v1/leader/update", EmployeeMember.class);
		Assert.assertNotNull(updatedUser);
	}
        
        
        @Test
	public void testgetEmployeeByIdLeader() {
		EmployeeLeader user = restTemplate.getForObject(getRootUrl() + "/v1/leader/4", EmployeeLeader.class);
		System.out.println(user.getNameLeader());
		Assert.assertNotNull(user);
	}
        
        @Test
	public void testdeleteEmployeeLeader() {
		EmployeeLeader user = restTemplate.getForObject(getRootUrl() + "/v1/leader/delete", EmployeeLeader.class);
		user.setId(10);
		user.setNameLeader("octomobile");
		user.setEmailLeader("octomobile@gmail.com");
		user.setGenderLeader("male");
		user.setPhoneLeader("098764545");	
                Assert.assertNotNull(user);

		restTemplate.delete(getRootUrl() + "/v1/leader/delete" );

		try {
			user = restTemplate.getForObject(getRootUrl() + "/v1/leader/delete", EmployeeLeader.class);
		} catch (final HttpClientErrorException e) {
			Assert.assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
		}
	}
        
}


