
package org.streampipes.connect.rest.master.hackathon.model;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResultObject {

    @SerializedName("created")
    private String mCreated;
    @SerializedName("id")
    private String mId;
    @SerializedName("iteration")
    private String mIteration;
    @SerializedName("predictions")
    private List<Prediction> mPredictions;
    @SerializedName("project")
    private String mProject;

    public String getCreated() {
        return mCreated;
    }

    public void setCreated(String created) {
        mCreated = created;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getIteration() {
        return mIteration;
    }

    public void setIteration(String iteration) {
        mIteration = iteration;
    }

    public List<Prediction> getPredictions() {
        return mPredictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        mPredictions = predictions;
    }

    public String getProject() {
        return mProject;
    }

    public void setProject(String project) {
        mProject = project;
    }

}
