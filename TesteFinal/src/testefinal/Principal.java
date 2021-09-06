package testefinal;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

@SuppressWarnings("serial")
public class Principal extends JFrame implements ActionListener{	
	private Connection conn;
	//Ajuste de tela:
	final int LARGURA_TELA = 1000;
	final int ALTURA_TELA = 600;
	final int LARGURA_SCROLL_PANE = LARGURA_TELA - 215;
	final int ALTURA_SCROLL_PANE = ALTURA_TELA - 45;
	private Container container;
	private JFrame frame;
	//Comandos SQL
	String carregaTudo="SELECT * FROM pessoa ORDER BY codigo";
	String ordenaNome="SELECT * FROM pessoa ORDER BY nome";
	//Variaveis de auxilio
	int numHomens;
	int numMulheres;
	int sumIHomens;
	int sumIMulheres;
	//Conteúdo do JPanel Leste:
	private JTable tblPessoas;
	private JPanel pnlLeste;
	private String[] colunas = {"Código","Nome", "Último Nome", "Email", "Sexo", "IP", "Idade", "Data de Nascimento"};
	private Object[][] pessoas;
	private JScrollPane rolagem;
	//Conteúdo do JPanel Oeste:
	private JLabel lblApresentacao;
	private JButton btnOrdenar;
	private JButton btnContador;
	private JButton btnData;
	private JButton btnMedia;
	private JButton btnSalvar;
	private JButton btnCarregar;
	
	public Principal() {
		super("Projeto para teste final Multiportal");
		try{
			conn = null;
	        ConexaoBD bd = new ConexaoBD();
	        conn = bd.conectar();        
		}catch (Exception e){
	        System.out.println(e);
		}
	    
		//Montando JPanels		
	    //JPanel Leste
		pnlLeste = new JPanel(new FlowLayout());
		Pessoa pessoa = new Pessoa();
		instanciaJTableEScrollPane(pessoa.carregar(conn,carregaTudo));
		//JPanel Oeste
		JPanel pnlOeste = new JPanel(new GridLayout(7,1));
		lblApresentacao = new JLabel("Bem vindo!");
		lblApresentacao.setHorizontalAlignment(SwingConstants.CENTER);
		btnOrdenar = new JButton("Ordenar pelo nome");
		btnContador = new JButton("Contar Homens e Mulheres");
		btnData = new JButton("Consertar nascimento");
		btnMedia = new JButton("Média de idade");
		btnSalvar = new JButton("Salvar CSV");
		btnCarregar = new JButton("Carregar CSV");
		
		pnlOeste.add(lblApresentacao);
		pnlOeste.add(btnOrdenar);
		pnlOeste.add(btnContador);
		pnlOeste.add(btnData);
		pnlOeste.add(btnMedia);
		pnlOeste.add(btnSalvar);
		pnlOeste.add(btnCarregar);
		
		pnlLeste.add(rolagem);
		//ActionListener dos botões
		btnOrdenar.addActionListener(this);
		btnContador.addActionListener(this);
		btnData.addActionListener(this);
		btnMedia.addActionListener(this);
		btnSalvar.addActionListener(this);
		btnCarregar.addActionListener(this);
	    
	  //----------------------------------------------
		container = getContentPane();
	    container.setLayout(new BorderLayout());
	    
	    container.add(pnlLeste, BorderLayout.EAST);
	    container.add(pnlOeste, BorderLayout.WEST);
	    
	    setSize(LARGURA_TELA, ALTURA_TELA);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true);
	    setResizable(false);
	    setLocationRelativeTo(null);
	}
	//MÉTODO MAIN
	public static void main (String[] args) {
		new Principal();
	}
	
	//AÇÃO DOS BOTÕES
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnCarregar){
			Pessoa pessoa = new Pessoa();
			pessoa.excluir(conn);
			try {
				pessoa.carregaCSV(conn);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			container.remove(pnlLeste);
			pnlLeste.remove(rolagem);
            
            instanciaJTableEScrollPane(pessoa.carregar(conn,carregaTudo)); 
            
            pnlLeste.add(rolagem);
            container.add(pnlLeste, BorderLayout.CENTER);
           
            validate();            
            repaint();
		}else if(e.getSource() == btnOrdenar) {
			Pessoa pessoa = new Pessoa();
			container.remove(pnlLeste);
			pnlLeste.remove(rolagem);
            
            instanciaJTableEScrollPane(pessoa.carregar(conn,ordenaNome)); 
            
            pnlLeste.add(rolagem);
            container.add(pnlLeste, BorderLayout.CENTER);
           
            validate();            
            repaint();
		}else if(e.getSource() == btnContador) {
			Pessoa pessoa = new Pessoa();
			pessoa.carregar(conn,carregaTudo);
			JOptionPane.showMessageDialog(frame,
			        "No documento há um total de:\n\n" + numHomens + " Homens e\n" + numMulheres + " Mulheres" , 
			        "Contagem",
			        JOptionPane.INFORMATION_MESSAGE);
		}else if(e.getSource() == btnMedia) {
			Pessoa pessoa = new Pessoa();
			pessoa.carregar(conn,carregaTudo);
			double mediaH = sumIHomens/numHomens;
			double mediaM = sumIMulheres/numMulheres;
			System.out.println(sumIHomens);
			System.out.println(sumIMulheres);
			JOptionPane.showMessageDialog(frame,
			        "No documento a média de idade de Homens e Mulheres é igual a:\n\n"+ mediaH + " Homens e\n" + mediaM + " Mulheres" , 
			        "Média",
			        JOptionPane.INFORMATION_MESSAGE);
		}else if(e.getSource() == btnData) {
			Pessoa pessoa = new Pessoa();
			consertadata(conn);
			container.remove(pnlLeste);
			pnlLeste.remove(rolagem);		
			
			instanciaJTableEScrollPane(pessoa.carregar(conn,carregaTudo)); 
            
            pnlLeste.add(rolagem);
            container.add(pnlLeste, BorderLayout.CENTER);
            
            validate();            
            repaint();
		}else {
			Pessoa pessoa = new Pessoa();
			pessoa.exportaCSV(conn);
			JOptionPane.showMessageDialog(frame,
			        "Arquivo salvo em:\n\n"+" /tmp/csvNovo.csv", 
			        "Contagem",
			        JOptionPane.INFORMATION_MESSAGE);
		}
		
	}	
	//MÉTODOS DA JTABLE
	public String[][] carregaDados(ArrayList<Pessoa> listaPessoas){
		Pessoa pessoa = new Pessoa();
	    ArrayList<Pessoa> lista = listaPessoas;
	    numHomens=0;
	    numMulheres=0;
	    sumIHomens=0;
	    sumIMulheres=0;
	    String[][] saida = new String[lista.size()][colunas.length];
	    for(int i = 0; i < lista.size(); i++){
	    	pessoa = lista.get(i);
	        saida[i][0] = pessoa.getCodigo()+"";
	        saida[i][1] = pessoa.getNome();
	        saida[i][2] = pessoa.getUltNome();
	        saida[i][3] = pessoa.getEmail();
	        saida[i][4] = pessoa.getSexo();
	        if(pessoa.getSexo().equals("Male")) {
	        	numHomens++;
	        	sumIHomens+=pessoa.getIdade();
	        }else {
	        	numMulheres++;
	        	sumIMulheres+=pessoa.getIdade();
	        }
	        saida[i][5] = pessoa.getIp();
	        saida[i][6] = String.valueOf(pessoa.getIdade());
	        saida[i][7] = pessoa.getNasc();
	    }	      
	    return saida;
	}
	public void instanciaJTableEScrollPane(ArrayList<Pessoa> listaPessoas){
		//carrega a matriz de pedidos para instanciar a JTable
	    pessoas = carregaDados(listaPessoas);
	    tblPessoas = new JTable(pessoas, colunas);
	    rolagem = new JScrollPane(tblPessoas);
	    rolagem.setPreferredSize(new Dimension(LARGURA_SCROLL_PANE, ALTURA_SCROLL_PANE));
	}
	public void consertadata(Connection conn){
		Pessoa pessoa = new Pessoa();
		ArrayList<Pessoa> lista = pessoa.carregar(conn, carregaTudo);
		Calendar cal = GregorianCalendar.getInstance();
		int ano = cal.get(Calendar.YEAR);
		String auxano;
		for(int i=0;i<lista.size();i++) {
			String arraydata[] = new String[3];
			pessoa = lista.get(i);
			arraydata = pessoa.getNasc().split("/");
			auxano = Integer.toString(ano-pessoa.getIdade());
			arraydata[2]=auxano;
			pessoa.setNasc(arraydata[0]+"/"+arraydata[1]+"/"+arraydata[2]);
			pessoa.atualizar(conn);
		}
	}
}
