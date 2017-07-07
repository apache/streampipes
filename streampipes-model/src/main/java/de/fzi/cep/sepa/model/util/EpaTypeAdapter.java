package de.fzi.cep.sepa.model.util;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import de.fzi.cep.sepa.model.impl.EpaType;

public class EpaTypeAdapter extends TypeAdapter<EpaType> {

	@Override
	public void write(JsonWriter out, EpaType value) throws IOException {
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
	public EpaType read(JsonReader in) throws IOException {
		EpaType epaType = null;
		in.beginObject();
		while(in.hasNext()) {
			String name = in.nextName();
			if (name.equals("type"))
				epaType = EpaType.valueOf(in.nextString());
		}
		in.endObject();
		if (epaType != null) return epaType;
		else throw new IOException();
	}

}
