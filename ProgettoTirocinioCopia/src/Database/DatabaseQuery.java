package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Beans.Domanda;
import Beans.Paziente;


public class DatabaseQuery {

	
	public static synchronized boolean addUser(Paziente paziente) throws SQLException {
		/*  71 */     Connection connection = null;
		/*  72 */     PreparedStatement psAddUtente = null;
		/*     */     
		/*     */     try {
		/*  75 */       connection = Database.getConnection();
		/*  76 */       psAddUtente = connection.prepareStatement(queryAddPaziente);
		/*     */       
		/*  78 */       //String idUtente = Integer.toString(paziente.getIdUtente());
		/*     */       
		/*  80 */       psAddUtente.setString(1, paziente.getNome());
		/*  81 */       psAddUtente.setString(2, paziente.getCognome());
		/*  82 */       psAddUtente.setInt(3, paziente.getEta());
		/*  83 */       psAddUtente.setString(4, paziente.getStato_civile());
		/*  84 */       psAddUtente.setString(5, paziente.getRegione());
		/*  85 */       psAddUtente.setString(6, paziente.getSesso());
						psAddUtente.setString(7, paziente.getProvincia());
						psAddUtente.setString(8, paziente.getTitolo_di_studio());
		/*     */       
		/*  87 */       System.out.println(psAddUtente.toString());
		/*     */       
		/*  89 */       psAddUtente.executeUpdate();
		/*     */       
		/*  91 */       connection.commit();
		/*  92 */       System.out.println("GU Connessione...");
		/*     */     } finally {
		/*     */       try {
		/*  95 */         if (psAddUtente != null)
		/*  96 */           psAddUtente.close(); 
		/*     */       } finally {
		/*  98 */         Database.releaseConnection(connection);
		/*     */       } 
		/*     */     } 
		/*     */     
		/* 102 */     return true;
		/*     */   }
	
	
	public static synchronized boolean addPunteggioExtra(Domanda d) throws SQLException {
		/*  71 */     Connection connection = null;
		/*  72 */     PreparedStatement psAddUtente = null;
		/*     */     
		/*     */     try {
		/*  75 */       connection = Database.getConnection();
		/*  76 */       psAddUtente = connection.prepareStatement(queryAddPunteggioExtra);
		/*     */       
		/*  78 */       //String idUtente = Integer.toString(paziente.getIdUtente());
		/*     */       
		/*  80 */       psAddUtente.setInt(1, d.getN_domanda());
		/*  81 */       psAddUtente.setString(2, d.getValore());
		/*  82 */       psAddUtente.setString(3, d.getGrado());
		/*  83 */       
		
		/*     */       
		/*  87 */       System.out.println(psAddUtente.toString());
		/*     */       
		/*  89 */       psAddUtente.executeUpdate();
		/*     */       
		/*  91 */       connection.commit();
		/*  92 */       System.out.println("GU Connessione...");
		/*     */     } finally {
		/*     */       try {
		/*  95 */         if (psAddUtente != null)
		/*  96 */           psAddUtente.close(); 
		/*     */       } finally {
		/*  98 */         Database.releaseConnection(connection);
		/*     */       } 
		/*     */     } 
		/*     */     
		/* 102 */     return true;
		/*     */   }
	
	public static synchronized boolean addCompilazione(int idTest,int idPersona) throws SQLException {
		/*  71 */     Connection connection = null;
		/*  72 */     PreparedStatement psAddUtente = null;
		/*     */     
		/*     */     try {
		/*  75 */       connection = Database.getConnection();
		/*  76 */       psAddUtente = connection.prepareStatement(queryAddCompilazione);
		/*     */       
		/*  78 */       //String idUtente = Integer.toString(paziente.getIdUtente());
		/*     */       
		/*  80 */       psAddUtente.setInt(1, idPersona);
		/*  81 */       psAddUtente.setInt(2, idTest);
	
		/*  83 */       
		
		/*     */       
		/*  87 */       System.out.println(psAddUtente.toString());
		/*     */       
		/*  89 */       psAddUtente.executeUpdate();
		/*     */       
		/*  91 */       connection.commit();
		/*  92 */       System.out.println("GU Connessione...");
		/*     */     } finally {
		/*     */       try {
		/*  95 */         if (psAddUtente != null)
		/*  96 */           psAddUtente.close(); 
		/*     */       } finally {
		/*  98 */         Database.releaseConnection(connection);
		/*     */       } 
		/*     */     } 
		/*     */     
		/* 102 */     return true;
		/*     */   }
	
	public static synchronized boolean addCompilazioneExtra(int idDomanda,int idPersona) throws SQLException {
		/*  71 */     Connection connection = null;
		/*  72 */     PreparedStatement psAddUtente = null;
		/*     */     
		/*     */     try {
		/*  75 */       connection = Database.getConnection();
		/*  76 */       psAddUtente = connection.prepareStatement(queryAddCompilazioneExtra);
		/*     */       
		/*  78 */       //String idUtente = Integer.toString(paziente.getIdUtente());
		/*     */       
		/*  80 */       psAddUtente.setInt(1, idDomanda);
		/*  81 */       psAddUtente.setInt(2, idPersona);
	
		/*  83 */       
		
		/*     */       
		/*  87 */       System.out.println(psAddUtente.toString());
		/*     */       
		/*  89 */       psAddUtente.executeUpdate();
		/*     */       
		/*  91 */       connection.commit();
		/*  92 */       System.out.println("GU Connessione...");
		/*     */     } finally {
		/*     */       try {
		/*  95 */         if (psAddUtente != null)
		/*  96 */           psAddUtente.close(); 
		/*     */       } finally {
		/*  98 */         Database.releaseConnection(connection);
		/*     */       } 
		/*     */     } 
		/*     */     
		/* 102 */     return true;
		/*     */   }
	
	public static synchronized boolean addPunteggioBeck(int punteggio) throws SQLException {
		/*  71 */     Connection connection = null;
		/*  72 */     PreparedStatement psAddUtente = null;
		/*     */     
		/*     */     try {
		/*  75 */       connection = Database.getConnection();
		/*  76 */       psAddUtente = connection.prepareStatement(queryAddPunteggioBeck);
		/*     */       
		/*  78 */       //String idUtente = Integer.toString(paziente.getIdUtente());
		/*     */       
		/*  80 */       psAddUtente.setInt(1,punteggio );
		
		/*     */       
		/*  87 */       System.out.println(psAddUtente.toString());
		/*     */       
		/*  89 */       psAddUtente.executeUpdate();
		/*     */       
		/*  91 */       connection.commit();
		/*  92 */       System.out.println("GU Connessione...");
		/*     */     } finally {
		/*     */       try {
		/*  95 */         if (psAddUtente != null)
		/*  96 */           psAddUtente.close(); 
		/*     */       } finally {
		/*  98 */         Database.releaseConnection(connection);
		/*     */       } 
		/*     */     } 
		/*     */     
		/* 102 */     return true;
		/*     */   }
	
	public static synchronized int getLastId() throws SQLException {
		/* 686 */     Connection connection = null;
		/* 687 */     PreparedStatement psGetMaxId = null;
		/*     */     
		/* 689 */     int valore = 0;
		/*     */     
		/*     */     try {
		/* 692 */       connection = Database.getConnection();
		/* 693 */       psGetMaxId = connection.prepareStatement(queryUltimoInserito);
		/*     */       
		/* 695 */      
		/* 696 */       ResultSet rs = psGetMaxId.executeQuery();
		              if (rs.next()){
		            	  valore=rs.getInt(1);
		              }
		/*     */       
		/* 698 */      
		/*     */     } finally {
		/*     */ 
		/*     */       
		/*     */       try {
		/* 706 */         if (psGetMaxId != null)
		/* 707 */           psGetMaxId.close(); 
		/* 708 */         if (psGetMaxId != null)
		/* 709 */           psGetMaxId.close(); 
		/* 710 */       } catch (SQLException e) {
		/* 711 */         e.printStackTrace();
		/*     */       } finally {
		/*     */         
		/* 714 */         connection.close();
		/* 715 */         Database.releaseConnection(connection);
		/*     */       } 
		/*     */     } 
		/* 718 */     return valore;
		/*     */   }
	
	public static synchronized int getLastIdRisposteExtr() throws SQLException {
		/* 686 */     Connection connection = null;
		/* 687 */     PreparedStatement psGetMaxId = null;
		/*     */     
		/* 689 */     int valore = 0;
		/*     */     
		/*     */     try {
		/* 692 */       connection = Database.getConnection();
		/* 693 */       psGetMaxId = connection.prepareStatement(queryUltimoInseritoDomanda);
		/*     */       
		/* 695 */      
		/* 696 */       ResultSet rs = psGetMaxId.executeQuery();
		              if (rs.next()){
		            	  valore=rs.getInt(1);
		              }
		/*     */       
		/* 698 */      
		/*     */     } finally {
		/*     */ 
		/*     */       
		/*     */       try {
		/* 706 */         if (psGetMaxId != null)
		/* 707 */           psGetMaxId.close(); 
		/* 708 */         if (psGetMaxId != null)
		/* 709 */           psGetMaxId.close(); 
		/* 710 */       } catch (SQLException e) {
		/* 711 */         e.printStackTrace();
		/*     */       } finally {
		/*     */         
		/* 714 */         connection.close();
		/* 715 */         Database.releaseConnection(connection);
		/*     */       } 
		/*     */     } 
		/* 718 */     return valore;
		/*     */   }
	
	public static synchronized int getLastIdTest() throws SQLException {
		/* 686 */     Connection connection = null;
		/* 687 */     PreparedStatement psGetMaxId = null;
		/*     */     
		/* 689 */     int valore = 0;
		/*     */     
		/*     */     try {
		/* 692 */       connection = Database.getConnection();
		/* 693 */       psGetMaxId = connection.prepareStatement(queryUltimoInseritoTest);
		/*     */       
		/* 695 */      
		/* 696 */       ResultSet rs = psGetMaxId.executeQuery();
		              if (rs.next()){
		            	  valore=rs.getInt(1);
		              }
		/*     */       
		/* 698 */      
		/*     */     } finally {
		/*     */ 
		/*     */       
		/*     */       try {
		/* 706 */         if (psGetMaxId != null)
		/* 707 */           psGetMaxId.close(); 
		/* 708 */         if (psGetMaxId != null)
		/* 709 */           psGetMaxId.close(); 
		/* 710 */       } catch (SQLException e) {
		/* 711 */         e.printStackTrace();
		/*     */       } finally {
		/*     */         
		/* 714 */         connection.close();
		/* 715 */         Database.releaseConnection(connection);
		/*     */       } 
		/*     */     } 
		/* 718 */     return valore;
		/*     */   }
	
	public static synchronized ArrayList getUtentiAll() throws SQLException {
		/* 206 */     Connection connection = null;
		/* 207 */     PreparedStatement preparedStatement = null;
		/* 208 */     ArrayList<Paziente> pazienti = new ArrayList();
		/*     */     
		/*     */     try {
		/* 211 */       connection = Database.getConnection();
		/* 212 */       preparedStatement = connection.prepareStatement(queryGetUtenti);
		/*     */       
		/* 214 */       ResultSet rs = preparedStatement.executeQuery();
		/*     */       
		/* 216 */       while (rs.next()) {
		/* 217 */         Paziente utente = new Paziente();
		/* 218 */         utente.setNome(rs.getString("nome"));
		/* 220 */         utente.setCognome(rs.getString("cognome"));
		/* 221 */         utente.setEta(rs.getInt("eta"));
		/* 222 */         utente.setSesso(rs.getString("sesso"));
		                  utente.setProvincia(rs.getString("provincia"));
		/*     */         utente.setRegione(rs.getString("regione"));
		                  utente.setStato_civile(rs.getString("stato_civile"));
		                  utente.setTitolo_di_studio(rs.getString("titolo_di_studio"));
		                  utente.setId(rs.getInt("id"));
		/* 224 */         pazienti.add(utente);
		/*     */       } 
		/*     */     } finally {
		/*     */       try {
		/* 228 */         if (preparedStatement != null)
		/* 229 */           preparedStatement.close(); 
		/*     */       } finally {
		/* 231 */         Database.releaseConnection(connection);
		/*     */       } 
		/*     */     } 
		/* 234 */     return pazienti;
		/*     */   }
	
	
	public static synchronized int getPunteggio(int idpersona) throws SQLException {
		/* 206 */     Connection connection = null;
		/* 207 */     PreparedStatement preparedStatement = null;
		/*     */     int punteggio = 0;
		/*     */     try {
		/* 211 */       connection = Database.getConnection();
		/* 212 */       preparedStatement = connection.prepareStatement(queryGetPunteggio);
		/*     */       preparedStatement.setInt(1, idpersona);
		/* 214 */       ResultSet rs = preparedStatement.executeQuery();
		/*     */       
		/* 216 */       while (rs.next()) {
		/* 217 */         punteggio=rs.getInt(1);
		/*     */       } 
		/*     */     } finally {
		/*     */       try {
		/* 228 */         if (preparedStatement != null)
		/* 229 */           preparedStatement.close(); 
		/*     */       } finally {
		/* 231 */         Database.releaseConnection(connection);
		/*     */       } 
		/*     */     } 
		/* 234 */     return punteggio;
		/*     */   }
	
	public static synchronized ArrayList getDomandeExtra(int idpersona) throws SQLException {
		/* 206 */     Connection connection = null;
		/* 207 */     PreparedStatement preparedStatement = null;
		/*     */     ArrayList<Domanda> domande = new ArrayList();
		/*     */     try {
		/* 211 */       connection = Database.getConnection();
		/* 212 */       preparedStatement = connection.prepareStatement(queryGetDomandeExtra);
		/*     */       preparedStatement.setInt(1, idpersona);
		/* 214 */       ResultSet rs = preparedStatement.executeQuery();
		/*     */       
		/* 216 */       while (rs.next()) {
		/* 217 */         Domanda d=new Domanda();
		                  d.setId(rs.getInt("iddomande_extra"));
		                  d.setValore(rs.getString("valore"));
		                  d.setGrado(rs.getString("grado"));
		                  d.setN_domanda(rs.getInt("n_domanda"));
		                  domande.add(d);
		/*     */       } 
		/*     */     } finally {
		/*     */       try {
		/* 228 */         if (preparedStatement != null)
		/* 229 */           preparedStatement.close(); 
		/*     */       } finally {
		/* 231 */         Database.releaseConnection(connection);
		/*     */       } 
		/*     */     } 
		/* 234 */     return domande;
		/*     */   }
	
	private static String queryAddPaziente = "INSERT INTO talkmebot.paziente (nome,cognome, eta, stato_civile, regione, sesso, provincia,titolo_di_studio) VALUES (?,?,?,?,?,?,?,?);";
	private static String queryUltimoInserito = "SELECT (max(id)) from talkmebot.paziente;";
	private static String queryUltimoInseritoDomanda = "SELECT (max(iddomande_extra)) from talkmebot.domande_extra;";
	private static String queryUltimoInseritoTest = "SELECT (max(idpunteggio_beck)) from talkmebot.punteggio_beck;";
	private static String queryAddPunteggioBeck = "INSERT INTO talkmebot.punteggio_beck (punteggio) VALUES (?);";
	private static String queryAddCompilazione = "INSERT INTO talkmebot.compilazione (idpazient,idpunteggio) VALUES (?,?);";
	private static String queryAddCompilazioneExtra = "INSERT INTO talkmebot.compilazione_extra (iddomanda_extra,idpersona) VALUES (?,?);";
	private static String queryAddPunteggioExtra= "INSERT INTO talkmebot.domande_extra (n_domanda,valore,grado) VALUES (?,?,?);";
	private static String queryGetUtenti = "SELECT * FROM talkmebot.paziente";
	private static String queryGetPunteggio = "SELECT punteggio FROM talkmebot.punteggio_beck INNER JOIN talkmebot.compilazione ON idpunteggio = idpunteggio_beck WHERE idpazient = ?;";
	private static String queryGetDomandeExtra = "SELECT * FROM talkmebot.domande_extra INNER JOIN talkmebot.compilazione_extra ON iddomanda_extra = iddomande_extra WHERE idpersona = ? GROUP BY n_domanda";

}
