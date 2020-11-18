package Beans;

public class Paziente {
	
	 
		


				private String nome;
				private String cognome;
	    		private int eta;
	    		private String stato_civile;
				private String regione;
	           private String sesso;
	           private String provincia;
	           private String titolo_di_studio;
	           private int id;
	  
	           
	  public Paziente() {}
  

	public Paziente(String nome, String cognome, int eta, String stato_civile, String regione, String sesso,
			String provincia, String titolo_di_studio) {
		this.nome = nome;
		this.cognome = cognome;
		this.eta = eta;
		this.stato_civile = stato_civile;
		this.regione = regione;
		this.sesso = sesso;
		this.provincia = provincia;
		this.titolo_di_studio = titolo_di_studio;
	}
	
	public String getNome() {
	return nome;
}


public void setNome(String nome) {
	this.nome = nome;
}


public String getCognome() {
	return cognome;
}


public void setCognome(String cognome) {
	this.cognome = cognome;
}


public int getEta() {
	return eta;
}


public void setEta(int eta) {
	this.eta = eta;
}


public String getStato_civile() {
	return stato_civile;
}


public void setStato_civile(String stato_civile) {
	this.stato_civile = stato_civile;
}


public String getRegione() {
	return regione;
}


public void setRegione(String regione) {
	this.regione = regione;
}


public String getSesso() {
	return sesso;
}


public void setSesso(String sesso) {
	this.sesso = sesso;
}


public String getProvincia() {
	return provincia;
}


public void setProvincia(String provincia) {
	this.provincia = provincia;
}


public String getTitolo_di_studio() {
	return titolo_di_studio;
}


public void setTitolo_di_studio(String titolo_di_studio) {
	this.titolo_di_studio = titolo_di_studio;
}


public int getId() {
	return id;
}


public void setId(int id) {
	this.id = id;
}
	
}
	

