package com.github.mdjc.json;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.mdjc.domain.Apartment;
import com.github.mdjc.domain.CondoBill;

@JsonComponent
public class CondoBillJsonComponent {
	public static class Deserializer extends JsonDeserializer<CondoBill> {

		@Override
		public CondoBill deserialize(JsonParser parser, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			 ObjectCodec oc = parser.getCodec();
		    JsonNode node = oc.readTree(parser);
			Apartment apartment = new Apartment(node.findPath("apartment").get("name").asText());
			String description = node.get("description").asText();
			double dueAmount = node.get("dueAmount").asDouble();
			LocalDate dueDate = LocalDate.parse(node.get("dueDate").asText());
			return new CondoBill(description, dueDate, dueAmount, apartment);
		}
		
	}
}
