package org.apache.streampipes.connect.iiot.adapters.oi4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Implementation of network message as defined by the Open Industry 4.0 Alliance.
 *
 * @see <a href="https://openindustry4.com/fileadmin/Dateien/Downloads/OEC_Development_Guideline_V1.1.1.pdf">Open Insdustry 4.0 Alliance Development Guideline, p.80</a>}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NetworkMessage(
    @JsonProperty("MessageId") String messageId,
    @JsonProperty("MessageType") String messageType,
    @JsonProperty("PublisherId") String publisherId,
    @JsonProperty("Messages") List<DataSetMessage> messages
    ) {
}
