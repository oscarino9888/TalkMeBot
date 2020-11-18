package com.amazonaws.demos.polly;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectEntitiesRequest;
import com.amazonaws.services.comprehend.model.DetectEntitiesResult;

public class NamedEntities 
{
    public static void main( String[] args )
    {

        String text = "Oggi piove in tutta la Campania";

   
        AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();
 
        AmazonComprehend comprehendClient =
            AmazonComprehendClientBuilder.standard()
                                         .withCredentials(awsCreds)
                                         .withRegion(Regions.EU_WEST_1)
                                         .build();
                                         
        // Chiama detectEntities API
        System.out.println("Calling DetectEntities");
        DetectEntitiesRequest detectEntitiesRequest = new DetectEntitiesRequest().withText(text)
                                                                                 .withLanguageCode("it");
        DetectEntitiesResult detectEntitiesResult  = comprehendClient.detectEntities(detectEntitiesRequest);
        detectEntitiesResult.getEntities().forEach(System.out::println);
        System.out.println("End of DetectEntities\n");
    }
}
