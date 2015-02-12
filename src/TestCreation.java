import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class TestCreation {
	
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
		Statement stmt = null;
		try {
			stmt = c.createStatement();
			String sql4 = "insert into test(id,_class_id,name,available_to_students,time_limit,strict_test,date_added,total_mark) VALUES (nextval('test_seq'),(select id from class limit 1),'Countries Test',false,15,true,'2015-02-12 00:00:00',0);"
					+ "insert into question(id,test_id,question_type_id,value,position,mark) VALUES (nextval('question_seq'),(select id from test where name = 'Countries Test'),(select id from question_type where name = 'Radio'),'What is the capital of Qatar?',0,5);"
					+ "insert into answer VALUES (nextval('answer_seq'),(select id from question where value = 'What is the capital of Qatar?'),'Abu Dhabi',false);"
					+ "insert into answer VALUES (nextval('answer_seq'),(select id from question where value = 'What is the capital of Qatar?'),'Accra',false);"
					+ "insert into answer VALUES (nextval('answer_seq'),(select id from question where value = 'What is the capital of Qatar?'),'Manama',false);"
					+ "insert into answer VALUES (nextval('answer_seq'),(select id from question where value = 'What is the capital of Qatar?'),'Doha',true);"
					+ "insert into answer VALUES (nextval('answer_seq'),(select id from question where value = 'What is the capital of Qatar?'),'Dukhan',false);"
					+ "insert into question(id,test_id,question_type_id,value,position,mark) VALUES (nextval('question_seq'),(select id from test where name = 'Countries Test'),(select id from question_type where name = 'Checkbox'),'Which of the places below are in the country of Spain?',0,10);"
					+ "insert into answer VALUES (nextval('answer_seq'),(select id from question where value = 'Which of the places below are in the country of Spain?'),'Albacete',true);"
					+ "insert into answer VALUES (nextval('answer_seq'),(select id from question where value = 'Which of the places below are in the country of Spain?'),'Beja',false);"
					+ "insert into answer VALUES (nextval('answer_seq'),(select id from question where value = 'Which of the places below are in the country of Spain?'),'Coimbra',false);"
					+ "insert into answer VALUES (nextval('answer_seq'),(select id from question where value = 'Which of the places below are in the country of Spain?'),'Pamplona',true);"
					+ "insert into answer VALUES (nextval('answer_seq'),(select id from question where value = 'Which of the places below are in the country of Spain?'),'Zamora',true);"
					+ "insert into answer VALUES (nextval('answer_seq'),(select id from question where value = 'Which of the places below are in the country of Spain?'),'Guarda',false);"
					+ "update test set total_mark = (select sum(mark) from question where test_id = (select id from test where name = 'Countries Test')) where name = 'Countries Test'";
			
			stmt.executeUpdate(sql4);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		

	}

}
