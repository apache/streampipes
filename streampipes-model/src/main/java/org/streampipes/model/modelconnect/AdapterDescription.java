package org.streampipes.model.modelconnect;

import com.google.gson.annotations.SerializedName;
import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.model.util.Cloner;

import javax.persistence.Entity;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass("sp:AdapterDescription")
@Entity
public class AdapterDescription extends NamedStreamPipesEntity {

    @RdfProperty("sp:couchDBId")
    private @SerializedName("_id") String id;

    private @SerializedName("_rev") String rev;

    @RdfProperty("sp:hasFormat")
    private FormatDescription formatDescription;


    @RdfProperty("sp:hasProtocol")
    private ProtocolDescription protocolDescription;


    public AdapterDescription() {
        super();
    }

    public AdapterDescription(AdapterDescription other) {
        super(other);
        if (other.getFormatDescription() != null) this.formatDescription = new FormatDescription(other.getFormatDescription());
        if (other.getProtocolDescription() != null) this.protocolDescription = new ProtocolDescription(other.getProtocolDescription());
    }

    public AdapterDescription(FormatDescription formatDescription, ProtocolDescription protocolDescription) {
        super();
        this.formatDescription = formatDescription;
        this.protocolDescription = protocolDescription;
    }

    public FormatDescription getFormatDescription() {
        return formatDescription;
    }

    public void setFormatDescription(FormatDescription formatDescription) {
        this.formatDescription = formatDescription;
    }

    public ProtocolDescription getProtocolDescription() {
        return protocolDescription;
    }

    public void setProtocolDescription(ProtocolDescription protocolDescription) {
        this.protocolDescription = protocolDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    @Override
    public String toString() {
        return "AdapterDescription{" +
                "id='" + id + '\'' +
                ", rev='" + rev + '\'' +
                ", formatDescription=" + formatDescription +
                ", protocolDescription=" + protocolDescription +
                ", elementId='" + elementId + '\'' +
                ", DOM='" + DOM + '\'' +
                '}';
    }
}
