package com.gitlab.modaslam.kafka_chat_server.message.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.gitlab.modaslam.kafka_chat_server.constant.KafkaConstant;
import com.gitlab.modaslam.kafka_chat_server.model.Message;

@Component
public class ChatListener {
	@Autowired
	SimpMessagingTemplate template;

	@KafkaListener(topics = KafkaConstant.KAFKA_TOPIC, groupId = KafkaConstant.GROUP_ID)
	public void listen(Message message) {
		System.out.println("Sending via kafka listener..");
		template.convertAndSend("/topic/group", message);
	}
	
}
