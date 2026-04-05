package com.springai.ollama.service;

import java.util.List;

import com.springai.ollama.dto.Details;
import com.springai.ollama.dto.TechInfo;

import reactor.core.publisher.Flux;

public interface IPromptService {

	String customMsgWithSystemMsg();
	String apiMsgWithPrompt(String msg);
	Details storeResponseInDTO(String msg);
	List<Details> storeResponseInDTOList(String query);
	
	TechInfo getTechInfo(String query);
	
	String apiMsgWithPromptTemplate(String msg);
	String apiMsgWithPromptTemplateOneMsg();
	String apiMsgWithPromptTemplateMultipleMsg();
	
	Flux<String> getPromptResponseAsStream(String query);
	
	
}
