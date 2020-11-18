package com.amazonaws.demos.polly;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehend.*;
import com.amazonaws.services.comprehend.model.*;
import com.amazonaws.services.comprehend.model.transform.*;


public class LanguageDetection {

		    public static void main( String[] args )
		    {

		        String text = "Io sono triste ";
		        
		   
		        AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();
		 
		        AmazonComprehend comprehendClient =
		            AmazonComprehendClientBuilder.standard()
		                                         .withCredentials(awsCreds)
		                                         .withRegion(Regions.EU_WEST_1)
		                                         .build();
		                                         
		        // Chiama detectDominantLanguage API
		        System.out.println("Calling DetectDominantLanguage");
		        DetectDominantLanguageRequest detectDominantLanguageRequest = new DetectDominantLanguageRequest().withText(text);
		        DetectDominantLanguageResult detectDominantLanguageResult = comprehendClient.detectDominantLanguage(detectDominantLanguageRequest);
		        detectDominantLanguageResult.getLanguages().forEach(System.out::println);
		        System.out.println("Calling DetectDominantLanguage\n");
		        System.out.println("Done");
		    }
		}
	
	
	

