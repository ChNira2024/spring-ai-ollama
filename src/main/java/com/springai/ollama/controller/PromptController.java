package com.springai.ollama.controller;//D*****sh yt code

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springai.ollama.dto.Details;
import com.springai.ollama.dto.TechInfo;
import com.springai.ollama.service.IPromptService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/prompt")
public class PromptController {
	
	private IPromptService iPromptService;
	
	public PromptController(IPromptService iPromptService) {
		this.iPromptService=iPromptService;
	}

	@GetMapping("/ask")
	public ResponseEntity<String> askCustomQueryWithSystemMsg() {
		
		String response = iPromptService.customMsgWithSystemMsg();
		return ResponseEntity.ok(response);
	
	}
	
	@GetMapping("/ask2")
	public ResponseEntity<String> askOwnQueryWithPromptObj(@RequestParam(value = "query") String query) {
		
		String response = iPromptService.apiMsgWithPrompt(query);
		return ResponseEntity.ok(response);
	
	}
	
	@GetMapping("/ask3")
	public ResponseEntity<Details> askOwnQueryWithPromptObjStoreInDTO(@RequestParam(value = "query") String query) {
		System.out.println("query: "+query);
		Details response = iPromptService.storeResponseInDTO(query);
		return ResponseEntity.ok(response);
	
	}
	
	@GetMapping("/ask4")
	public ResponseEntity<List<Details>> askOwnQueryWithPromptObjStoreInDTOList(@RequestParam(value = "query") String query) {
		System.out.println("query: "+query);
		List<Details> response = iPromptService.storeResponseInDTOList(query);
		return ResponseEntity.ok(response);
	
	}
	
	//BeanOutputConverter? It converts AI response (String/JSON) → Java Object (DTO) automatically
	@GetMapping("/tech/stored-into-dto")
    public TechInfo getTechAndStoreResponseIntoDTO(@RequestParam String q) {
		System.out.println("Query comes into Controller class: getTechAndStoreResponseIntoDTO()"+q);
        return iPromptService.getTechInfo(q);
    }
	
	@GetMapping("/ask5")
	public ResponseEntity<String> askOwnQueryWithPromptTemplate(@RequestParam(value = "query") String query) {
		System.out.println("query: "+query);
		String response = iPromptService.apiMsgWithPromptTemplate(query);
		return ResponseEntity.ok(response);
	
	}
	
	@GetMapping("/ask6")
	public ResponseEntity<String> askOwnQueryWithPromptTemplateOneMsg() {
		String response = iPromptService.apiMsgWithPromptTemplateOneMsg();
		return ResponseEntity.ok(response);
	
	}
	
	@GetMapping("/ask7")
	public ResponseEntity<String> askOwnQueryWithPromptTemplateMultipleMsg() {
		String response = iPromptService.apiMsgWithPromptTemplateMultipleMsg();
		return ResponseEntity.ok(response);
	
	}
	
	@GetMapping("/stream-chat")
	public ResponseEntity<Flux<String>> getDataAsStreamFlow(@RequestParam(value="query")String query) {
		System.out.println("query"+query);
		Flux<String> response = iPromptService.getPromptResponseAsStream(query);
		return ResponseEntity.ok(response);
	
	}
	
	
}
