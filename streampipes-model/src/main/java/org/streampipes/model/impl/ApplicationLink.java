package org.streampipes.model.impl;

import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;
import org.streampipes.model.UnnamedSEPAElement;

import javax.persistence.Entity;

/**
 * Created by riemer on 11.10.2016.
 */
@RdfsClass("sepa:ApplicationLink")
@Entity
public class ApplicationLink extends UnnamedSEPAElement{

    @RdfProperty("sepa:hasName")
    protected String applicationName;

    @RdfProperty("sepa:hasDescription")
    protected String applicationDescription;

    @RdfProperty("sepa:applicationUrl")
    protected String applicationUrl;

    @RdfProperty("sepa:hasIcon")
    protected String applicationIconUrl;

    @RdfProperty("sepa:applicationLinkType")
    protected String applicationLinkType;

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
