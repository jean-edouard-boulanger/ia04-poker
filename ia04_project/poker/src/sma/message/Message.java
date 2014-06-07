package sma.message;

import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.io.StringWriter;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base class of exchanged message data.
 * This base class provide JSON serialization/de-serialization methods (Jackson annotation are used
 * to encode the effective class type of Message instances).
 * 
 * An implementation of the visitor pattern is providing, allowing a visitor object to handle 
 * each type of messages.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")  
public abstract class Message {
	
	/**
	 * This function implementation should call the appropriate method from the visitor object and
	 * indicate whether the message was processed or not (implementation of the visitor pattern).
	 * @param visitor	The visitor containing handler methods.
	 * @param aclMsg	The original Jade message, to be forwarded to the visitor.
	 * @return true if the message was processed, false otherwise.
	 */
	public abstract boolean accept(MessageVisitor visitor, ACLMessage aclMsg);
	
	/**
	 * Create a message from a JSON string.
	 * @param JSONSerializedMessage The message to de-serialize in JSON format.
	 * @return	The de-serialized message
	 * @throws IOException	If the JSON string was badly formated or do not correspond to any Message sub-type.
	 */
	public static Message fromJson(String JSONSerializedMessage) throws IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Fix AID deserialization
		return (Message)mapper.readValue(JSONSerializedMessage, Message.class);
	}
	
	/**
	 * Serialize the message in the JSON format.
	 * @return the serialized message in JSON
	 * @throws IOException
	 */
	public String toJson() throws IOException{
		StringWriter sw = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(sw, this);
		return sw.toString();
	}
}
