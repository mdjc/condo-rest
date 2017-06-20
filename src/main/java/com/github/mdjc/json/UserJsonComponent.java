package com.github.mdjc.json;

import java.io.IOException;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.boot.jackson.JsonObjectSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.mdjc.domain.User;

@JsonComponent
public class UserJsonComponent {
	public static class Serializer extends JsonObjectSerializer<User> {
		@Override
		protected void serializeObject(User value, JsonGenerator jgen, SerializerProvider provider)
				throws IOException {	
			jgen.writeStringField("name", value.getUsername());
			jgen.writeStringField("role", value.getRole().toString());
		}		
	}
}
