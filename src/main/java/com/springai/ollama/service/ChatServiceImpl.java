package com.springai.ollama.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements IChatService {

	private ChatClient chatClient;

	public ChatServiceImpl(@Autowired ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.build();
	}

	@Override
	public String chatMsg(String msg) {		
	String response =  this.chatClient.prompt(msg).call().content();
	System.out.println("Response: "+response);
	return response;
	}
}
