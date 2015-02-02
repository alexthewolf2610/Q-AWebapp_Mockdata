
import java.io.File;

import java.io.FileNotFoundException;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class UserCreation {

	public static List<String> titles = new ArrayList<>(Arrays.asList("Mr","Mrs", "Miss", "Ms", "Dr"));
	public static List<String> firstNames = new ArrayList<>();
	public static List<String> lastNames = new ArrayList<>();
	public static List<Integer> currentAssessors = new ArrayList<>();

	public static Connection initialiseDB() {

		Connection c = null;

		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5432/Q+AWebAppDB", "postgres",
					"password");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Opened database successfully");
		return c;
	}

	public static boolean initialiseLists() {

		File file = new File("src/first_names.txt");

		try {

			Scanner scanner = new Scanner(file);

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				firstNames.add(line);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		File file2 = new File("src/last_names.txt");

		try {

			Scanner scanner = new Scanner(file2);

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				lastNames.add(line);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public static void main(String[] args) {
		
		int noOfClasses = 0;
		
		System.out.println("What time of user would you like? Type (1) for assessor and (2 for student)");
		Scanner scanner = new Scanner(System.in);
		int userID = scanner.nextInt();
		System.out.println("How many users would you like to create?");
		int noOfUsers = scanner.nextInt();
	
		

		
		
		Connection c = initialiseDB();
		initialiseLists();
		if (userID == 1) {
			createAssessors(c, noOfUsers);
		}
		if (userID == 2) {
			createStudents(c, noOfUsers);
		}

		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	

	public static boolean createAssessors(Connection c, int noOfUsers) {

		int roleID = -1;
		Statement stmt = null;
		try {
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select id from role where name ILIKE '%Assessor%';");
			while (rs.next()) {
				roleID = rs.getInt("id");
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		System.out.println("The roleID for assessor is: " + roleID);
		

		for (int i = 0; i < noOfUsers; i++) {
			
			Random randomGenerator = new Random();
			SecureRandom random = new SecureRandom();
			int addressID = -1;
			
			try {
				stmt = c.createStatement();
				String sql = "insert into address VALUES (nextval('address_seq'),'line1','line2','city','county','country','postcode');";
				stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
				ResultSet rs = stmt.getGeneratedKeys();
				if ( rs.next() ) {
					addressID = rs.getInt(1);
				}
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(0);
			}
			
            
            int titleIndex = randomGenerator.nextInt(titles.size());
            String randomTitle = titles.get(titleIndex);
            
            int firstNameIndex = randomGenerator.nextInt(firstNames.size());
            String randomFirstName = firstNames.get(firstNameIndex);
            
            int lastNameIndex = randomGenerator.nextInt(lastNames.size());
            String randomLastName = lastNames.get(lastNameIndex);
            
            int randomNumberForEmail = randomGenerator.nextInt(50);
            
            String email = randomFirstName+"."+randomLastName+randomNumberForEmail+"@email.com";
            
            String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw("pass", org.mindrot.jbcrypt.BCrypt.gensalt());
            
            int year = randomGenerator.nextInt((2000 - 1900) + 1) + 1900;// generate a year between 1900 and 2010;
    		int dayOfYear = randomGenerator.nextInt((365 - 1) + 1) + 1;// generate a number between 1 and 365 (or 366 if you need to handle leap year);
    		Calendar calendar = Calendar.getInstance();
    		calendar.set(Calendar.YEAR, year);
    		calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
    		Date randomDoB = calendar.getTime();
    		
    		String newDate = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0000000").format(randomDoB);
    		
    		Date currentDate = new Date();
    		String newCurrentDate = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0000000").format(currentDate);
            String sql = "INSERT INTO users (id,role_id,address_id,title,first_name,last_name,email,password,dob,date_added) VALUES (nextval('users_seq'),"+roleID+","+addressID+",'"+randomTitle+"','"+randomFirstName+"','"+randomLastName+"','"+email+"','"+hashedPassword+"','"+newDate+"','"+newCurrentDate+"');"; 
            
            int count = -1;
   
    		try {
    			stmt = c.createStatement();
    			ResultSet rs = stmt.executeQuery("select count(*) from users where email ILIKE '"+email+"';");
    			while (rs.next()) {
    				count = rs.getInt(1);
    			}
    			rs.close();
    			stmt.close();

    		} catch (Exception e) {
    			e.printStackTrace();
    			return false;
    		}
            
			try {
				stmt = c.createStatement();
				if(count<=1){
				stmt.executeUpdate(sql);
				}
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			

		}


		return true;

	}


	public static boolean createStudents(Connection c, int noOfUsers) {
		
		if(currentAssessors.size()==0){
			createAssessors(c,1);
		}

		int roleID = -1;
		Statement stmt = null;
		try {
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select id from role where name ILIKE '%Student%';");
			while (rs.next()) {
				roleID = rs.getInt("id");
			}
			rs.close();
			stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("The roleID for student is: " + roleID);
		
		int assessorRoleID = -1;
		stmt = null;
		try {
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select id from role where name ILIKE '%Assessor%';");
			while (rs.next()) {
				assessorRoleID = rs.getInt("id");
			}
			rs.close();
			stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		try {
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select id from users where role_id = "+assessorRoleID+";");
			while (rs.next()) {
				currentAssessors.add(rs.getInt("id"));
			}
			rs.close();
			stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		

		for (int i = 0; i < noOfUsers; i++) {
			
			Random randomGenerator = new Random();
			SecureRandom random = new SecureRandom();
			int addressID = -1;
			
			try {
				stmt = c.createStatement();
				String sql = "insert into address VALUES (nextval('address_seq'),'line1','line2','city','county','country','postcode');";
				stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
				ResultSet rs = stmt.getGeneratedKeys();
				if ( rs.next() ) {
					addressID = rs.getInt(1);
				}
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(0);
			}
			
			
            int index = randomGenerator.nextInt(currentAssessors.size());
            int randomAssessor = currentAssessors.get(index);
            
            int titleIndex = randomGenerator.nextInt(titles.size());
            String randomTitle = titles.get(titleIndex);
            
            int firstNameIndex = randomGenerator.nextInt(firstNames.size());
            String randomFirstName = firstNames.get(firstNameIndex);
            
            int lastNameIndex = randomGenerator.nextInt(lastNames.size());
            String randomLastName = lastNames.get(lastNameIndex);
            
            int randomNumberForEmail = randomGenerator.nextInt(50);
            
            String email = randomFirstName+"."+randomLastName+randomNumberForEmail+"@email.com";
            
            String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw("pass", org.mindrot.jbcrypt.BCrypt.gensalt());
            
            int year = randomGenerator.nextInt((2000 - 1900) + 1) + 1900;// generate a year between 1900 and 2010;
    		int dayOfYear = randomGenerator.nextInt((365 - 1) + 1) + 1;// generate a number between 1 and 365 (or 366 if you need to handle leap year);
    		Calendar calendar = Calendar.getInstance();
    		calendar.set(Calendar.YEAR, year);
    		calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
    		Date randomDoB = calendar.getTime();
    		
    		String newDate = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0000000").format(randomDoB);
    		
    		Date currentDate = new Date();
    		String newCurrentDate = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0000000").format(currentDate);
            String sql = "INSERT INTO users VALUES (nextval('users_seq'),"+roleID+","+addressID+","+randomAssessor+",'"+randomTitle+"','"+randomFirstName+"','"+randomLastName+"','"+email+"','"+hashedPassword+"','"+newDate+"','"+newCurrentDate+"');"; 
            
            int count = -1;
   
    		try {
    			stmt = c.createStatement();
    			ResultSet rs = stmt.executeQuery("select count(*) from users where email ILIKE '"+email+"';");
    			while (rs.next()) {
    				count = rs.getInt(1);
    			}
    			rs.close();
    			stmt.close();

    		} catch (Exception e) {
    			e.printStackTrace();
    			return false;
    		}
    		
    		
    		if(count<=1){
			try {
				stmt = c.createStatement();
				
				stmt.executeUpdate(sql);
				
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    		}
			

		}
		
		

		return true;

	}

}
