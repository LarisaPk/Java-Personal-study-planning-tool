import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;



import java.awt.BorderLayout;

import javax.swing.ComboBoxEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.print.PrinterException;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.JCheckBox;
import javax.swing.DefaultComboBoxModel;

public class StudyPlan extends JFrame {
	//Private variables
	private static CourseQueries CourseQueries; //instance of the CourseQueries class
	private static ArrayList<Course> allCourses;  //array to store all courses
	//private JTextField txtSearchField;
	static JTable tableCourses;
	static DefaultTableModel tableModel;
	
	//Constants for table columns
	private static final int ID_COL = 0;
	private static final int NAME_COL = 1;
	private static final int STATUS_STRING_COL = 3;
	private static final int STATUS_COL = 2;
	private static final int SEMESTER_COL = 4;
	private static final int COL_COUNT = 5;
	
	public StudyPlan () {
		super("Personal study planner");
		CourseQueries = new CourseQueries();// Initializes a CourseQueries object to handle db operations in this session
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(null); 
		setBounds(0,0,708,507); 
		setLocationRelativeTo(null); 

		// button for removing course from the study plan
		JButton btnRemoveCourse = new JButton("Remove selected");
		btnRemoveCourse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CourseQueries.removeCourse(tableCourses.getModel().getValueAt((tableCourses.getSelectedRow()), ID_COL));
				populateTable();
			}
		});
		btnRemoveCourse.setBounds(495, 397, 149, 43);
		getContentPane().add(btnRemoveCourse);
		
		// button for adding course to the study plan
		JButton btnAddCourse = new JButton("Add a course");
		btnAddCourse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getCourse();
				populateTable();
			}
		});
		btnAddCourse.setBounds(506, 37, 138, 43);
		getContentPane().add(btnAddCourse);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(28, 143, 616, 208);
		getContentPane().add(scrollPane);
		
		//button for marking selected course as completed/not completed
		JButton btnMarkCompletednotCompleted = new JButton("Mark selected as completed / not completed");
		btnMarkCompletednotCompleted.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				markStatus();
				populateTable();
					}
				});
		btnMarkCompletednotCompleted.setBounds(206, 397, 279, 43);
		getContentPane().add(btnMarkCompletednotCompleted);
		
		// table for showing planned courses and manipulating them
		tableModel = new DefaultTableModel(
				new Object[1][COL_COUNT],  
				new String[] {"ID", "Course Name","","Status","Planned Semester"} // Set the column names
			);
		tableCourses = new JTable();
		scrollPane.setViewportView(tableCourses);
		tableCourses.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		tableCourses.setModel(tableModel);
		
		JLabel lblToUpdate = new JLabel("* To change Planned Semester click on it, choose a new one from the dropdown menu and press OK");
		lblToUpdate.setBounds(28, 361, 616, 26);
		getContentPane().add(lblToUpdate);
		
		//button for printing content of the table
		JButton btnPrint = new JButton("Print");
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
			          MessageFormat headerFormat = new MessageFormat("Page {0}");
			          MessageFormat footerFormat = new MessageFormat("- {0} -");
			          tableCourses.print(JTable.PrintMode.FIT_WIDTH, headerFormat, footerFormat);
			        } catch (PrinterException pe) {
			          System.err.println("Error printing: " + pe.getMessage());
			        }
			}
		});
		btnPrint.setBounds(28, 397, 167, 43);
		getContentPane().add(btnPrint);
		
		JComboBox comboBoxCompletion = new JComboBox();
		comboBoxCompletion.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			
				if(e.getStateChange() == ItemEvent.SELECTED) {
					
					if (e.getItem().equals("completed")) {
						populateTableStatus(true);
					}
					else if (e.getItem().equals("not completed")) {
						populateTableStatus(false);
					}
					else {
						populateTable();
					}
                }
			}
		});
		comboBoxCompletion.setModel(new DefaultComboBoxModel(new String[] {"All", "completed", "not completed"}));
		comboBoxCompletion.setBounds(302, 107, 131, 26);
		getContentPane().add(comboBoxCompletion);
		
		JLabel lblSelect = new JLabel("Show");
		lblSelect.setBounds(249, 111, 53, 19);
		getContentPane().add(lblSelect);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"All", "Autumn 2018", "Spring 2019", "Summer 2019", "Autumn 2019", "Spring 2020", "Summer 2020"}));
		comboBox.setBounds(506, 107, 138, 26);
		getContentPane().add(comboBox);
		
		JLabel lblShow = new JLabel("Show");
		lblShow.setBounds(457, 107, 46, 26);
		getContentPane().add(lblShow);
		
		//if user clicks on table cell in the "Planned Semester" column new panel will show up for updating the semester
		tableCourses.addMouseListener(new java.awt.event.MouseAdapter() {
		    @Override
		    public void mouseClicked(java.awt.event.MouseEvent evt) {
		    	int row = tableCourses.rowAtPoint(evt.getPoint());
		        int col = tableCourses.columnAtPoint(evt.getPoint());
		        if (row >= 0 && col == 2) {
		        	getNewSemester();
		        	populateTable();
		        }
		    }
		});
		
		//Makes ID and Status columns hidden
		tableCourses.removeColumn(tableCourses.getColumnModel().getColumn(STATUS_COL));
		tableCourses.removeColumn(tableCourses.getColumnModel().getColumn(ID_COL));
	}

	public static void main(String[] args) {
		JFrame frame = new StudyPlan();
		frame.setVisible(true);
		populateTable();
	}
	private void getCourse(){
		JPanel myPanel = new JPanel();
		
		JTextField name = new JTextField(15);
		
		String semesterChoise[]= {"Autumn 2018","Spring 2019","Summer 2019", "Autumn 2019","Spring 2020","Summer 2020"}; 
		JComboBox  semester= new JComboBox (semesterChoise);

		myPanel.add(new JLabel("Course name:"));
		myPanel.add(name);
		
	    myPanel.add(new JLabel("Planned semester:"));
	    myPanel.add(semester);
	    
	    int result = JOptionPane.showConfirmDialog(null, myPanel, "Enter course information", JOptionPane.OK_CANCEL_OPTION);
	    
	    if (result == JOptionPane.OK_OPTION) {	    	

	    	CourseQueries.addCourse(name.getText(), (String)semester.getSelectedItem());
	    }
	}
	private void getNewSemester(){
		JPanel myPanel = new JPanel();
		
		String semesterChoise[]= {"Autumn 2018","Spring 2019","Summer 2019", "Autumn 2019","Spring 2020","Summer 2020"}; 
		JComboBox  semester= new JComboBox (semesterChoise);
		
	    myPanel.add(new JLabel("Choose new Planned Semester:"));
	    myPanel.add(semester);
	    
	    int result = JOptionPane.showConfirmDialog(null, myPanel, "Update semester", JOptionPane.OK_CANCEL_OPTION);
	    
	    if (result == JOptionPane.OK_OPTION) {	    	

	    CourseQueries.updateSemester((String)semester.getSelectedItem(), (int)tableCourses.getModel().getValueAt((tableCourses.getSelectedRow()), ID_COL));
	    }
	}
	/************
	 * searchAlbums
	 * Queries the database for albums and lists them in a table
	 * Parameters: -
	 * Returns: -
	 */
	public static void populateTable() {
		Course currentCourse;
		allCourses= CourseQueries.getAllCourses();
		tableModel.setRowCount(allCourses.size());
		
		for (int row=0; row<allCourses.size(); row++){ //allCourses.size() returns the amount of items in the allCourses list
			currentCourse = allCourses.get(row); // get an course from the ArrayList allCourses
			
			tableCourses.getModel().setValueAt(currentCourse.getID(), row, ID_COL);
			tableCourses.getModel().setValueAt(currentCourse.getName(), row, NAME_COL);
			tableCourses.getModel().setValueAt(currentCourse.getStatus(), row, STATUS_COL);
			tableCourses.getModel().setValueAt(currentCourse.getStatusString(), row, STATUS_STRING_COL);
			tableCourses.getModel().setValueAt(currentCourse.getSemester(), row, SEMESTER_COL);
		}
	}
	public static void populateTableStatus(boolean status) {
		Course currentCourse;
		allCourses= CourseQueries.getByStatus(status);
		tableModel.setRowCount(allCourses.size());
		
		for (int row=0; row<allCourses.size(); row++){ //allCourses.size() returns the amount of items in the allCourses list
			currentCourse = allCourses.get(row); // get an course from the ArrayList allCourses
			
			tableCourses.getModel().setValueAt(currentCourse.getID(), row, ID_COL);
			tableCourses.getModel().setValueAt(currentCourse.getName(), row, NAME_COL);
			tableCourses.getModel().setValueAt(currentCourse.getStatus(), row, STATUS_COL);
			tableCourses.getModel().setValueAt(currentCourse.getStatusString(), row, STATUS_STRING_COL);
			tableCourses.getModel().setValueAt(currentCourse.getSemester(), row, SEMESTER_COL);
		}
	}
	private void markStatus() {
		CourseQueries.updateStatus(!(Boolean.parseBoolean(tableCourses.getModel().getValueAt(tableCourses.getSelectedRow(), STATUS_COL).toString())) ,(int) tableCourses.getModel().getValueAt((tableCourses.getSelectedRow()), ID_COL));
	}
}
