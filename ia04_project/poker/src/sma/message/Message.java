package sma.message;

import java.io.IOException;
import java.io.StringWriter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")  
public abstract class Message {
	public abstract boolean accept(MessageVisitor visitor);
	
	public static Message fromJson(String JSONSerializedMessage) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		return (Message)mapper.readValue(JSONSerializedMessage, Message.class);
	}
	
	public String toJson() throws JsonGenerationException, JsonMappingException, IOException{
		StringWriter sw = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(sw, this);
		return sw.toString();
	}
}
