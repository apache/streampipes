package de.fzi.cep.sepa.model.util;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import de.fzi.cep.sepa.model.impl.EcType;

public class EcTypeAdapter extends TypeAdapter<EcType> {

	@Override
	public void write(JsonWriter out, EcType value) throws IOException {
		out.beginObject();
		out.name("type");
		out.value(value.name());
		out.name("label");
		out.value(value.getLabel());
		out.name("description");
		out.value(value.getDescription());
		out.endObject();
	}

	@Override
	public EcType read(JsonReader in) throws IOException {
		in.beginObject();
		while(in.hasNext()) {
			String name = in.nextString();
			if (name.equals("type"))
				return EcType.valueOf(name);
		}
		throw new IOException();
	}

	

}
