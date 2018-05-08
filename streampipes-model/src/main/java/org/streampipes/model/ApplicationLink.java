/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.model;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.RDFS;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.APPLICATION_LINK)
@Entity
public class ApplicationLink extends UnnamedStreamPipesEntity {

    @RdfProperty(RDFS.LABEL)
    private String applicationName;

    @RdfProperty(RDFS.DESCRIPTION)
    private String applicationDescription;

    @RdfProperty(StreamPipes.APPLICATION_URL)
    private String applicationUrl;

    @RdfProperty(StreamPipes.ICON_URL)
    private String applicationIconUrl;

    @RdfProperty(StreamPipes.APPLICATION_LINK_TYPE)
    private String applicationLinkType;

    public ApplicationLink() {
        super();
    }

    public ApplicationLink(String applicationName, String applicationDescription, String applicationUrl, String applicationIconUrl) {
        super();
        this.applicationName = applicationName;
        this.applicationDescription = applicationDescription;
        this.applicationUrl = applicationUrl;
        this.applicationIconUrl = applicationIconUrl;
    }

    public ApplicationLink(ApplicationLink other) {
        super(other);
        this.applicationName = other.getApplicationName();
        this.applicationDescription = other.getApplicationDescription();
        this.applicationUrl = other.getApplicationUrl();
        this.applicationIconUrl = other.getApplicationIconUrl();
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationDescription() {
        return applicationDescription;
    }

    public void setApplicationDescription(String applicationDescription) {
        this.applicationDescription = applicationDescription;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    public String getApplicationIconUrl() {
        return applicationIconUrl;
    }

    public void setApplicationIconUrl(String applicationIconUrl) {
        this.applicationIconUrl = applicationIconUrl;
    }

    public String getApplicationLinkType() {
        return applicationLinkType;
    }

    public void setApplicationLinkType(String applicationLinkType) {
        this.applicationLinkType = applicationLinkType;
    }
}
