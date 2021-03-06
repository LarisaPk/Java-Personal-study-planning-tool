//This Class manages database operations associated to the Course Class
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CourseQueries {
	// DB connection details
	private static final String URL = "jdbc:mysql://localhost:3306/studyplan"; // here you need to give your URL of the database (make changes)
	private static final String USERNAME = "javaoo"; // here you need to give your username of the database (make changes)
	private static final String PASSWORD = "qwerty"; // here you need to give your password of the database (make changes)
	
	//Private variables to store connection and prepared statements
	private Connection connection = null;
	private PreparedStatement addCourse = null; 
	private PreparedStatement selectAllCourses = null;
	private PreparedStatement markCompletion = null;
	private PreparedStatement updateSemester = null;
	private PreparedStatement removeCourse = null;
	private PreparedStatement selectByName = null;
	private PreparedStatement selectBySemester = null;
	private PreparedStatement selectByStatus = null;
	
	public CourseQueries()// method contains SQL queries for the  prepared statements defined above
	{
		try
		{
			connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); // Starts a connection to the database
			selectAllCourses = connection.prepareStatement("SELECT * FROM courses"); // Prepares the select query that selects all the courses from the database
			addCourse = connection.prepareStatement("INSERT INTO courses (Name, Semester) VALUES (?,?)"); // Prepares the insert query that adds new course into the database
			markCompletion = connection.prepareStatement("UPDATE courses SET Status = ? WHERE CourseID = ?");// Prepares the update query that changes status of the course completion in the database
			updateSemester = connection.prepareStatement("UPDATE courses SET Semester = ? WHERE CourseID = ?");// Prepares the update query that updates planned semester in the database
			removeCourse = connection.prepareStatement("DELETE FROM courses WHERE CourseID=?"); // Prepares the delete query that removes course from the database
			selectByName = connection.prepareStatement("SELECT * FROM courses WHERE Name LIKE ?"); // Prepares the select query that gets course by name from the database
			selectBySemester = connection.prepareStatement("SELECT * FROM courses WHERE Semester=?"); // Prepares the select query that gets course by semester from the database
			selectByStatus = connection.prepareStatement("SELECT * FROM courses WHERE Status=?"); // Prepares the select query that gets course by status from the database
		}
		catch (SQLException sqlException)
		{
			sqlException.printStackTrace();
			System.exit(1);
		}
	}
	/*
	 * Method that inserts a new Course in the database
	 */
	public void addCourse(String name, String semester) {
		try
		{
			// Setting the values for the question marks '?' in the prepared statement
			addCourse.setString(1, name);
			addCourse.setString(2, semester);
			addCourse.executeUpdate(); 
		}
		catch (SQLException sqlException)
		{
			sqlException.printStackTrace();
		}	
	}
	/*
	 * Method that deletes a Course from the database
	 */
	public void removeCourse(int id) {
		try
		{
			// Setting the value for the question mark '?' in the prepared statement
			removeCourse.setInt(1, id);
			removeCourse.executeUpdate(); 
		}
		catch (SQLException sqlException)
		{
			sqlException.printStackTrace();
		}	
	}
	/*
	 * This method will execute the select query that gets all courses from the database. 
	 * It returns an ArrayList containing Course objects initialized with Course data from each row in the courses table (database)
	 */
	public ArrayList<Course> getAllCourses(){
	
		ArrayList<Course> results = null;
		ResultSet resultSet = null;
		
		try
		{
			resultSet = selectAllCourses.executeQuery(); //Execute the selectAllCourses query. resultSet contains the rows returned by the query
			results = new ArrayList<Course>();
		
			while(resultSet.next()) // for each row returned by the select query...
			{
				// Initialize a new Course object with the row's data. Add the Course object to the results ArrayList
				results.add(new Course(
					resultSet.getInt("CourseID"), // get the value associated to the CourseID column
					resultSet.getString("Name"), // get the value associated to the Name column
					resultSet.getBoolean("Status"), // get the value associated to the Status column
					resultSet.getString("Semester"))); // get the value associated to the Semester column
			}
		} // end try
		catch (SQLException sqlException)
		{
			sqlException.printStackTrace();
		}
		finally
		{
			try
			{
				resultSet.close();
			}
			catch (SQLException sqlException)
			{
				sqlException.printStackTrace();
			}
		} // end finally
		
		return results;
	} // end method getAllCourses
	
	/*
	 * This method will execute the markCompletion query that updates value of the boolean "status" in the database. 
	 */
	protected void updateStatus(boolean status, int id) {
		try {
			markCompletion.setBoolean(1, status);
			markCompletion.setInt(2, id);
			markCompletion.executeUpdate();
		} 
		catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
	}
	/*
	 * This method will execute the updateSemester query that updates value of the planned semester in the database. 
	 */
	protected void updateSemester(String semester, int id) {
		try {
			updateSemester.setString(1, semester);
			updateSemester.setInt(2, id);
			updateSemester.executeUpdate();
		}
		catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
	}
	/*
	 * This method will execute the selectByStatus query that selects courses from the db by status and returns the resultSet
	 */
	protected ResultSet selectByStatus(boolean status) {
		ResultSet resultSet = null;
		try {
			selectByStatus.setBoolean(1, status);
			resultSet = selectByStatus.executeQuery();
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
		return resultSet;
	}
	/*
	 * This method takes boolean as an input and executes the selectByStatus query.
	 * It returns an ArrayList containing Course objects initialized with Course data from each row in the courses table (database)
	 */
	public ArrayList<Course> getAllByStatus(boolean status){
		ArrayList<Course> results = null;
		ResultSet resultSet = null;
		
		try
		{
			resultSet = selectByStatus(status); //Execute the selectByStatus query. resultSet contains the rows returned by the query
			results = new ArrayList<Course>();
		
			while(resultSet.next()) // for each row returned by the select query...
			{
				// Initialize a new Course object with the row's data. Add the Course object to the results ArrayList
				results.add(new Course(
					resultSet.getInt("CourseID"), // get the value associated to the CourseID column
					resultSet.getString("Name"), // get the value associated to the Name column
					resultSet.getBoolean("Status"), // get the value associated to the Status column
					resultSet.getString("Semester"))); // get the value associated to the Semester column
			}
		} // end try
		catch (SQLException sqlException)
		{
			sqlException.printStackTrace();
		}
		finally
		{
			try
			{
				resultSet.close();
			}
			catch (SQLException sqlException)
			{
				sqlException.printStackTrace();
			}
		} // end finally
		return results;
	} // end method getAllByStatus
	/*
	 * This method will execute the selectBySemester query that selects courses from the db by planned semester and returns the resultSet
	 */
	protected ResultSet selectBySemester(String semester) {
		ResultSet resultSet = null;
		try {
			selectBySemester.setString(1, semester);
			resultSet = selectBySemester.executeQuery();
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
		return resultSet;
	}
	/*
	 * This method takes String as an input and executes the selectBySemester query.
	 * It returns an ArrayList containing Course objects initialized with Course data from each row in the courses table (database)
	 */
	public ArrayList<Course> getAllBySemester(String semester){
		ArrayList<Course> results = null;
		ResultSet resultSet = null;
		
		try
		{
			resultSet = selectBySemester(semester); //Execute the selectBySemester query. resultSet contains the rows returned by the query
			results = new ArrayList<Course>();
		
			while(resultSet.next()) // for each row returned by the select query...
			{
				// Initialize a new Course object with the row's data. Add the Course object to the results ArrayList
				results.add(new Course(
					resultSet.getInt("CourseID"), // get the value associated to the CourseID column
					resultSet.getString("Name"), // get the value associated to the Name column
					resultSet.getBoolean("Status"), // get the value associated to the Status column
					resultSet.getString("Semester"))); // get the value associated to the Semester column
			}
		} // end try
		catch (SQLException sqlException)
		{
			sqlException.printStackTrace();
		}
		finally
		{
			try
			{
				resultSet.close();
			}
			catch (SQLException sqlException)
			{
				sqlException.printStackTrace();
			}
		} // end finally
		return results;
	} // end method getAllBySemester
	/*
	 * This method will execute the selectByName query that selects courses from the db by name and returns the resultSet
	 */
	protected ResultSet selectByName(String name) {
		ResultSet resultSet = null;
		try {
			selectByName.setString(1,"%"+name+"%");//search will be based on part of the name
			resultSet = selectByName.executeQuery();
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
		return resultSet;
	}
	/*
	 * This method takes String as an input and executes the selectByName query.
	 * It returns an ArrayList containing Course objects initialized with Course data from each row in the courses table (database)
	 */
	public ArrayList<Course> getAllByName(String name){
		ArrayList<Course> results = null;
		ResultSet resultSet = null;
		
		try
		{
			resultSet = selectByName(name); //Execute the selectAllCourses query. resultSet contains the rows returned by the query
			results = new ArrayList<Course>();
		
			while(resultSet.next()) // for each row returned by the select query...
			{
				// Initialize a new Course object with the row's data. Add the Course object to the results ArrayList
				results.add(new Course(
					resultSet.getInt("CourseID"), // get the value associated to the CourseID column
					resultSet.getString("Name"), // get the value associated to the Name column
					resultSet.getBoolean("Status"), // get the value associated to the Status column
					resultSet.getString("Semester"))); // get the value associated to the Semester column
			}
		} // end try
		catch (SQLException sqlException)
		{
			sqlException.printStackTrace();
		}
		finally
		{
			try
			{
				resultSet.close();
			}
			catch (SQLException sqlException)
			{
				sqlException.printStackTrace();
			}
		} // end finally
		return results;
	} // end method getAllByName
}


	