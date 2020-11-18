package com.amazonaws.demos.polly;

import java.math.BigDecimal;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectSentimentRequest;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;
import com.amazonaws.services.comprehend.model.SentimentScore;

public class SentimentAnalisys 
{

	public SentimentAnalisys() {
		    awsCreds = DefaultAWSCredentialsProviderChain.getInstance();
	        comprehendClient = AmazonComprehendClientBuilder.standard()
	                                         .withCredentials(awsCreds)
	                                         .withRegion(Regions.EU_WEST_1)
	                                         .build();
	}
	
	
	private AWSCredentialsProvider awsCreds;
	private  AmazonComprehend comprehendClient;
	
	/**
	 * Metodo per approssimare in float fino alla seconda cifra della percentuale in decimale
	 * @param number
	 * @param decimalPlace
	 * @return
	 */
	public static float round(float number, int decimalPlace) {
		BigDecimal bd = new BigDecimal(number);
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}
	
	
	
	
	public String getSentimentoPredominante(String testo){
		
		 DetectSentimentRequest detectSentimentRequest = new DetectSentimentRequest().withText(testo)
                 .withLanguageCode("it");
		 DetectSentimentResult detectSentimentResult = comprehendClient.detectSentiment(detectSentimentRequest);
		
		 String sentimentoMaggiore= detectSentimentResult.getSentiment();//Indica il sentimento con maggiore importanza nella frase

		
		return sentimentoMaggiore;
		
	}
	
	
    public void analizzaSentimento(String testo)
    {

        
                                         
        //Chiama detectSentiment API
        System.out.println("Calling DetectSentiment");
        DetectSentimentRequest detectSentimentRequest = new DetectSentimentRequest().withText(testo)
                                                                                    .withLanguageCode("it");
        DetectSentimentResult detectSentimentResult = comprehendClient.detectSentiment(detectSentimentRequest);
        String sentimentoMaggiore= detectSentimentResult.getSentiment();//Indica il sentimento con maggiore importanza nella frase
        
        SentimentScore score= detectSentimentResult.getSentimentScore();//Prende lo score di tutti i sentimenti
        float sentimentoPercentuale= 0; //Servirà per lo score percentuale del sentimento
        //int sentimentoIntero=0;Servirà per calcolare il sentimento in interi da 0 a 3
        
        if(sentimentoMaggiore.equals("NEUTRAL")) {
        	sentimentoPercentuale=round(score.getNeutral()*100, 2);
        	System.out.println(sentimentoMaggiore+ "=" + sentimentoPercentuale+"%" );}
       /* if(sentimentoPercentuale>=0 && sentimentoPercentuale<25) {
        	sentimentoIntero=0;
        }
        
        if(sentimentoPercentuale>=25 && sentimentoPercentuale<50) {
        	sentimentoIntero=1;
        }
        
        if(sentimentoPercentuale>=50 && sentimentoPercentuale<75) {
        	sentimentoIntero=2;
        }
  
        if(sentimentoPercentuale>=75) {
        	sentimentoIntero=3;
        }
        }
        */
        
        
        if(sentimentoMaggiore.equals("POSITIVE")) {
        	sentimentoPercentuale=round(score.getPositive()*100, 2);
        	System.out.println(sentimentoMaggiore+ "=" + sentimentoPercentuale+"%" );}
        	/*
        if(sentimentoPercentuale>=0 && sentimentoPercentuale<25) {
        	sentimentoIntero=0;
        }
        
        if(sentimentoPercentuale>=25 && sentimentoPercentuale<50) {
        	sentimentoIntero=1;
        }
        
        if(sentimentoPercentuale>=50 && sentimentoPercentuale<75) {
        	sentimentoIntero=2;
        }
  
        if(sentimentoPercentuale>=75) {
        	sentimentoIntero=3;
        }
        }
        */
        if(sentimentoMaggiore.equals("NEGATIVE")) {
        	sentimentoPercentuale=round(score.getNegative()*100, 2);
        	System.out.println(sentimentoMaggiore+ "=" + sentimentoPercentuale+"%" );}
        	
        	/*
        if(sentimentoPercentuale>=0 && sentimentoPercentuale<25) {
        	sentimentoIntero=0;
        }
        
        if(sentimentoPercentuale>=25 && sentimentoPercentuale<50) {
        	sentimentoIntero=1;
        }
        
        if(sentimentoPercentuale>=50 && sentimentoPercentuale<75) {
        	sentimentoIntero=2;
        }
  
        if(sentimentoPercentuale>=75) {
        	sentimentoIntero=3;
        }
        }
        */
        
        
        //System.out.println("Valore Intero del sentimento ="+ sentimentoIntero);
        System.out.println(detectSentimentResult);
        System.out.println("End of DetectSentiment\n");
        System.out.println( "Fatto" );
    }
    }

