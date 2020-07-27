package com.example.sqsaws.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

@Configuration
public class AmazonS3Configuration {

	
	  @Value("${aws.Accesskey}")
	    private String accessKey;
	    @Value("${aws.Secretaccesskey}")
	    private String secretKey;
	    @Value("${aws.Region}")
	    private String region;

	    @Bean
	    public AmazonS3 generateS3Client() {
	        AWSCredentials credentials = new BasicAWSCredentials(accessKey,secretKey);
	        AmazonS3 client=AmazonS3ClientBuilder.standard()
	        		.withRegion(Regions.fromName(region))
	        		.withCredentials(new AWSStaticCredentialsProvider(credentials)) .build();
	        return client;
	    }
	    @Bean
	    public AmazonSQS sqsClient() {
	        AWSCredentials credentials = new BasicAWSCredentials(accessKey,secretKey);
	        AmazonSQS sqs = AmazonSQSClientBuilder.standard() 
	        		.withRegion(Regions.fromName(region))
	        		.withCredentials(new AWSStaticCredentialsProvider(credentials)) .build();
	        return sqs;
	    }
	    
}	    
	         
