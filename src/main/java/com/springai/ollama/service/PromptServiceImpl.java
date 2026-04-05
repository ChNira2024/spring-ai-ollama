package com.springai.ollama.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.springai.ollama.dto.Details;
import com.springai.ollama.dto.TechInfo;

import reactor.core.publisher.Flux;

@Service
public class PromptServiceImpl implements IPromptService {

	private ChatClient chatClient;

	public PromptServiceImpl(@Autowired ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.build();
	}

	@Override
	public String customMsgWithSystemMsg() {
		String query = "Tell me about spring boot?";

		String response = this.chatClient.prompt().user(query).system("As an expert in spring boot" + "").call()
				.content();
		System.out.println("Response of customMsgWithSystemMsg(): " + response);
		return response;

	}

	@Override
	public String apiMsgWithPrompt(String msg) {
		Prompt prompt = new Prompt(msg);

		var response = this.chatClient.prompt(prompt).call().chatResponse().getResult().getOutput().getText();
		System.out.println("Response of customMsgWithPrompt(): " + response);
		return response;
	}
//============================================================================
	@Override
	public Details storeResponseInDTO(String msg) {	
        String prompt = """
        Answer the question and provide details in this format:

        Title: <short title>
        Content: <detailed explanation>
        Year: <year>

        Rules:
        - Do not skip any field
        - Year must be 4 digits

        Question: %s
        """.formatted(msg);

        String response = chatClient.prompt(prompt)
                .call()
                .content()
                .trim();

        System.out.println("Raw Response: " + response);

        return parseResponse(response);
    }

    // 👉 Parse structured response
    private Details parseResponse(String response) {
        Details details = new Details();
        String[] lines = response.split("\\n");

        for (String line : lines) {
            line = line.trim();
            if (line.toLowerCase().startsWith("title:")) {
                details.setTitle(line.substring(6).trim());
            } 
            else if (line.toLowerCase().startsWith("content:")) {
                details.setContent(line.substring(8).trim());
            } 
            else if (line.toLowerCase().startsWith("year:")) {
                String year = line.substring(5).trim();

                if (year.matches("\\d{4}")) {
                    details.setCreatedYear(year);
                }
            }
        }
        // 👉 fallback handling
        if (details.getTitle() == null) {
            details.setTitle("General Topic");
        }
        if (details.getContent() == null) {
            details.setContent(response);
        }
        if (details.getCreatedYear() == null) {
            details.setCreatedYear(extractYearFallback(response));
        }
        return details;
    }

    // 👉 Extract year if model fails
    private String extractYearFallback(String text) {
        Pattern pattern = Pattern.compile("\\b(19|20)\\d{2}\\b");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group();
        }
        return "2000"; // safe default
	}
    
//===========================================================================	
	
	@Override
	public List<Details> storeResponseInDTOList(String query) {	
		 String prompt = """
			        Provide top 3 results in the following format:

			        1.
			        Title: <title>
			        Content: <content>
			        Year: <year>

			        2.
			        Title: <title>
			        Content: <content>
			        Year: <year>

			        3.
			        Title: <title>
			        Content: <content>
			        Year: <year>

			        Rules:
			        - Follow exact format
			        - Do not skip any field
			        - Year must be 4 digits

			        Query: %s
			        """.formatted(query);

			        String response = chatClient.prompt(prompt).call().content().trim();
			        System.out.println("Raw Response: " + response);
			        return parseListResponse(response);
			    }

			    // 👉 PARSER
			    private List<Details> parseListResponse(String response) {
			        List<Details> list = new ArrayList<>();

			        // Split using 1. 2. 3.
			        String[] blocks = response.split("\\d+\\.");

			        for (String block : blocks) {
			            block = block.trim();
			            if (block.isEmpty()) 
			            	continue;
			            Details details = new Details();
			            String[] lines = block.split("\\n");

			            for (String line : lines) {
			                line = line.trim();

			                if (line.toLowerCase().startsWith("title:")) {
			                    details.setTitle(line.substring(6).trim());
			                }
			                else if (line.toLowerCase().startsWith("content:")) {
			                    details.setContent(line.substring(8).trim());
			                }
			                else if (line.toLowerCase().startsWith("year:")) {
			                    String year = line.substring(5).trim();

			                    if (year.matches("\\d{4}")) {
			                        details.setCreatedYear(year);
			                    }
			                }
			            }

			            // 👉 fallback handling
			            if (details.getTitle() == null) {
			                details.setTitle("General Topic");
			            }
			            if (details.getContent() == null) {
			                details.setContent(block);
			            }
			            if (details.getCreatedYear() == null) {
			                details.setCreatedYear(extractYearFallbackList(block));
			            }
			            list.add(details);
			        }
			        return list;
			    }

			    // 👉 YEAR FALLBACK
			    private String extractYearFallbackList(String text) {
			        Pattern pattern = Pattern.compile("\\b(19|20)\\d{2}\\b");
			        Matcher matcher = pattern.matcher(text);

			        if (matcher.find()) {
			            return matcher.group();
			        }
			        return "2000";
			    }
//============================================================================
		
		@Override
		public String apiMsgWithPromptTemplate(String msg) {
			Prompt prompt = new Prompt(msg);
			
			//modify this prompt and extra things to prompt make it more interactive
			String queryStr = "As an expert in coding and programing. Always write program in java . Now reply for this question :{msg}";

			var response = chatClient.prompt().user(u -> u.text(queryStr).param("msg", msg)).call().content();
			System.out.println("response: "+response);
			return response;
		}
//================================================
		@Override
		public String apiMsgWithPromptTemplateOneMsg() {
			PromptTemplate template = PromptTemplate.builder().template("What is {techName}? provide example on  {exampleName} with db configuration").build();
		
					//render the template
					String renderedMessage = template.render(Map.of("techName","Spring boot","exampleName","Spring Boot"));//old way
					Prompt prompt = new Prompt(renderedMessage);
					System.out.println("prompt:"+prompt);

					Prompt prompt2 = template.create(Map.of(
				                							"techName", "Spring Boot",
				                							"exampleName", "Spring Boot"
				                							));//new way
					System.out.println("prompt2:"+prompt2);
					return this.chatClient.prompt(prompt).call().content();
		}
//================================================	
		@Override
		public String apiMsgWithPromptTemplateMultipleMsg() {
			var systemPromptTemplate = SystemPromptTemplate.builder().template("You are a helpful coding assistant. You are an expert in coding.").build();
					
			var systemMessage = systemPromptTemplate.createMessage();

			var userPromptTemplate = PromptTemplate.builder(). template("What is {techName}? provide example on  {exampleName}").build();
			var userMessage = userPromptTemplate.createMessage(Map.of("techName","Spring","exampleName","spring exception"));

			Prompt prompt = new Prompt(systemMessage, userMessage);//passing multiple messages like system and user msg
			System.out.println("prompt:"+prompt);
		return this. chatClient.prompt(prompt) .call().content();
		}
//===========================================================================
		@Override
		public TechInfo getTechInfo(String query) {
			System.out.println("Query comes into PromptServiceImpl class: getTechInfo()"+query);
		    
			BeanOutputConverter<TechInfo> converter = new BeanOutputConverter<>(TechInfo.class);

		    String prompt = """
		            You are a strict JSON generator.

		            Respond ONLY with JSON.
		            Do not return schema.
		            Do not include properties field.

		            Return EXACTLY this:

		            {
		              "topic": "string",
		              "description": "string",
		              "example": "string",
		              "difficultyLevel": "string"
		            }

		            Question: %s
		            """.formatted(query);

		    System.out.println("prompt is in PromptServiceImpl class: getTechInfo()"+prompt);

		    String response = chatClient.prompt(prompt).call().content();

		    System.out.println("Response comes is in PromptServiceImpl class: getTechInfo()"+response);

		    response = response
		            .replaceAll("```json", "")
		            .replaceAll("```", "")
		            .trim();//“It cleans LLM markdown-formatted JSON responses before parsing into DTO.”

		    return converter.convert(response);//convert AI String/json response to DTO
		}
//==============================================================================
		//Use @Value to inject file
	    @Value("classpath:prompts/system-message.st")
	    private Resource systemMessageResource;
	    
	    @Value("classpath:prompts/user-message.st")
	    private Resource userMessage;
	    
		@Override
		public Flux<String> getPromptResponseAsStream(String query) {
			String systemText = loadFile(systemMessageResource);
			return this.chatClient.prompt().system(system ->system.text(systemText))
					                       .user(user ->user.text(this.userMessage)
					                       .param("concept",query))
			                              .stream()
			                              .content();
		}
		private String loadFile(Resource resource) {
		    try {
		        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
		    } catch (Exception e) {
		        throw new RuntimeException(e);
		    }
		}
//------------------------------------------------		

}
