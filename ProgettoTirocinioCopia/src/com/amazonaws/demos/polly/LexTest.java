package com.amazonaws.demos.polly;

import java.util.List;
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

import com.amazonaws.demos.polly.PollyDemo;

public class LexTest {
public LexTest() {
	// TODO Auto-generated constructor stub
}
public static void main(String[] args) throws IOException, JavaLayerException {
	PollyDemo p= new PollyDemo(Region.getRegion(Regions.US_EAST_1),"Giorgio"); 
	AmazonLexRuntime client=AmazonLexRuntimeClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
	PostTextRequest textRequest=new PostTextRequest();
	ResponseCard card=new ResponseCard();
	ArrayList<Button> button=new  ArrayList<Button>();
	ArrayList<GenericAttachment> scelte= new ArrayList<GenericAttachment>(); 
	GenericAttachment scelta= new GenericAttachment();
	SentimentAnalisys sentimentAnalyzer= new SentimentAnalisys();
	int points=0;
	int countRisp=0;
	textRequest.setBotName("Parliere");
	textRequest.setBotAlias("parliere");
	textRequest.setUserId("testuser");
	Scanner scan=new Scanner(System.in);
	
	
	while(true) {
		String requestText=scan.nextLine().trim();
		if(requestText==null) {
			break;
		}
	textRequest.setInputText(requestText);//Inserisce il testo inserito nell'input
	//textRequest.setInputText("Piacere") per inserire l'intent del Piacere
	PostTextResult textResult=client.postText(textRequest);//Invia l'imput a Lex e ottiene la risposta
	
	if(textResult.getDialogState().startsWith("Elicit")) { // Se si è nella fase Elicit
		if(textRequest.getInputText().equalsIgnoreCase("Mi sento triste per la maggior parte del tempo")) {
			points+=1;
		}
		if(textRequest.getInputText().equalsIgnoreCase("Mi sento sempre triste")) {
			points+=2;
		}
		if(textRequest.getInputText().equalsIgnoreCase("Mi sento così triste o infelice da non poterlo sopportare")) {
			points+=3;
		}
		if(textRequest.getInputText().equalsIgnoreCase("Non mi sento triste")) {
			break;
		}
		System.out.println("Points = "+ points);
		System.out.println(textResult.getMessage());
		p.audioSynth(textResult.getMessage());	
		
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
		
	}
	else if(textResult.getDialogState().equals("ReadyForFulfillment")) { // Si entra in questo stato quando si ha risposto all'ultima domanda
		sentimentAnalyzer.analizzaSentimento(requestText);
		if(sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("NEUTRAl")||
				sentimentAnalyzer.getSentimentoPredominante(requestText).equalsIgnoreCase("POSITIVE")) {
			System.out.println("Risposta contraddittoria specificare meglio");
			p.audioSynth("Risposta contraddittoria specificare meglio");
		}
	
		
		
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
	System.out.println("Bye");
}	
}
