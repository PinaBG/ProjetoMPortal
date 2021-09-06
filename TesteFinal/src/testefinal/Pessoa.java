package testefinal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import java.io.IOException;

public class Pessoa {
	private int codigo;
	private String nome,ultnome,email,sexo,ip,nasc;
	private int idade,nH,nM;
	
	//MÉTODOS CONSTRUTORES
	public Pessoa() {
		
	}
	public Pessoa(String nome, String ultnome, String email, String sexo, String ip, int idade, String nasc) {
		this.setNome(nome);
		this.setUltNome(ultnome);
		this.setEmail(email);
		this.setSexo(sexo);
		this.setIp(ip);
		this.setIdade(idade);
		this.setNasc(nasc);		
	}
	//MÉTODOS DE ACESSO
	public int getCodigo() {
		return this.codigo;
	}
	public String getNome() {
		return this.nome;
	}
	public String getUltNome() {
		return this.ultnome;
	}
	public String getEmail() {
		return this.email;
	}
	public String getSexo() {
		return this.sexo;
	}
	public String getIp() {
		return this.ip;
	}
	public int getIdade() {
		return this.idade;
	}
	public String getNasc() {
		return this.nasc;
	}
	public int getnH() {
		return this.nH;
	}
	public int getnM() {
		return this.nM;
	}
	//MÉTODOS MODIFICADORES
	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	public void setNome(String nome) {
		this.nome = nome;  
	}
	public void setUltNome(String ultnome) {
		this.ultnome = ultnome;  
	}
	public void setEmail(String email) {
		this.email = email;  
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;  
	}
	public void setIp(String ip) {
		this.ip = ip;  
	}
	public void setIdade(int idade) {
		this.idade = idade;  
	}
	public void setNasc(String nasc) {
		this.nasc = nasc;  
	}
	public void setnH(int nH) {
		this.nH = nH;
	}
	public void setnM(int nM) {
		this.nM = nM;
	}
	//MÉTODOS DE BD
	public void incluir (Connection conn){
		String sqlInsert = "INSERT INTO pessoa(nome, ultnome, email, sexo, ip, idade, nascimento) VALUES (?, ?, ?, ?, ?, ?, ?)";
	      
	    try (PreparedStatement stm = conn.prepareStatement(sqlInsert);){
	    	stm.setString(1, getNome());
	        stm.setString(2, getUltNome());
	        stm.setString(3, getEmail());
	        stm.setString(4, getSexo());
	        stm.setString(5, getIp());
	        stm.setInt(6, getIdade());
	        stm.setString(7, getNasc());
	        stm.execute();
	    }catch (Exception e){
	        e.printStackTrace();
	        try{
	        	conn.rollback();
	        }
	        catch (SQLException e1){
	        	System.out.println(e1.getStackTrace());
	        }
	    }	      
	}
	public void excluir(Connection conn) {
		String sqlDelete = "DELETE FROM pessoa;ALTER SEQUENCE pessoa_codigo_seq RESTART WITH 1";
	    try (PreparedStatement stm = conn.prepareStatement(sqlDelete);){
	        stm.execute();
	    }catch (Exception e){
	        e.printStackTrace();
	        try{
	        	conn.rollback();
	        }catch (SQLException e1){
	        	System.out.print(e1.getStackTrace());
	        }
	    }
	}
	public void atualizar(Connection conn){
		String sqlUpdate = "UPDATE pessoa SET nascimento=? WHERE codigo = ?";
	   
		try (PreparedStatement stm = conn.prepareStatement(sqlUpdate);){
			stm.setString(1, getNasc());
	        stm.setInt(2, getCodigo());
	        stm.execute();
		}catch (Exception e){
			e.printStackTrace();
	         
	        try{
	        	conn.rollback();
	        }catch (SQLException e1){
	        	System.out.print(e1.getStackTrace());
	        }
	    }
	}
	public ArrayList<Pessoa> carregar(Connection conn, String sql) {
		String sqlSelect = sql;
	    ArrayList<Pessoa> lista = new ArrayList<>();
	    try(PreparedStatement stm = conn.prepareStatement(sqlSelect)){
	    	ResultSet rs = stm.executeQuery();
	        while(rs.next()){	        	
	        	Pessoa p = new Pessoa(); 
	            p.setCodigo(rs.getInt("codigo"));
	            p.setNome(rs.getString("nome"));
	            p.setUltNome(rs.getString("ultnome"));
	            p.setEmail(rs.getString("email"));
	            p.setSexo(rs.getString("sexo"));	            
	            p.setIp(rs.getString("ip"));               
	            p.setIdade(rs.getInt("idade"));
	            p.setNasc(rs.getString("nascimento"));
	            lista.add(p);
	        }	        
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    return lista;
	}
	//MÉTODO DE CSV
	public void carregaCSV(Connection conn) throws IOException{
		File csvselecionado = null;
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		int returnValue = jfc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            csvselecionado = jfc.getSelectedFile();
        }
        BufferedReader br = new BufferedReader(new FileReader(csvselecionado));
        String linha = "";
        
        try {
        	while((linha=br.readLine())!=null) {
        		
        		String[] count = linha.split(",");
        		if(!count[0].equals("Nome")) {
        			Pessoa p = new Pessoa();
    	            p.setNome(count[0]);
    	            p.setUltNome(count[1]);
    	            p.setEmail(count[2]);
    	            p.setSexo(count[3]);
    	            p.setIp(count[4]);               
    	            p.setIdade(Integer.parseInt(count[5]));
    	            p.setNasc(count[6]);
    	            p.incluir(conn);    	            
        		}
        	}
        }catch(FileNotFoundException e) {
        	e.printStackTrace();
        }
	}
	public void exportaCSV(Connection conn) {
        String sqlCopy ="COPY (SELECT * FROM pessoa ORDER BY codigo) TO '/tmp/csvNovo.csv' (format CSV);"; 
	    try (PreparedStatement stm = conn.prepareStatement(sqlCopy);){
	        stm.execute();
	    }catch (Exception e){
	        e.printStackTrace();
	        try{
	        	conn.rollback();
	        }
	        catch (SQLException e1){
	        	System.out.println(e1.getStackTrace());
	        }
	    }	      
	}
}
