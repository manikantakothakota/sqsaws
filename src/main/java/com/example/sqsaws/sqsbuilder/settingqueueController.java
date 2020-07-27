package com.example.sqsaws.sqsbuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.example.sqsaws.configuration.AmazonS3Configuration;

@RestController
public class settingqueueController {
	
	@Autowired
	AmazonS3Configuration awsconfig;
	
	@Value("${aws.sqsurl}")
    private String queueurl;
	
	@Value("${aws.bucket}")
	private String bucketName;

	@Autowired
	private AmazonS3 s3client;

	
	/*
	 * CreateQueueRequest createStandardQueueRequest = new
	 * CreateQueueRequest("my-queue"); String standardQueueUrl =
	 * awsconfig.sqsClient().createQueue(createStandardQueueRequest) .getQueueUrl();
	 */ 
     
    @PostMapping("/add")
public String sendsqsmsg(@RequestParam String val) {
     SendMessageRequest send_msg_request = new SendMessageRequest()
    	        .withQueueUrl(queueurl)
    	        .withMessageBody(val);
     awsconfig.sqsClient().sendMessage(send_msg_request);
     System.out.println("msg sent");
	return "massege sent sucessfully" ;
}

@GetMapping("/getall")
public List<Message> getallmsg(){
	List<Message> messages = awsconfig.sqsClient().receiveMessage(queueurl).getMessages();
	return messages;
}

@GetMapping("/get")
public String addnumbers() {
	 File file = null;
	List<Message> messages = awsconfig.sqsClient().receiveMessage(queueurl).getMessages();
	Message msg=messages.get(0);
	String msg1=msg.getBody().toString();
	String str[] = msg1.split(" ");
	List<String> al = new ArrayList<>();
	al = Arrays.asList(str);	
	Integer result=Integer.parseInt(al.get(0))+Integer.parseInt(al.get(1));
	 try {
		 file=new File("filename.txt");
	      FileWriter myWriter = new FileWriter(file);
	      myWriter.write("firstvalue :"+al.get(0)+","+"secondvalue :"+al.get(1)+","+"result :"+result);
	      myWriter.close();
	      System.out.println("Successfully wrote to the file.");
	    } catch (IOException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
	 
		
		
	 s3client.putObject(
			  bucketName, 
			  "filename.txt", 
			  file
			);

	
	System.out.println(result);
	return "file added to s3bucket";
	
}
}

