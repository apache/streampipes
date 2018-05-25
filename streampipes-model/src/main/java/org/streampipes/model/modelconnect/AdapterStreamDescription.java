package org.streampipes.model.modelconnect;
import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.SpDataStream;

import javax.persistence.Entity;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass("sp:AdapterStreamDescription")
@Entity
public class AdapterStreamDescription extends AdapterDescription {

    public AdapterStreamDescription() {
    }

    public AdapterStreamDescription(FormatDescription formatDescription, ProtocolDescription protocolDescription) {
        super(formatDescription, protocolDescription);
    }

    @RdfProperty("sp:hasDataStream")
    private SpDataStream dataStream;

    public SpDataStream getDataStream() {
        return dataStream;
    }

    public void setDataStream(SpDataStream dataStream) {
        this.dataStream = dataStream;
    }
}
