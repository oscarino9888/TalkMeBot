package Beans;

public class Domanda {

	private int n_domanda;
	private String grado;
	private String valore;
	private int id;
	
	
	public Domanda() {
		
	}
	
	public Domanda(int n_domanda, String grado, String valore) {
		super();
		this.n_domanda = n_domanda;
		this.grado = grado;
		this.valore = valore;
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getN_domanda() {
		return n_domanda;
	}
	public void setN_domanda(int n_domanda) {
		this.n_domanda = n_domanda;
	}
	public String getGrado() {
		return grado;
	}
	public void setGrado(String grado) {
		this.grado = grado;
	}
	public String getValore() {
		return valore;
	}
	public void setValore(String valore) {
		this.valore = valore;
	}
	
	
	
	
}
