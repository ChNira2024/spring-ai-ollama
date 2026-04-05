package com.springai.ollama.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springai.ollama.service.IChatService;

@RestController
@RequestMapping("/chat")
public class ChatController {
	
	
	private IChatService iChatService;
	
	public ChatController(IChatService iChatService) {
		this.iChatService=iChatService;
	}
	
	@GetMapping("/ask")
	public ResponseEntity<String> chat(@RequestParam(value = "query") String query) {
	return ResponseEntity.ok(iChatService.chatMsg(query));
	}
	
}
