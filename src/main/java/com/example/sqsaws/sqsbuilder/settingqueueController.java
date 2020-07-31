package com.example.sqsaws.sqsbuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.example.sqsaws.configuration.AmazonS3Configuration;

@RestController
@RequestMapping("/sqs")
public class settingqueueController {

	@Autowired
	AmazonS3Configuration awsconfig;

	@Value("${aws.sqsurl}")
	private String queueurl;

	@Value("${aws.bucket}")
	private static String bucketName;

	@Autowired
	private static AmazonS3 s3client;


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

	@SqsListener(value = "MyQueue")
	@GetMapping("/get")
	public void reciveMsg() {

		String msg1="";
		ReceiveMessageRequest messageRequest = new ReceiveMessageRequest(queueurl).withWaitTimeSeconds(10)
				.withMaxNumberOfMessages(10);;

				ReceiveMessageResult queueResult = awsconfig.sqsClient().receiveMessage(messageRequest);
				List<Message> messages = queueResult.getMessages();
				if(messages!=null) {
					for (Message message : messages) {
						if(message!=null) {
							msg1=message.getBody().toString();
							addnumbers(msg1);
						}

					}
				}


				/*
				 * List<Message> messages =
				 * awsconfig.sqsClient().receiveMessage(queueurl).getMessages(); for(Message
				 * message:messages) { msg1=message.getBody().toString(); addnumbers(msg1);
				 * 
				 * }
				 */	}

	//	@GetMapping("/get")
	public static String addnumbers(String msg) {
		
		File file = null;
		String str[] = msg.split(" ");
		List<String> al = new ArrayList<>();
		al = Arrays.asList(str);	
		Integer result=Integer.parseInt(al.get(0))+Integer.parseInt(al.get(1));	
		try {
			file=new File(msg);
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
				msg, 
				file
				);
		System.out.println(result);
		return "file added to s3bucket";

	}
}

