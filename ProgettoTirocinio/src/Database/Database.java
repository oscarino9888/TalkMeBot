/*    */ package Database;
/*    */ 
/*    */ import java.sql.Connection;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.SQLException;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Database
/*    */ {
/* 21 */   private static List<Connection> freeDbConnections = new LinkedList<>(); static {
/*    */     try {
/* 23 */       Class.forName("com.mysql.jdbc.Driver");
/* 24 */     } catch (ClassNotFoundException e) {
/* 25 */       System.out.println("Driver Database non trovato:" + e.getMessage());
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   private static synchronized Connection createDBConnection() throws SQLException {
/* 31 */     Connection newConnection = null;
/*    */     
/* 33 */     String ip = "localhost";
/* 34 */     String port = "3306";
/* 35 */     String db = "talkmebot";
/* 36 */     String username = "root";
/* 37 */     String password = "granese98";
/*    */ 
/*    */     
/* 40 */     newConnection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + db, username, password);
/* 41 */     newConnection.setAutoCommit(false);
/* 42 */     return newConnection;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static synchronized Connection getConnection() throws SQLException {
/*    */     Connection connection;
/* 49 */     if (!freeDbConnections.isEmpty()) {
/* 50 */       connection = freeDbConnections.get(0);
/* 51 */       freeDbConnections.remove(0);
/*    */       
/*    */       try {
/* 54 */         if (connection.isClosed())
/* 55 */           connection = getConnection(); 
/* 56 */       } catch (SQLException e) {
/* 57 */         connection.close();
/* 58 */         connection = getConnection();
/*    */       } 
/*    */     } else {
/* 61 */       connection = createDBConnection();
/*    */     } 
/*    */     
/* 64 */     return connection;
/*    */   }
/*    */   
/*    */   public static synchronized void releaseConnection(Connection connection) throws SQLException {
/* 68 */     if (connection != null)
/* 69 */       freeDbConnections.add(connection); 
/*    */   }
/*    */ }


/* Location:              C:\Users\oscar\Desktop\Workspace Ingegneria del software\Progetto Esame tsw\ImportedClasses\!\Database\Database.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */