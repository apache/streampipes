package org.streampipes.serializers.json;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.streampipes.model.DataSinkType;

import java.io.IOException;

public class EcTypeAdapter extends PeTypeAdapter<DataSinkType> {

	@Override
	public void write(JsonWriter out, DataSinkType value) throws IOException {
		write(out, value.getLabel(), value.getDescription(), value.name());
	}

	@Override
	public DataSinkType read(JsonReader in) throws IOException {
		in.beginObject();
		while(in.hasNext()) {
			String name = in.nextString();
			if (name.equals("type"))
				return DataSinkType.valueOf(name);
		}
		throw new IOException();
	}

	

}
