import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


public class SimpleCreation {
	
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
	
	public static void main(String[] args) {
		
		Connection c = initialiseDB();
		
		System.out.println("Are the database tables empty? (y/n)");
		Scanner scanner = new Scanner(System.in);
		String defaults = scanner.nextLine();
		if(defaults.equalsIgnoreCase("y")){
		createDefaults(c);
		}
		System.out.println("Sorted");

	}


public static boolean createDefaults(Connection c){
	
	Date currentDate = new Date();
	String newCurrentDate = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0000000").format(currentDate);
	
	Statement stmt = null;
	try {
		stmt = c.createStatement();
		String sql = "insert into role values (1,'Student','Student');"
				   + "insert into role values (2,'Assessor','Assessor');"
				   + "insert into role values (3,'Admin','Admin');"
				   + "insert into category VALUES (nextval('category_seq'),'Math');"
				   + "insert into category VALUES (nextval('category_seq'),'English');"
				   + "insert into category VALUES (nextval('category_seq'),'Science');"
				   + "insert into question_type VALUES (nextval('question_type_seq'),'Radio','radio buttons');"
				   + "insert into question_type VALUES (nextval('question_type_seq'),'Checkbox','checkboxes');"
				   + "insert into question_type VALUES (nextval('question_type_seq'),'Drag & Drop','drag and drop type questions');"
				   + "insert into users (id,role_id,address_id,title,first_name,last_name,email,password,dob,date_added) values (nextval('users_seq'),(select id from role where name ILIKE '%Assessor%'),(select id from address limit 1),'Mr.','Alex','Fox','af170@le.ac.uk','$2a$10$oFakQxlr/xVlGweVYS4wEenReexIExnhjHttQfXw/5yFJaAum2a1u','1992-10-26 00:00:00','"+newCurrentDate+"');"
				   + "insert into users (id,role_id,address_id,title,first_name,last_name,email,password,dob,date_added) values (nextval('users_seq'),(select id from role where name ILIKE '%Admin%'),(select id from address limit 1),'Mr.','Alex','Fox','alex.fox2610@googlemail.com','$2a$10$oFakQxlr/xVlGweVYS4wEenReexIExnhjHttQfXw/5yFJaAum2a1u','1992-10-26 00:00:00','"+newCurrentDate+"');";
		
		stmt.executeUpdate(sql);
		stmt.close();
	} catch (SQLException e) {
		e.printStackTrace();
		System.exit(0);
		return false;
	}
	
	int assessorID = -1;
	
	try{
	stmt = c.createStatement();
	ResultSet rs = stmt.executeQuery("select id from users where email = 'af170@le.ac.uk';");
	while (rs.next()) {
		assessorID = rs.getInt("id");
	}
	rs.close();
	}catch(SQLException e){
		e.printStackTrace();
		System.exit(0);
		return false;
	}
	
	int classID = -1;
	
	for(int i=0;i<5;i++){
	try {
		stmt = c.createStatement();
		String sql = "insert into class VALUES (nextval('class_seq'),'Class "+i+"','Class "+i+" Description','"+newCurrentDate+"');";
		stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		ResultSet rs = stmt.getGeneratedKeys();
		if ( rs.next() ) {
			classID = rs.getInt(1);
		}
		stmt.close();
		
		stmt = c.createStatement();
		String sql2 = "insert into users_class VALUES ("+assessorID+","+classID+");";
		stmt.executeUpdate(sql2);
		stmt.close();
	} catch (SQLException e) {
		e.printStackTrace();
		System.exit(0);
		return false;
	}
	}
	


	
	
	
	return true;
	
	
	
}

}
