package com.luv2code.web.jdbc;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Resource(name="jdbc/web_student_tracker")
	private DataSource dataSource;

	private StudentDbUtil studentDbUtil;
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		// create out student db util ... and pass in the conn pool / datasource
		try {
			studentDbUtil = new StudentDbUtil(dataSource);
		} catch (Exception e) {
			// TODO: handle exception
			throw new ServletException(e);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			// read the "command" parameter
			String theCommand = request.getParameter("command");
			
			if (theCommand == null) {
				theCommand = "LIST";
			}
			
			switch(theCommand) {
			case "LIST":
				listStudents(request, response);
				break;
			case "LOAD":
				loadStudent(request, response);
				break;
			case "UPDATE":
				updateStudent(request, response);
				break;
			case "DELETE":
				deleteStudent(request, response);
				break;
            case "SEARCH":
                searchStudents(request, response);
                break;
			default:
				listStudents(request, response);
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
	

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// read the "command" parameter
			String theCommand = request.getParameter("command");
			
			switch(theCommand) {
			case "ADD":
				addStudent(request, response);
				break;
			default:
				listStudents(request, response);
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
    private void searchStudents(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // read search name from form data
        String theSearchName = request.getParameter("theSearchName");
        
        // search students from db util
        List<Student> students = studentDbUtil.searchStudents(theSearchName);
        
        // add students to the request
        request.setAttribute("STUDENT_LIST", students);
                
        // send to JSP page (view)
        RequestDispatcher dispatcher = request.getRequestDispatcher("/list-students.jsp");
        dispatcher.forward(request, response);
    }

	private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// get student id from request
		Integer studentId = Integer.parseInt(request.getParameter("studentId"));
		
		// execute StudentDbUtil method
		studentDbUtil.deleteStudent(studentId);
		
		// return the user to list of students
		listStudents(request, response);
	}

	private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// read student info from request
		int id = Integer.parseInt(request.getParameter("studentId"));
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		// create a new student object
		Student student = new Student(id, firstName, lastName, email);
		
		// perform update on database
		studentDbUtil.updateStudent(student);
		
		// send them back to the "list students" page
		listStudents(request, response);
	}

	private void loadStudent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		
		// read student id from request
		String theStudentId = request.getParameter("studentId");
		
		// get student from database
		Student theStudent = studentDbUtil.getStudent(theStudentId);
		
		// place student in the request
		request.setAttribute("THE_STUDENT", theStudent);
		
		// send to jsp page: update-student-form.jsp
		RequestDispatcher dispatcher = request.getRequestDispatcher("update-student-form.jsp");
		dispatcher.forward(request, response);
	}

	private void addStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// read student info from form data
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		// create a new student object
		Student theStudent = new Student(firstName, lastName, email);
		
		// add the student to the database
		studentDbUtil.addStudent(theStudent);
		
		// send back to main page (the student list)
        // SEND AS REDIRECT to avoid multiple-browser reload issue
        response.sendRedirect(request.getContextPath() + "/StudentControllerServlet?command=LIST");
	}

	private void listStudents(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// get students from db utils
		List<Student> students = studentDbUtil.getStudents();
		
		// add students to the request
		request.setAttribute("STUDENT_LIST", students);
		
		// send to JSP page (view)\
		RequestDispatcher rd = request.getRequestDispatcher("/list-students.jsp");
		rd.forward(request, response);
	}

}
