package com.github.mdjc.json;

import java.io.IOException;

import org.springframework.boot.jackson.JsonObjectSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.mdjc.domain.Building;

public class BuildingJsonComponent extends JsonObjectSerializer<Building>{

	@Override
	protected void serializeObject(Building value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeNumberField("id", value.getId());
		jgen.writeStringField("name", value.getName());
		jgen.writeObjectField("manager", value.getManager());
	}

}
