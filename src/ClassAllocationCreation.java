import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


/*
 * Creates classes and randomly allocates them to an assessor
 * Allocates classes to students as well
 */

public class ClassAllocationCreation {
	
	public static List<String> modules = new ArrayList<>();
	
	public static Connection initialiseDB() {

		Connection c = null;

		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5432/Q+AWebAppDB", "postgres","password");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return c;
	}
	
	public static boolean initialiseLists() {

		File file = new File("src/modules.txt");

		try {

			Scanner scanner = new Scanner(file);

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				modules.add(line);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}
	
	public static void main(String[] args) {
		Connection c = initialiseDB();
		initialiseLists();
		
		System.out.println("Are you happy for all modules to be allocated? (y/n)");
		Scanner scanner = new Scanner(System.in);
		String defaults = scanner.nextLine();
		if(defaults.equalsIgnoreCase("y")){
			createClasses(c);
		}
		
		System.out.println("Sorted");
		
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean createClasses(Connection c){
		
		Date currentDate = new Date();
		String newCurrentDate = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0000000").format(currentDate);
		
		int classID = -1;
		Statement stmt = null;
		Long assessorID = (long) -1;
		int studentCount = -1;
		Long randomStudent = (long) -1;
		int studentClassExist = -1;
		try {
			stmt = c.createStatement();
			
			for(int i=0;i<modules.size();i++){
				stmt = c.createStatement();
				String sql = "insert into class VALUES (nextval('class_seq'),'"+modules.get(i)+"',null,'"+newCurrentDate+"');";
				stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					classID = rs.getInt(1);
				}
				rs.close();
				stmt.close();
				
				

				stmt = c.createStatement();
				String sql2 = "SELECT id FROM users where role_id = (select id from role where name ILIKE '%Assessor%') ORDER BY RANDOM() LIMIT 1;";
    			ResultSet rs2 = stmt.executeQuery(sql2);
    			while (rs2.next()) {
    				assessorID = rs2.getLong(1);
    			}
    			rs2.close();
				stmt.close();
				
				stmt = c.createStatement();
				String sql3 = "insert into users_class VALUES ("+assessorID+","+classID+");";
				stmt.executeUpdate(sql3);
				stmt.close();
				
				stmt = c.createStatement();
				String sql4 = "select count(*) from users where created_by = "+assessorID;
    			ResultSet rs4 = stmt.executeQuery(sql4);
    			while (rs4.next()) {
    				studentCount = rs4.getInt(1);
    			}
    			rs4.close();
				stmt.close();
				
				Random rand = new Random();
				int randomNum = rand.nextInt((studentCount - 1) + 1) + 1;
				
				if(randomNum>0){
					
					for(int b=0;b<randomNum;b++){
					
						stmt = c.createStatement();
						String sql5 = "SELECT id FROM users where created_by = "+assessorID+" ORDER BY RANDOM() LIMIT 1;";
		    			ResultSet rs5 = stmt.executeQuery(sql5);
		    			while (rs5.next()) {
		    				randomStudent = rs5.getLong(1);
		    			}
		    			rs5.close();
						stmt.close();
						
						 
								
						stmt = c.createStatement();
						String sql6 = "select count(*) from users_class where users_id = "+randomStudent+" and class_id = "+classID;
		    			ResultSet rs6 = stmt.executeQuery(sql6);
		    			while (rs6.next()) {
		    				studentClassExist = rs6.getInt(1);
		    			}
		    			rs6.close();
						stmt.close();
						
						if(studentClassExist==0){
							
							stmt = c.createStatement();
							String sql7 = "insert into users_class VALUES ("+randomStudent+","+classID+");";
							stmt.executeUpdate(sql7);
							stmt.close();
							
							
						}	
					
					}
					
				}
				
				
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
			return false;
		}
		
		
		

		
		
		return false;
	}

}
