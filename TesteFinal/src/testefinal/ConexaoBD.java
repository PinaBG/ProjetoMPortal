package testefinal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
	static{
	    try{
	      Class.forName("org.postgresql.Driver");
	    }
	    catch (ClassNotFoundException e){
	      throw new RuntimeException(e); 
	    }
	}
	public Connection conectar() throws SQLException{
	      String servidor = "localhost";
	      String porta = "5432";
	      String database = "mportal";
	      String usuario = "postgres";
	      String senha = "admin";
	      return DriverManager.getConnection("jdbc:postgresql://"+servidor+":"+porta+"/"+database+"?user="+usuario+"&password="+senha); 
    }
}
