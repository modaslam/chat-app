package com.gitlab.modaslam.kafka_chat_server.controller;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gitlab.modaslam.kafka_chat_server.constant.KafkaConstant;
import com.gitlab.modaslam.kafka_chat_server.model.Message;

@CrossOrigin
@RestController
public class ChatController {

	@Autowired
	private KafkaTemplate<String, Message> kafkaTemplate;

	@PostMapping(value = "/api/send", consumes = "application/json", produces = "application/json")
	public void sendMessage(@RequestBody Message message) {
		message.setTimestamp(LocalDateTime.now().toString());
		try {
			// Send message to kafka topic queue
			kafkaTemplate.send(KafkaConstant.KAFKA_TOPIC, message).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
	//WEBSOCKET APIs

	@MessageMapping("/sendMessage")
	@SendTo("/topic/group")
	public Message broadcastGroupMessage(@Payload Message message) {
		// Sending this message to all the subscribers
		return message;
	}

	@MessageMapping("/newUser")
	@SendTo("/topic/group")
	public Message addUser(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
		// Add user in web socket session
		headerAccessor.getSessionAttributes().put("username", message.getSender());
		return message;
	}
}
