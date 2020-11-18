package Servlet;

import Beans.Domanda;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import Database.DatabaseQuery;
import com.amazonaws.demos.polly.PollyDemo;
import com.amazonaws.demos.polly.SentimentAnalisys;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lexruntime.AmazonLexRuntime;
import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder;
import com.amazonaws.services.lexruntime.model.Button;
import com.amazonaws.services.lexruntime.model.GenericAttachment;
import com.amazonaws.services.lexruntime.model.PostTextRequest;
import com.amazonaws.services.lexruntime.model.PostTextResult;
import com.amazonaws.services.lexruntime.model.ResponseCard;
import com.google.gson.Gson;

import Beans.Paziente;
import javazoom.jl.decoder.JavaLayerException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.amazonaws.demos.polly.BotClass;

/**
 * Servlet implementation class BotServlet
 */
@WebServlet("/BotServlet")

public class BotServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static int phase=0;
	public static int pass=0;   
	public static int newOne=0;
	public static PollyDemo p= new PollyDemo(Region.getRegion(Regions.US_EAST_1),"Giorgio"); 
	public static AmazonLexRuntime client=AmazonLexRuntimeClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
	public static PostTextRequest textRequest=new PostTextRequest();
	public static ResponseCard card=new ResponseCard();
	public static ArrayList<Button> button=new  ArrayList<Button>();
	public static ArrayList<GenericAttachment> scelte= new ArrayList<GenericAttachment>(); 
	public static GenericAttachment scelta= new GenericAttachment();
	public static SentimentAnalisys sentimentAnalyzer= new SentimentAnalisys();
	public static ArrayList<Integer> points=new ArrayList<Integer>();//sostituire con un vettore punteggio, e un sistema di SQL
	public static int countRisp=0;
	public static int test1=0;
	public static Paziente paziente=new Paziente();
	
	public static int id;
	String requestText = null;
	PostTextResult textResult = null;
	String x = null;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BotServlet() {
        super();
       
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		Gson gson = new Gson();
		String risp = request.getParameter("risposta");
		Scanner scan=new Scanner(System.in);
		System.out.println(risp);
		HttpSession session = request.getSession();
		
		
		
		
		
		
		if(newOne==0) {
		
		textRequest.setBotName("Parliere");
		textRequest.setBotAlias("parliere");
		textRequest.setUserId("testuser");
		}
		
		
		
		while(true) {
			 //requestText=scan.nextLine().trim();
			
		//textRequest.setInputText(requestText);//Inserisce il testo inserito nell'input
		//textRequest.setInputText("Piacere") per inserire l'intent del Piacere
		//PostTextResult textResult=client.postText(textRequest);//Invia l'imput a Lex e ottiene la risposta
		
		
		if(phase==0 ) { //chiede le informazioni di base
			if(pass==0) { //viene eseguito una sola volta
			textRequest.setInputText("Intro"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);//Invia l'imput a Lex e ottiene la risposta
			 newOne++;
			}
			if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
				
				System.out.println("Points = "+ points);
				//System.out.println(textResult.getMessage());
				if(test1==0) {
				String risposta = (new Gson()).toJson(textResult.getMessage());
		        response.getWriter().write(risposta);
		        
				try {
					
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				pass++;
				if(test1==0) {
					test1++;
					break;			
				}
				if(textResult.getMessage().equals("Come ti chiami?"))
					paziente.setNome(risp);
				else if(textResult.getMessage().equals("Qual'è il tuo cognome?"))
					paziente.setCognome(risp);
				else if(textResult.getMessage().equals("Sesso"))
					paziente.setSesso(risp);
				else if(textResult.getMessage().equals("Regione?"))
					paziente.setRegione(risp);
				else if(textResult.getMessage().equals("Provincia?"))
					paziente.setProvincia(risp);
				else if(textResult.getMessage().equals("Titolo di studio?"))
					paziente.setTitolo_di_studio(risp);
				else if(textResult.getMessage().equals("Età?")) {
					if(Integer.parseInt(risp)<18||Integer.parseInt(risp)>70) {
						try {
							p.audioSynth("Scusa ma questo test è disponibile solo da 18 ai 70 anni di età");
						} catch (IOException | JavaLayerException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						phase=30;
						break;
					}
						paziente.setEta(Integer.parseInt(risp));
				}
					
				else if(textResult.getMessage().equals("Stato civile?"))
					paziente.setStato_civile(risp);
				
				requestText=risp;
				textRequest.setInputText(requestText);
				textResult=client.postText(textRequest);
				 if(textResult.getMessage()!=null) {
				String risposta = (new Gson()).toJson(textResult.getMessage());
		        response.getWriter().write(risposta.replaceAll("è", "&#232;").replaceAll("à", "&#224;"));
				}
		        if(textResult.getMessage()==null) {
		        	 
		        	test1=0;
		        	continue;
		        	
		        }
				try {
					
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			}
			else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			
				phase++;
				pass=1;
				try {
					DatabaseQuery.addUser(paziente);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					id=DatabaseQuery.getLastId();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
				System.out.println("Completato");
				System.out.println(textResult.getMessage());
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			else if(textResult.getDialogState().equals("Failed")) {
				System.out.println(textResult.getMessage());
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
				
				
			}
		
		
		if(phase==1) {
			if(pass==1) {
			textRequest.setInputText("Tristezza"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			//inserire break
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0)+"\">"+choises.get(0) +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1)+"\">"+choises.get(1) +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ì", "i")+"\">"+choises.get(2).replaceAll("ì", "&#236;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3)+"\">"+choises.get(3) +"</button>\r\n" +
					
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Cosa significa per te")) {
				if(textRequest.getInputText().equalsIgnoreCase("Mi sento triste per la maggior parte del tempo")) {
					points.add(1);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Mi sento sempre triste")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Mi sento cosi triste o infelice da non poterlo sopportare")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Non mi sento triste")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta);
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			}
			}
			
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			Domanda domanda= new Domanda();
			sentimentAnalyzer.analizzaSentimento(risp);
			
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(1);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		  System.out.println("fase finale");
			phase=2;
			pass=1;
			countRisp=0;
			test1=0;
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
			
			
		}
		
		
		if(phase==2) {
			if(pass==1) {
			textRequest.setInputText("Pessimismo"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0)+"\">"+choises.get(0) +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1)+"\">"+choises.get(1) +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u")+"\">"+choises.get(2).replaceAll("ù", "&#249;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3).replaceAll("è", "e").replaceAll("à", "a")+"\">"+choises.get(3).replaceAll("è", "&#233;").replaceAll("à", "&#225;") +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("precisamente")) {
				if(textRequest.getInputText().equalsIgnoreCase("Mi sento piu scoraggiato riguardo al mio futuro rispetto al solito")) {
					points.add(1);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Non mi aspetto nulla di buono per me")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Avverto che il mio futuro e senza speranza e che continuerà a peggiorare")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Non sono scoraggiato riguardo al mio futuro")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			}
			
			
			
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);
			
			
			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(2);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
			
			
			
			
			
		}
		
		
		
		if(phase==3) {
			if(pass==1) {
			textRequest.setInputText("Fallimento"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0)+"\">"+choises.get(0) +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1)+"\">"+choises.get(1) +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u")+"\">"+choises.get(2).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3)+"\">"+choises.get(3) +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("precisamente")) {
				if(textRequest.getInputText().equalsIgnoreCase("Ho fallito piu di quanto avrei dovuto")) {
					points.add(1);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Se ripenso alla mia vita riesco a vedere solo una serie di fallimenti")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Ho la sensazione di essere un fallimento totale come persona")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Non mi sento un fallito")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			
			}
			}
			
		
			
		}	
			
			
			
		
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);
			
			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(3);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		if(phase==4) {
			if(pass==1) {
			textRequest.setInputText("Piacere"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0)+"\">"+choises.get(0) +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1).replaceAll("ù", "u")+"\">"+choises.get(1).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u")+"\">"+choises.get(2).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3)+"\">"+choises.get(3) +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Spiega meglio")) {
				if(textRequest.getInputText().equalsIgnoreCase("Non traggo piu piacere dalle cose come un tempo")) {
					points.add(1);
				}
				if(textRequest.getInputText().contains("Traggo molto poco piacere dalle cose che di solito mi divertivano")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Non riesco a trarre alcun piacere dalle cose che una volta mi piacevano")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Traggo lo stesso piacere di sempre dalle cose che faccio")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			
			}
			}
			
		
			
		}	
			
			
			
			

			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);
			
			
			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(4);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		
		
		if(phase==5) {
			if(pass==1) {
			textRequest.setInputText("Senso di Colpa"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0)+"\">"+choises.get(0) +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1)+"\">"+choises.get(1) +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u")+"\">"+choises.get(2).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3)+"\">"+choises.get(3) +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("precisamente")) {
				if(textRequest.getInputText().equalsIgnoreCase("Mi sento in colpa per molte cose che ho fatto o che avrei dovuto fare")) {
					points.add(1);
				}
				if(textRequest.getInputText().contains("Mi sento molto spesso in colpa")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Mi sento sempre in colpa")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Non mi sento particolarmente in colpa")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
				}
				}
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			
			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(5);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		if(phase==6) {
			if(pass==1) {
			textRequest.setInputText("Punizione"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0)+"\">"+choises.get(0) +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1)+"\">"+choises.get(1) +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u")+"\">"+choises.get(2).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3)+"\">"+choises.get(3) +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Che intendi")) {
				if(textRequest.getInputText().equalsIgnoreCase("Sento che potrei essere punito")) {
					points.add(1);
				}
				if(textRequest.getInputText().contains("Mi aspetto di essere punito")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Mi sento come se stessi subendo una punizione")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Non mi sento come se stessi subendo una punizione")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			
			
			}
			}
			
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);
			
			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(6);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		if(phase==7) {
			if(pass==1) {
			textRequest.setInputText("Autostima"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0)+"\">"+choises.get(0) +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1)+"\">"+choises.get(1) +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u")+"\">"+choises.get(2).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3)+"\">"+choises.get(3) +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Più precisamente")) {
				if(textRequest.getInputText().equalsIgnoreCase("Credo meno in me stesso")) {
					points.add(1);
				}
				if(textRequest.getInputText().contains("Sono deluso di me stesso")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Mi detesto")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Considero me stesso come ho sempre fatto")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			}
			}
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);
			
			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(7);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		if(phase==8) {
			if(pass==1) {
			textRequest.setInputText("Autocritica"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0).replaceAll("ù", "u").replaceAll("è", "e")+"\">"+choises.get(0).replaceAll("ù","&#249;").replaceAll("è", "&#232;") +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1).replaceAll("è", "e")+"\">"+choises.get(1).replaceAll("è", "&#232;") +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u").replaceAll("è", "e")+"\">"+choises.get(2).replaceAll("ù","&#249;").replaceAll("è", "&#232;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3).replaceAll("è", "e")+"\">"+choises.get(3).replaceAll("è", "&#232;") +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Cosa significa")) {
				if(textRequest.getInputText().equalsIgnoreCase("Mi critico piu spesso del solito")) {
					points.add(1);
				}
				if(textRequest.getInputText().contains("Mi critico per tutte le mie colpe")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Mi biasimo per ogni cosa brutta che mi accade")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Non mi critico ne mi biasimo più del solito")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			}
			}
			}
			
			
			
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);
			
			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(8);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		if(phase==9) {
			if(pass==1) {
			textRequest.setInputText("Suicidio"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			

			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0)+"\">"+choises.get(0) +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1)+"\">"+choises.get(1) +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u")+"\">"+choises.get(2).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3)+"\">"+choises.get(3) +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Come ti senti")) {
				if(textRequest.getInputText().equalsIgnoreCase("Ho pensieri suicidi ma non li realizzerei mai")) {
					points.add(1);
				}
				if(textRequest.getInputText().contains("Sento che starei meglio se morissi")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Se mi si presentasse l'occasione non esiterei ad uccidermi")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Non ho alcun pensiero suicida")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			}
			}
			}
			
			
			
		
			
			
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);
			
			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(9);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		if(phase==10) {
			if(pass==1) {
			textRequest.setInputText("Pianto"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0).replaceAll("ù", "u")+"\">"+choises.get(0).replaceAll("ù","&#249;") +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1).replaceAll("ù", "u")+"\">"+choises.get(1).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u")+"\">"+choises.get(2).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3).replaceAll("ù", "u")+"\">"+choises.get(3).replaceAll("ù","&#249;") +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Che intendi")) {
				if(textRequest.getInputText().equalsIgnoreCase("Piango piu del solito")) {
					points.add(1);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Piango per ogni minima cosa")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Ho spesso voglia di piangere ma non ci riesco")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Non piango piu del solito")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			}
			}
			}
			
			
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);
			
			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(10);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		if(phase==11) {
			if(pass==1) {
			textRequest.setInputText("Agitazione"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0).replaceAll("ù", "u").replaceAll("è", "e").replaceAll("ì", "i")+"\">"+choises.get(0).replaceAll("ù","&#249;").replaceAll("è", "&#233;").replaceAll("ì", "&#237;") +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1).replaceAll("ù", "u").replaceAll("è", "e").replaceAll("ì", "i")+"\">"+choises.get(1).replaceAll("ù","&#249;").replaceAll("è", "&#233;").replaceAll("ì", "&#237;") +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u").replaceAll("è", "e").replaceAll("ì", "i")+"\">"+choises.get(2).replaceAll("ù","&#249;").replaceAll("è", "&#233;").replaceAll("ì", "&#237;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3).replaceAll("ù", "u").replaceAll("è", "e").replaceAll("ì", "i")+"\">"+choises.get(3).replaceAll("ù","&#249;").replaceAll("è", "&#233;").replaceAll("ì", "&#237;") +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Più specificamente")) {
				if(textRequest.getInputText().equalsIgnoreCase("Mi sento piu agitato o teso del solito")) {
					points.add(1);
				}
				if(textRequest.getInputText().contains("Sono cosi nervoso o agitato al punto che mi e difficile rimanere fermo")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Sono cosi nervoso o agitato che devo continuare a muovermi o fare qualcosa")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Non mi sento piu teso o agitato del solito")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			}
			}
			}
			
			
			
		
			
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);

			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(11);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		if(phase==12) {
			if(pass==1) {
			textRequest.setInputText("Perdita di interessi"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0).replaceAll("à", "a")+"\">"+choises.get(0).replaceAll("à", "&#225;") +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1).replaceAll("à", "a")+"\">"+choises.get(1).replaceAll("à", "&#225;") +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("à", "a")+"\">"+choises.get(2).replaceAll("à", "&#225;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3).replaceAll("à", "a")+"\">"+choises.get(3).replaceAll("à", "&#225;") +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("In che modo")) {
				if(textRequest.getInputText().equalsIgnoreCase("Sono meno interessato agli altri o a alle cose rispetto a prima")) {
					points.add(1);
				}
				if(textRequest.getInputText().contains("Ho perso la maggior parte dell'interesse verso le altre persone o cose")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Mi risulta difficile interessarmi a qualsiasi cosa")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Non ho perso interesse verso le altre persone o verso le attivita")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			}
			}
			}
			
			
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);

			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(12);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		if(phase==13) {
			if(pass==1) {
			textRequest.setInputText("Indecisione"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0).replaceAll("ù", "u").replaceAll("à", "a")+"\">"+choises.get(0).replaceAll("ù","&#249;").replaceAll("à", "&#225;") +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1).replaceAll("ù", "u").replaceAll("à", "a")+"\">"+choises.get(1).replaceAll("ù","&#249;").replaceAll("à", "&#225;") +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u").replaceAll("à", "a")+"\">"+choises.get(2).replaceAll("ù","&#249;").replaceAll("à", "&#225;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3).replaceAll("ù", "u").replaceAll("à", "a")+"\">"+choises.get(3).replaceAll("ù","&#249;").replaceAll("à", "&#225;") +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Spiega meglio")) {
				if(textRequest.getInputText().equalsIgnoreCase("Trovo piu difficolta del solito nel prendere decisioni")) {
					points.add(1);
				}
				if(textRequest.getInputText().contains("Ho molte piu difficolta del solito nel prendere decisioni")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Non riesco a prendere nessuna decisione")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Prendo decisioni come sempre")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			}
			}
			}
			
			
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);

			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(13);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		if(phase==14) {
			if(pass==1) {
			textRequest.setInputText("Inutilità"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0).replaceAll("ù", "u")+"\">"+choises.get(0).replaceAll("ù","&#249;") +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1).replaceAll("ù", "u")+"\">"+choises.get(1).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u")+"\">"+choises.get(2).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3).replaceAll("ù", "u")+"\">"+choises.get(3).replaceAll("ù","&#249;") +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Più precisamente")) {
				if(textRequest.getInputText().equalsIgnoreCase("Non mi sento valido e utile come un tempo")) {
					points.add(1);
				}
				if(textRequest.getInputText().contains("Mi sento piu inutile delle altre persone")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Mi sento completamente inutile su qualsiasi cosa")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Non mi sento inutile")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			}
			}
			}
			
			
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);

			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(14);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		
		if(phase==15) {
			if(pass==1) {
			textRequest.setInputText("Perdita di energia"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0).replaceAll("ì", "i")+"\">"+choises.get(0).replaceAll("ì","&#237;") +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1).replaceAll("ì", "i")+"\">"+choises.get(1).replaceAll("ì","&#237;") +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ì", "i")+"\">"+choises.get(2).replaceAll("ì","&#237;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3).replaceAll("ì", "i")+"\">"+choises.get(3).replaceAll("ì","&#237;") +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Che cosa intendi")) {
				if(textRequest.getInputText().equalsIgnoreCase("Ho meno energia del solito")) {
					points.add(1);
				}
				if(textRequest.getInputText().contains("Non ho energia sufficiente per fare la maggior parte delle cose")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Ho cosi poca energia che non riesco a fare nulla")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Ho la stessa energia di sempre")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			}
			}
			}
			
			
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);

			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(15);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		
		if(phase==16) {
			if(pass==1) {
			textRequest.setInputText("Sonnolenza"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				x=button.get(i).getText();
				System.out.println(button.get(i).getText());
				countRisp++;
				choises.add(x);
				}
				scelta=scelte.get(1);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
					System.out.println(button.get(i).getText());
					x=button.get(i).getText();
					countRisp++;
					choises.add(x);
					
					}
				
				
							}
			
		
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0).replaceAll("ù", "u")+"\">"+choises.get(0).replaceAll("ù","&#249;") +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1).replaceAll("ù", "u")+"\">"+choises.get(1).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u")+"\">"+choises.get(2).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3).replaceAll("ù", "u")+"\">"+choises.get(3).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but5\" value=\""+choises.get(4).replaceAll("ù", "u")+"\">"+choises.get(4).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but6\" value=\""+choises.get(5).replaceAll("ù", "u")+"\">"+choises.get(5).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but7\" value=\""+choises.get(6).replaceAll("ù", "u")+"\">"+choises.get(6).replaceAll("ù","&#249;") +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttonsPLUS.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Spiega meglio")) {
				if(textRequest.getInputText().equalsIgnoreCase("Dormo un po piu del solito")) {
					points.add(1);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Dormo un po meno del solito")) {
					points.add(1);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Dormo molto piu del solito")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Dormo molto meno del solito")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Dormo quasi tutto il giorno")) {
					points.add(3);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Mi sveglio ogni 1-2 ore prima e non riesco a riaddormentarmi")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Non ho notato alcun cambiamento nel mio modo di dormire")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			
			}
			}
			}
			
			
			
	
			
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);

			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(16);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		if(phase==17) {
			if(pass==1) {
			textRequest.setInputText("Irritabilità"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0).replaceAll("ù", "u")+"\">"+choises.get(0).replaceAll("ù","&#249;") +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1).replaceAll("ù", "u")+"\">"+choises.get(1).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u")+"\">"+choises.get(2).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3).replaceAll("ù", "u")+"\">"+choises.get(3).replaceAll("ù","&#249;") +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Spiega meglio")) {
				if(textRequest.getInputText().equalsIgnoreCase("Sono piu irritabile del solito")) {
					points.add(1);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Sono molto piu irritabile del solito")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Sono sempre irritabile")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Non sono piu irritabile del solito")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			}
			}
			}
			
			
			
			
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);

			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(17);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		if(phase==18) {
			if(pass==1) {
			textRequest.setInputText("Appetito"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			

			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				x=button.get(i).getText();
				System.out.println(button.get(i).getText());
				countRisp++;
				choises.add(x);
				}
				scelta=scelte.get(1);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
					System.out.println(button.get(i).getText());
					x=button.get(i).getText();
					countRisp++;
					choises.add(x);
					
					}
				
				
							}
			
		
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0).replaceAll("è", "e")+"\">"+choises.get(0).replaceAll("è", "&#232;") +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1).replaceAll("è", "e")+"\">"+choises.get(1).replaceAll("è", "&#232;") +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u").replaceAll("è", "e")+"\">"+choises.get(2).replaceAll("ù","&#249;").replaceAll("è", "&#232;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3).replaceAll("è", "e")+"\">"+choises.get(3).replaceAll("è", "&#232;") +"</button>\r\n" +
					"  <button id=\"but5\" value=\""+choises.get(4).replaceAll("è", "e")+"\">"+choises.get(4).replaceAll("è", "&#232;") +"</button>\r\n" +
					"  <button id=\"but6\" value=\""+choises.get(5).replaceAll("è", "e")+"\">"+choises.get(5).replaceAll("è", "&#232;") +"</button>\r\n" +
					"  <button id=\"but7\" value=\""+choises.get(6).replaceAll("è", "e")+"\">"+choises.get(6).replaceAll("è", "&#232;") +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttonsPLUS.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Cosa vuol dire")) {
				if(textRequest.getInputText().equalsIgnoreCase("Il mio appetito e un po diminuito rispetto al solito")) {
					points.add(1);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Il mio appetito e un po aumentato rispetto al solito")) {
					points.add(1);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Il mio appetito e molto aumentato rispetto al solito")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Il mio appetito e molto diminuito rispetto al solito")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Mangerei in qualsiasi momento")) {
					points.add(3);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Non ho per niente appetito")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Non ho notato alcun cambiamento nel mio appetito")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			
			}
			}
			}
			
			
			
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);

			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(18);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		if(phase==19) {
			if(pass==1) {
			textRequest.setInputText("Concentrazione"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
			 
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0)+"\">"+choises.get(0) +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1)+"\">"+choises.get(1) +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u")+"\">"+choises.get(2).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3)+"\">"+choises.get(3) +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Spiegati meglio")) {
				if(textRequest.getInputText().equalsIgnoreCase("Non riesco a concentrarmi come al solito")) {
					points.add(1);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Trovo difficile concentrarmi per molto tempo")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Non riesco a concentrarmi su nulla")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Riesco a concentrarmi come sempre")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			}
			}
			}
			
			
			
		
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);

			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(19);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		if(phase==20) {
			if(pass==1) {
			textRequest.setInputText("Rampino"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0).replaceAll("ù", "u").replaceAll("ì", "i")+"\">"+choises.get(0).replaceAll("ù","&#249;").replaceAll("ì", "&#237;") +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1).replaceAll("ù", "u").replaceAll("ì", "i")+"\">"+choises.get(1).replaceAll("ù","&#249;").replaceAll("ì", "&#237;") +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u").replaceAll("ì", "i")+"\">"+choises.get(2).replaceAll("ù","&#249;").replaceAll("ì", "&#237;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3).replaceAll("ù", "u").replaceAll("ì", "i")+"\">"+choises.get(3).replaceAll("ù","&#249;").replaceAll("ì", "&#237;") +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Più precisamente")) {
				if(textRequest.getInputText().equalsIgnoreCase("Mi affatico piu facilmente del solito")) {
					points.add(1);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Non riesco a fare molte delle cose che facevo")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Non riesco piu a fare niente")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Non sono piu stanco o affaticato del solito")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			}
			}
			}

			
			
			
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);

			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(20);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		
		if(phase==21) {
			if(pass==1) {
			textRequest.setInputText("Sesso"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
			 pass++;
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			
			ArrayList<String> choises= new ArrayList<String>();
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
				card=textResult.getResponseCard();
				scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
				scelta=scelte.get(0);
				button=(ArrayList<Button>) scelta.getButtons();
				for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				x=button.get(i).getText();
				choises.add(x);
				}
				}
			if(test1==0) {
			try {
				p.audioSynth(textResult.getMessage()+choises);
				
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n" + 
					"  <button id=\"but1\" value=\""+choises.get(0)+"\">"+choises.get(0) +"</button>\r\n" + 
					"  <button id=\"but2\" value=\""+choises.get(1)+"\">"+choises.get(1) +"</button>\r\n" +
					"  <button id=\"but3\" value=\""+choises.get(2).replaceAll("ù", "u")+"\">"+choises.get(2).replaceAll("ù","&#249;") +"</button>\r\n" +
					"  <button id=\"but4\" value=\""+choises.get(3)+"\">"+choises.get(3) +"</button>\r\n" +
					"<script language=\"text/javascript\" src=\"js/buttons.js\">" + 
					"</div>"); //correggere per inserire tutte le scelte
        	response.getWriter().write(risposta);
        	test1++;
        	session.setAttribute("buttons", true);
        	break;
			}
			
			
			
			
			
			//requestText=scan.nextLine().trim();
			textRequest.setInputText(risp);
			textResult=client.postText(textRequest);
			if(textResult.getMessage()!=null) {
			if(textResult.getMessage().contains("Più precisamente")) {
				if(textRequest.getInputText().equalsIgnoreCase("Sono meno interessato/a al sesso rispetto a prima")) {
					points.add(1);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Ora sono molto meno interessato/a al sesso")) {
					points.add(2);
				}
				if(textRequest.getInputText().equalsIgnoreCase("Ho completamente perso l'interesse verso il sesso")) {
					points.add(3);
				}
				
				if(textRequest.getInputText().equalsIgnoreCase("Non ho notato alcun cambiamento recente nel mio interesse verso il sesso")) {
					points.add(0);
				}
				String risposta = (new Gson()).toJson("<p>"+textResult.getMessage()+"</p><div class=\"btn-group\">\r\n"  +"<button id=\"noresponse\" value=\"Non voglio rispondere\">Non voglio rispondere</button>\r\n </div>"+ "<script language=\"text/javascript\" src=\"js/buttons.js\">"  );
				response.getWriter().write(risposta.replaceAll("ù", "&#249;"));
				try {
					p.audioSynth(textResult.getMessage());
				} catch (IOException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			
			}
			}
			}
			
			
			
			
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(risp);

			Domanda domanda=new Domanda();
			domanda.setGrado(sentimentAnalyzer.getSentimentoPredominante(risp));
			domanda.setN_domanda(21);
			domanda.setValore(risp);
			try {
				DatabaseQuery.addPunteggioExtra(domanda);
				DatabaseQuery.addCompilazioneExtra(DatabaseQuery.getLastIdRisposteExtr(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			test1=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			try {
				p.audioSynth(textResult.getMessage());
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		}
		
		
		if(phase==22) {
			int sum = 0;
			String risposta = (new Gson()).toJson("Grazie per aver partecipato");
			response.getWriter().write(risposta);
			
			
			try {
				p.audioSynth("<p>Grazie per aver partecipato</p><script language=\"text/javascript\" src=\"js/hideTasti.js\">");
			} catch (IOException | JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(int i = 0; i < points.size(); i++) {
			    sum += points.get(i);}
			try {
				DatabaseQuery.addPunteggioBeck(sum);
				DatabaseQuery.addCompilazione(DatabaseQuery.getLastIdTest(), id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}

		
		
		
	}//end while(true)	
		

		
		
		
		
		
		
		
		
		
		
	        
	        
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
