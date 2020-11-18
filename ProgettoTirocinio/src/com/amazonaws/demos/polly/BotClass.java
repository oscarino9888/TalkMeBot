package com.amazonaws.demos.polly;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lexruntime.AmazonLexRuntime;
import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder;
import com.amazonaws.services.lexruntime.model.Button;
import com.amazonaws.services.lexruntime.model.GenericAttachment;
import com.amazonaws.services.lexruntime.model.PostTextRequest;
import com.amazonaws.services.lexruntime.model.PostTextResult;
import com.amazonaws.services.lexruntime.model.ResponseCard;

import javazoom.jl.decoder.JavaLayerException;

public class BotClass {

	public static int phase=0;
	public static int pass=0;
	
	public BotClass() {
		
	}
	
	public void communication() throws IOException, JavaLayerException    {
		
		
		PollyDemo p= new PollyDemo(Region.getRegion(Regions.US_EAST_1),"Giorgio"); 
		AmazonLexRuntime client=AmazonLexRuntimeClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
		PostTextRequest textRequest=new PostTextRequest();
		ResponseCard card=new ResponseCard();
		ArrayList<Button> button=new  ArrayList<Button>();
		ArrayList<GenericAttachment> scelte= new ArrayList<GenericAttachment>(); 
		GenericAttachment scelta= new GenericAttachment();
		SentimentAnalisys sentimentAnalyzer= new SentimentAnalisys();
		ArrayList<Integer> points=new ArrayList<Integer>();//sostituire con un vettore punteggio, e un sistema di SQL
		int countRisp=0;
		textRequest.setBotName("Parliere");
		textRequest.setBotAlias("parliere");
		textRequest.setUserId("testuser");
		Scanner scan=new Scanner(System.in);
		String requestText = null;
		PostTextResult textResult = null;
		
		
		while(true) {
			 //requestText=scan.nextLine().trim();
			
		//textRequest.setInputText(requestText);//Inserisce il testo inserito nell'input
		//textRequest.setInputText("Piacere") per inserire l'intent del Piacere
		//PostTextResult textResult=client.postText(textRequest);//Invia l'imput a Lex e ottiene la risposta
		
		
		if(phase==0 ) { //chiede le informazioni di base
			if(pass==0) {
			textRequest.setInputText("Intro"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);//Invia l'imput a Lex e ottiene la risposta
			}
			if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
				
				System.out.println("Points = "+ points);
				System.out.println(textResult.getMessage());
				p.audioSynth(textResult.getMessage());
				pass++;
				requestText=scan.nextLine().trim();
				textRequest.setInputText(requestText);
				textResult=client.postText(textRequest);
				
			
			}
			else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			
				phase++;
				pass=1;
				
			}
			else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
				System.out.println("Completato");
				System.out.println(textResult.getMessage());
				p.audioSynth(textResult.getMessage());
			}
			
			else if(textResult.getDialogState().equals("Failed")) {
				System.out.println(textResult.getMessage());
				p.audioSynth(textResult.getMessage());
				
			}
				
				
			}
		
		
		if(phase==1) {
			if(pass==1) {
			textRequest.setInputText("Tristezza"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			 
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			
			if(textRequest.getInputText().equalsIgnoreCase("Mi sento triste per la maggior parte del tempo")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Mi sento sempre triste")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Mi sento così triste o infelice da non poterlo sopportare")) {
				points.add(3);
			}
			
			if(textRequest.getInputText().equalsIgnoreCase("Non mi sento triste")) {
				points.add(0);
			}
			
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(0)>0  ) {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");
			}
		  System.out.println("fase finale");
			phase=2;
			pass=1;
			countRisp=0;
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
			
			
		}
		
		
		if(phase==2) {
			if(pass==1) {
			textRequest.setInputText("Pessimismo"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Mi sento più scoraggiato riguardo al mio futuro rispetto al solito")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Non mi aspetto nulla di buono per me")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Sento che il mio futuro è senza speranza e che continuerà a peggiorare")) {
				points.add(3);
			}
			
			if(textRequest.getInputText().equalsIgnoreCase("Non sono scoraggiato riguardo al mio futuro")) {
				points.add(0);
			}
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(1)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
			
			
			
			
			
		}
		
		
		
		if(phase==3) {
			if(pass==1) {
				
			textRequest.setInputText("Fallimento"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Ho fallito più di quanto avrei dovuto")) {
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
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(2)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		if(phase==4) {
			if(pass==1) {
			textRequest.setInputText("Piacere"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Non traggo più piacere dalle cose come un tempo")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Traggo molto poco piacere dalle cose che di solito mi divertivano")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Non riesco a trarre alcun piacere dalle cose che una volta mi piacevano")) {
				points.add(3);
			}
			
			if(textRequest.getInputText().equalsIgnoreCase("Traggo lo stesso piacere di sempre dalle cose")) {
				points.add(0);
			}
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(3)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		
		
		if(phase==5) {
			if(pass==1) {
			textRequest.setInputText("Senso di Colpa"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Mi sento in colpa per molte cose che ho fatto o che avrei dovuto fare")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Mi sento molto spesso in colpa")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Mi sento sempre in colpa")) {
				points.add(3);
			}
			
			if(textRequest.getInputText().equalsIgnoreCase("Non mi sento particolarmente in colpa")) {
				points.add(0);
			}
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(4)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		if(phase==6) {
			if(pass==1) {
			textRequest.setInputText("Punizione"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Sento che potrei essere punito")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Mi aspetto di essere punito")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Mi sento come se stessi subendo una punizione")) {
				points.add(3);
			}
			
			if(textRequest.getInputText().equalsIgnoreCase("Non mi sento come se stessi subendo una punizione")) {
				points.add(0);
			}
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(5)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		if(phase==7) {
			if(pass==1) {
			textRequest.setInputText("Autostima"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Credo meno in me stesso")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Sono deluso di me stesso")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Mi detesto")) {
				points.add(3);
			}
			
			if(textRequest.getInputText().equalsIgnoreCase("Considero me stesso come ho sempre fatto")) {
				points.add(0);
			}
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(6)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		if(phase==8) {
			if(pass==1) {
			textRequest.setInputText("Autocritica"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Mi critico più spesso del solito")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Mi critico per tutte le mie colpe")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Mi biasimo per ogni cosa brutta che mi accade")) {
				points.add(3);
			}
			
			if(textRequest.getInputText().equalsIgnoreCase("Non mi critico né mi biasimo più del solito")) {
				points.add(0);
			}
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(7)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		if(phase==9) {
			if(pass==1) {
			textRequest.setInputText("Suicidio"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Ho pensieri suicidi ma non li realizzerei mai")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Sento che starei meglio se morissi")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Se mi si presentasse l'occasione non esisterei ad uccidermi")) {
				points.add(3);
			}
			
			if(textRequest.getInputText().equalsIgnoreCase("Non ho alcun pensiero suicida")) {
				points.add(0);
			}
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(8)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		if(phase==10) {
			if(pass==1) {
			textRequest.setInputText("Pianto"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Piango più del solito")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Piango per ogni minima cosa")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Ho spesso voglia di piangere ma non ci riesco")) {
				points.add(3);
			}
			
			if(textRequest.getInputText().equalsIgnoreCase("Non piango più del solito")) {
				points.add(0);
			}
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(9)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		if(phase==11) {
			if(pass==1) {
			textRequest.setInputText("Agitazione"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Mi sento più agitato o teso del solito")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Sono così nervoso o agitato al punto che mi è difficile rimanere fermo")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Sono così nervoso o agitato che devo continuare a muovermi o fare qualcosa")) {
				points.add(3);
			}
			
			if(textRequest.getInputText().equalsIgnoreCase("Non mi sento più teso o agitato del solito")) {
				points.add(0);
			}
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(10)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		if(phase==12) {
			if(pass==1) {
			textRequest.setInputText("Perdita di interessi"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Sono meno interessato agli altri o a alle cose rispetto a prima")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Sono meno interessato agli altri o a alle cose rispetto a prima")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Ho perso la maggior parte dell'interesse verso le altre persone o cose")) {
				points.add(3);
			}
			
			if(textRequest.getInputText().equalsIgnoreCase("Non ho perso interesse verso le altre persone o verso le attività")) {
				points.add(0);
			}
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(11)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		if(phase==13) {
			if(pass==1) {
			textRequest.setInputText("Indecisione"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Trovo più difficoltà del solito nel prendere decisioni")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Ho molte più difficoltà del solito nel prendere decisioni")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Non riesco a prendere nessuna decisione")) {
				points.add(3);
			}
			
			if(textRequest.getInputText().equalsIgnoreCase("Prendo decisioni come sempre")) {
				points.add(0);
			}
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(12)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		if(phase==14) {
			if(pass==1) {
			textRequest.setInputText("Inutilità"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Non mi sento valido e utile come un tempo")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Mi sento più inutile delle altre persone")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Mi sento completamente inutile su qualsiasi cosa")) {
				points.add(3);
			}
			
			if(textRequest.getInputText().equalsIgnoreCase("Non mi sento inutile")) {
				points.add(0);
			}
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(13)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		
		if(phase==15) {
			if(pass==1) {
			textRequest.setInputText("Perdita di energia"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Ho meno energia del solito")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Non ho energia sufficiente per fare la maggior parte delle cose")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Ho così poca energia che non riesco a fare nulla")) {
				points.add(3);
			}
			
			if(textRequest.getInputText().equalsIgnoreCase("Ho la stessa energia di sempre")) {
				points.add(0);
			}
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(14)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		
		if(phase==16) {
			if(pass==1) {
			textRequest.setInputText("Sonnolenza"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Dormo un po più del solito")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Dormo un po meno del solito")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Dormo molto più del solito")) {
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
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			scelta=scelte.get(1);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				p.audioSynth(button.get(i).getText());
				}
			
			
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(15)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		if(phase==17) {
			if(pass==1) {
			textRequest.setInputText("Irritabilità"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Sono più irritabile del solito")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Sono molto più irritabile del solito")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Sono sempre irritabile")) {
				points.add(3);
			}
			
			if(textRequest.getInputText().equalsIgnoreCase("Non sono più irritabile del solito")) {
				points.add(0);
			}
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(16)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		if(phase==18) {
			if(pass==1) {
			textRequest.setInputText("Appetito"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Il mio appetito è un po diminuito rispetto al solito")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Il mio appetito è un po aumentato rispetto al solito")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Il mio appetito è molto aumentato rispetto al solito")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Il mio appetito è molto diminuito rispetto al solito")) {
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
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			scelta=scelte.get(1);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
				System.out.println(button.get(i).getText());
				countRisp++;
				p.audioSynth(button.get(i).getText());
				}
			
			
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(17)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		if(phase==19) {
			if(pass==1) {
			textRequest.setInputText("Concentrazione"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
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
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(18)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		if(phase==20) {
			if(pass==1) {
			textRequest.setInputText("Fatica"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
			if(textRequest.getInputText().equalsIgnoreCase("Sono stanco e mi affatico più facilmente del solito")) {
				points.add(1);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Sono così stanco e affaticato che non riesco a fare molte cosa che facevano prima")) {
				points.add(2);
			}
			if(textRequest.getInputText().equalsIgnoreCase("Sono talmente stanco e affaticato che non riesco più a fare nessuna delle cose che facevo prima")) {
				points.add(3);
			}
			
			if(textRequest.getInputText().equalsIgnoreCase("Non sono più stanco e affaticato del solito")) {
				points.add(0);
			}
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(19)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}
		
		
		
		if(phase==21) {
			if(pass==1) {
			textRequest.setInputText("Sesso"); //Inserisce il testo inserito nell'input
			 textResult=client.postText(textRequest);
		}
		
		if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
			
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
			
			System.out.println("Points = "+ points);
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			pass++;
			
			
			if(countRisp==0) { //Frammento temporaneo fino a quando non si separa le response cards dalle risposte libere
			card=textResult.getResponseCard();
			scelte=(ArrayList<GenericAttachment>) card.getGenericAttachments();
			scelta=scelte.get(0);
			button=(ArrayList<Button>) scelta.getButtons();
			for(int i = 0;i<button.size();i++){
			System.out.println(button.get(i).getText());
			countRisp++;
			p.audioSynth(button.get(i).getText());
			}
			}
			
			requestText=scan.nextLine().trim();
			textRequest.setInputText(requestText);
			textResult=client.postText(textRequest);
			}
			
		
		else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
			sentimentAnalyzer.analizzaSentimento(requestText);
			if((sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
					sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE"))&& points.get(20)>0  )  {
				System.out.println("Risposta contraddittoria specificare meglio");
				p.audioSynth("Risposta contraddittoria specificare meglio");//da migliorare con una funzione apposita del bot
			}
		  System.out.println("fase finale");
			phase++;
			pass=1;
			countRisp=0;
			
			
		}
		else if(textResult.getDialogState().equals("Fulfilled")) { //entra in questo stato solo se c'è una risposta finale da parte del Bot
			System.out.println("Completato");
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
		}
		
		else if(textResult.getDialogState().equals("Failed")) {
			System.out.println(textResult.getMessage());
			p.audioSynth(textResult.getMessage());
			
		}
				
		}

		
		
		
	}//end while(true)	
		
		
	}//end metodo

	
	
	
}//end classe
	

