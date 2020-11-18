package com.amazonaws.demos.polly;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectSyntaxRequest;
import com.amazonaws.services.comprehend.model.DetectSyntaxResult;
 
public class SyntaxDetect
{
	public static void main( String[] args )
	{
 
		String text = "Piove molto oggi";
		Regions region = Regions.EU_WEST_1;
 
	
		AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();
 
		AmazonComprehend comprehendClient =
				AmazonComprehendClientBuilder.standard()
						.withCredentials(awsCreds)
						.withRegion(region)
						.build();
 
		// Chiama detectSyntax API
		System.out.println("Calling DetectSyntax");
		DetectSyntaxRequest detectSyntaxRequest = new DetectSyntaxRequest()
				.withText(text)
				.withLanguageCode("it");
		DetectSyntaxResult detectSyntaxResult = comprehendClient.detectSyntax(detectSyntaxRequest);
		detectSyntaxResult.getSyntaxTokens().forEach(System.out::println);
		System.out.println("End of DetectSyntax\n");
		System.out.println( "Fatto" );
	}
}