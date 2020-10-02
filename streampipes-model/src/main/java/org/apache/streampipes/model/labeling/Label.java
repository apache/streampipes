package org.apache.streampipes.model.labeling;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import io.fogsy.empire.annotations.RdfProperty;
import org.apache.streampipes.model.shared.annotation.TsModel;

@TsModel
public class Label {
    private String name;
    private String color;
    private String internalName;

    @JsonProperty("_id")
    private @SerializedName("_id") String id;

    @JsonProperty("_rev")
    private @SerializedName("_rev") String rev;

    public Label() {
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public String getColor() { return color; }
    public void setColor(String color) {
        this.color = color;
    }

    public String getInternalName() {
        return internalName;
    }
    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) { this.id = id; }

    public String getRev() {
        return rev;
    }
    public void setRev(String rev) {
        this.rev = rev;
    }
}
