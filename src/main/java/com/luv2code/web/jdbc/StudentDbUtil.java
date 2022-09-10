package com.luv2code.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDbUtil {
	
	private DataSource dataSource;

	public StudentDbUtil(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}
	
	public List<Student> getStudents() throws Exception {
		
		List<Student> students = new ArrayList<>();
		
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		
		try {
			// get a connection
			myConn = dataSource.getConnection();
			
			// create sql statement
			String sql = "SELECT * FROM Student ORDER BY last_name";
			myStmt = myConn.createStatement();
			
			// execute query
			myRs = myStmt.executeQuery(sql);
			
			// process result set
			while(myRs.next()) {
				
				// retrieve data from result set row
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				
				// create new student object
				Student tempStudent = new Student(id, firstName, lastName, email);
				
				// add it to the list of Students
				students.add(tempStudent);
			}
			
			return students;
		} finally {
			// close JDBC objects
			close(myConn, myStmt, myRs);
		}
	}
	
	private void close(Connection c, Statement s, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			
			if (s != null) {
				s.close();
			}
			
			if(c != null) {
				c.close(); // doesnt really close it ... just puts back in connection pool
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addStudent(Student theStudent) throws SQLException {
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			// get db connection
			myConn = dataSource.getConnection();
			
			// create sql for insert
			String sql = "INSERT INTO student "
					+ "(first_name, last_name, email) "
					+ "values (?, ?, ?)";
			myStmt = myConn.prepareStatement(sql);
			
			// set the param values for the student
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			
			// execute sql insert
			// preferrably, we will use the return value to denote if add is successfully or not
			myStmt.execute();
		} finally {
			// clean up JDBC objects
			close(myConn, myStmt, null);
		}
		
	}

	public Student getStudent(String theStudentId) throws SQLException {
		
		Connection myConn = null;
		ResultSet myRs = null;
		PreparedStatement myStmt = null;
		
		try {
			Student student = null;
			Integer id = Integer.parseInt(theStudentId);
			
			// get db connection
			myConn = dataSource.getConnection();
			
			// create sql statement for fetching student
			String sql  = "SELECT * FROM student WHERE id=?";
			myStmt = myConn.prepareStatement(sql);
			
			// set the param values for the student id
			myStmt.setInt(1, id);
			
			// execute sql insert
			myRs = myStmt.executeQuery();
			
			// read the data from result set
			while(myRs.next()) {
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				student = new Student(id, firstName, lastName, email);
			}
					
			// return the student data
			return student;
		} finally {
			// clean up JDBC objects
			close(myConn, myStmt, myRs);
		}
	}

	public void updateStudent(Student student) throws SQLException {
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			// get db connection
			myConn = dataSource.getConnection();
			
			// create sql query
			String sql = "UPDATE student "
					+ "SET first_name=?, last_name=?, email=? "
					+ "WHERE id=?;";
			myStmt = myConn.prepareStatement(sql);
			myStmt.setString(1, student.getFirstName());
			myStmt.setString(2, student.getLastName());
			myStmt.setString(3, student.getEmail());
			myStmt.setInt(4, student.getId());
			
			// execute sql query
			myStmt.execute();
			
		} finally {		
			// clean JDBC objects
			close(myConn, myStmt, null);
		}

	}

	public void deleteStudent(Integer studentId) throws SQLException {
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			
			// get db connection
			myConn = dataSource.getConnection();
			
			// create sql query
			String sql = "DELETE FROM student WHERE id=?";
			myStmt = myConn.prepareStatement(sql);
			myStmt.setInt(1, studentId);
			
			// execute query
			myStmt.execute();
			
		} finally {
			// clean up jdbc code
			close(myConn, myStmt, null);
		}
		
	}
	
    public List<Student> searchStudents(String theSearchName)  throws Exception {
        List<Student> students = new ArrayList<>();
        
        Connection myConn = null;
        PreparedStatement myStmt = null;
        ResultSet myRs = null;
        int studentId;
        
        try {
            
            // get connection to database
            myConn = dataSource.getConnection();
            
            //
            // only search by name if theSearchName is not empty
            //
            if (theSearchName != null && theSearchName.trim().length() > 0) {
                // create sql to search for students by name
                String sql = "select * from student where lower(first_name) like ? or lower(last_name) like ?";
                // create prepared statement
                myStmt = myConn.prepareStatement(sql);
                // set params
                String theSearchNameLike = "%" + theSearchName.toLowerCase() + "%";
                myStmt.setString(1, theSearchNameLike);
                myStmt.setString(2, theSearchNameLike);
                
            } else {
                // create sql to get all students
                String sql = "select * from student order by last_name";
                // create prepared statement
                myStmt = myConn.prepareStatement(sql);
            }
            
            // execute statement
            myRs = myStmt.executeQuery();
            
            // retrieve data from result set row
            while (myRs.next()) {
                
                // retrieve data from result set row
                int id = myRs.getInt("id");
                String firstName = myRs.getString("first_name");
                String lastName = myRs.getString("last_name");
                String email = myRs.getString("email");
                
                // create new student object
                Student tempStudent = new Student(id, firstName, lastName, email);
                
                // add it to the list of students
                students.add(tempStudent);            
            }
            
            return students;
        }
        finally {
            // clean up JDBC objects
            close(myConn, myStmt, myRs);
        }
    }
}
