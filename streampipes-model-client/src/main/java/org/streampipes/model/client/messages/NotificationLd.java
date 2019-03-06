/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.model.client.messages;

import org.apache.commons.lang.RandomStringUtils;
import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfId;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@Namespaces({StreamPipes.NS_PREFIX, StreamPipes.NS})
@RdfsClass(StreamPipes.NOTIFICATION)
@Entity
public class NotificationLd {

    private static final String prefix = "urn:streampipes.org:spi:";

    @RdfId
    @RdfProperty(StreamPipes.HAS_ELEMENT_NAME)
    private String elementId;

    @RdfProperty(StreamPipes.NOTIFICATION_TITLE)
    private String title;

    @RdfProperty(StreamPipes.NOTIFICATION_DESCRIPTION)
    private String description;

    @RdfProperty(StreamPipes.NOTIFICATION_ADDITIONAL_INFORMATION)
    private String additionalInformation;

    public NotificationLd() {
        this.elementId = prefix
                + this.getClass().getSimpleName().toLowerCase()
                + ":"
                + RandomStringUtils.randomAlphabetic(6);
    }

    public NotificationLd(NotificationLd other) {
        this.title = other.getTitle();
        this.description = other.getDescription();
        this.additionalInformation = other.getAdditionalInformation();
    }

    public NotificationLd(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }

    public NotificationLd(String title, String description,
                        String additionalInformation) {
        this();
        this.title = title;
        this.description = description;
        this.additionalInformation = additionalInformation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }
}
