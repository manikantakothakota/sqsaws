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
import org.springframework.scheduling.annotation.Scheduled;
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
	private  String bucketName;

	@Autowired
	private  AmazonS3 s3client;


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

	@SqsListener(value = "MyQueue"/* , deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS */)
	@Scheduled(fixedRate = 1000)
	//@GetMapping("/get")
	public void reciveMsg() {
		ReceiveMessageRequest messageRequest = new ReceiveMessageRequest(
				queueurl)/*
							 * .withWaitTimeSeconds(10) .withMaxNumberOfMessages(10);
							 */;
				ReceiveMessageResult queueResult = awsconfig.sqsClient().receiveMessage(messageRequest);
				List<Message> messages = queueResult.getMessages();
				if(messages!=null) {
					for (Message message : messages) {
						if(message!=null) {
							addnumbers(message.getBody().toString());
							// delete message after successful operation
							awsconfig.sqsClient().deleteMessage(queueurl, message.getReceiptHandle());
							System.out.println("deleted msg :"+message.getBody());
						}
					}
				}
	}

	//	@GetMapping("/get")
	public  String addnumbers(String msg) {
		String name=msg;
		File file = null;
		String str[] = msg.split(" ");
		List<String> al = new ArrayList<>();
		al = Arrays.asList(str);	
		Integer result=Integer.parseInt(al.get(0))+Integer.parseInt(al.get(1));	
		try {
			file=new File(name);
			FileWriter myWriter = new FileWriter(file);
			myWriter.write("firstvalue :"+al.get(0)+","+"secondvalue :"+al.get(1)+","+"result :"+result);
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}System.out.println(bucketName);
		s3client.putObject(
				bucketName, 
				name, 
				file
				);
		System.out.println(result);
		return "file added to s3bucket";
	}
}

