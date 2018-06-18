package org.streampipes.model.modelconnect;

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.SpDataSet;

import javax.persistence.Entity;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass("sp:AdapterSetDescription")
@Entity
public class AdapterSetDescription extends AdapterDescription {

    public AdapterSetDescription() {
    }

    public AdapterSetDescription(FormatDescription formatDescription, ProtocolDescription protocolDescription) {
        super(formatDescription, protocolDescription);
    }

    public AdapterSetDescription(AdapterSetDescription other) {
        super(other);
        if (other.getDataSet() != null) this.setDataSet(new SpDataSet(other.getDataSet()));
    }

    @RdfProperty("sp:hasDataSet")
    private SpDataSet dataSet;

    public SpDataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(SpDataSet dataSet) {
        this.dataSet = dataSet;
    }

}
