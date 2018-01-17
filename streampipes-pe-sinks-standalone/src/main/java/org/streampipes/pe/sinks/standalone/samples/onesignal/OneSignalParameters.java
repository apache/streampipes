package org.streampipes.pe.sinks.standalone.samples.onesignal;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class OneSignalParameters extends EventSinkBindingParams {

    private String title;
    private String content;
    private String appId;
    private String apiKey;

    public OneSignalParameters(DataSinkInvocation graph, String content, String appId, String apiKey) {
        super(graph);
        this.content = content;
        this.appId = appId;
        this.apiKey = apiKey;
    }

    public String getContent() {
        return content;
    }

    public String getAppId() { return appId; }

    public String getApiKey() { return apiKey; }
}
