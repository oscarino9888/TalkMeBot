import java.sql.SQLException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import Beans.Paziente;
import Database.DatabaseQuery;
public class testDatabase {
	
	public static void main(String[] args) throws SQLException {
		int idtest;
		Paziente paziente=new Paziente("Amero", "Tomaselli", 22, "Celibe", "Campania", "Maschio", "Avellino", "Diploma Scientifico");

		try {
			/* 66 */       DatabaseQuery.addUser(paziente);
			/* 67 */       
			/* 68 */     } catch (SQLException e) {
			/*    */       
			/* 70 */       
			/* 71 */       e.printStackTrace();
			/*    */     } 
		idtest=DatabaseQuery.getLastId();
		System.out.println(idtest);
		
	}
	
	
	
	
	}

	

