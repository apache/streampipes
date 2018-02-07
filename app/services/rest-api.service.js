import _ from 'npm/lodash';


export class RestApi {
    
    constructor ($http, apiConstants, AuthStatusService) {
        this.AuthStatusService = AuthStatusService;
        this.$http = $http;
        this.apiConstants = apiConstants;
    }

    getServerUrl() {
        return this.apiConstants.contextPath + this.apiConstants.api;
    }

    urlBase() {
        return this.getServerUrl() +'/users/' + this.AuthStatusService.email;
    };

    getUserDetails() {
        return this.$http.get(this.urlBase());
    }

    updateUserDetails(user) {
        return this.$http.put(this.urlBase(), user);
    }

    getBlocks() {
        return this.$http.get(this.urlBase() + "/block");
    };
    saveBlock(block) {
        return this.$http.post(this.urlBase() + "/block", block);
    };

    getOwnActions() {
        return this.$http.get(this.urlBase() +"/actions/own");
    };

    getAvailableActions() {
        return this.$http.get(this.urlBase() +"/actions/available");
    };

    getPreferredActions() {
        return this.$http.get(this.urlBase() +"/actions/favorites");
    };

    addPreferredAction(elementUri) {
        return this.$http({
            method: 'POST',
            url: this.urlBase() + "/actions/favorites",
            data: $.param({uri: elementUri}),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
    }

    removePreferredAction(elementUri) {
        return this.$http({
            method: 'DELETE',
            url: this.urlBase() + "/actions/favorites/" + this.encodeURIComponent(elementUri)
        })
    }

    getOwnSepas() {
        return this.$http.get(this.urlBase() +"/sepas/own");
    };

    getAvailableSepas() {
        return this.$http.get(this.urlBase() +"/sepas/available");
    };

    getPreferredSepas() {
        return this.$http.get(this.urlBase() +"/sepas/favorites");
    };

    addPreferredSepa(elementUri) {
        return this.$http({
            method: 'POST',
            url: this.urlBase() + "/sepas/favorites",
            data: $.param({uri: elementUri}),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
    }

    removePreferredSepa(elementUri) {
        return this.$http({
            method: 'DELETE',
            url: this.urlBase() + "/sepas/favorites/" + this.encodeURIComponent(elementUri)
        })
    }

    getOwnSources() {
        return this.$http.get(this.urlBase() +"/sources/own");
    };

    getAvailableSources() {
        return this.$http.get(this.urlBase() +"/sources/available");
    };

    getPreferredSources() {
        return this.$http.get(this.urlBase() +"/sources/favorites");
    };

    addPreferredSource(elementUri) {
        return this.$http({
            method: 'POST',
            url: this.urlBase() + "/sources/favorites",
            data: $.param({uri: elementUri}),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
    }

    removePreferredSource(elementUri) {
        return this.$http({
            method: 'DELETE',
            url: this.urlBase() + "/sources/favorites/" + this.encodeURIComponent(elementUri)
        })
    }

    getOwnStreams(source){
        return this.$http.get(this.urlBase() + "/sources/" + this.encodeURIComponent(source.uri) + "/streams");

    };

    add(elementUri, ispublic) {
        return this.$http({
            method: 'POST',
            url: this.urlBase() + "/element",
            data: $.param({uri: elementUri, publicElement: ispublic}),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
    }

    addBatch(elementUris, ispublic) {
        return this.$http({
            method: 'POST',
            url: this.urlBase() + "/element/batch",
            data: $.param({uri: elementUris, publicElement: ispublic}),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
    }

    update(elementUri) {
        return this.$http({
            method: 'PUT',
            url: this.urlBase() + "/element/" + this.encodeURIComponent(elementUri)
        })
    }

    del(elementUri) {
        return this.$http({
            method: 'DELETE',
            url: this.urlBase() + "/element/" + this.encodeURIComponent(elementUri)
        })
    }

    jsonld(elementUri) {
        return this.$http.get(this.urlBase() +"/element/" + this.encodeURIComponent(elementUri) +"/jsonld");
    }

    configured() {
        return this.$http.get(this.getServerUrl() + "/setup/configured");

    }

    getConfiguration() {
        return this.$http.get(this.getServerUrl() +"/setup/configuration");
        //return this.$http.get(this.getServerUrl() +"/semantic-epa-backend/api/v2/setup/configured");
    }

    updateConfiguration(config) {
        return this.$http.put(this.getServerUrl() +"/setup/configuration", config);
        //return this.$http.get(this.getServerUrl() +"/semantic-epa-backend/api/v2/setup/configured");
    };

    getOwnPipelines() {
        return this.$http.get(this.urlBase() +"/pipelines/own");
        //return this.$http.get("/semantic-epa-backend/api/pipelines");
    };

    getSystemPipelines() {
        return this.$http.get(this.urlBase() +"/pipelines/system");
    };

    storePipeline(pipeline) {
        return this.$http.post(this.urlBase() +"/pipelines", pipeline);
    }

    updateStream(stream) {
        return this.$http.post(this.urlBase() +"/streams/update", stream);
    }

    deleteOwnPipeline(pipelineId) {

        // delete all the widgets that use the pipeline results
        this.$http.get("/dashboard/_all_docs?include_docs=true").then(function(data) {
            var toDelete = _.chain(data.data.rows)
                .filter(function(o) {
                    return o.doc.visualisation.pipelineId == pipelineId;
                }).value();

            _.map(toDelete, function(o) {
                this.$http.delete("/dashboard/" + o.doc._id + '?rev=' + o.doc._rev);
            });

        });

        return this.$http({
            method: 'DELETE',
            url: this.urlBase() + "/pipelines/" +pipelineId
        });
    }

    getPipelineCategories() {
        return this.$http.get(this.urlBase() +"/pipelinecategories");
    };

    storePipelineCategory(pipelineCategory) {
        return this.$http.post(this.urlBase() +"/pipelinecategories", pipelineCategory);
    };

    deletePipelineCategory(categoryId) {
        return this.$http({
            method: 'DELETE',
            url: this.urlBase() + "/pipelinecategories/" +categoryId
        });
    }

    recommendPipelineElement(pipeline) {
        return this.$http.post(this.urlBase() +"/pipelines/recommend", pipeline);
    }

    updatePartialPipeline(pipeline) {
        return this.$http.post(this.urlBase() +"/pipelines/update", pipeline);
    }

    startPipeline(pipelineId) {
        return this.$http.get(this.urlBase() +"/pipelines/" +pipelineId +"/start");
    }

    stopPipeline(pipelineId) {
        return this.$http.get(this.urlBase() +"/pipelines/" +pipelineId +"/stop");
    }

    getPipelineById(pipelineId) {
        return this.$http.get(this.urlBase() + "/pipelines/" + pipelineId);
    }

    getPipelineStatusById(pipelineId) {
        return this.$http.get(this.urlBase() + "/pipelines/" + pipelineId +"/status");
    }

    updatePipeline(pipeline){
        var pipelineId = pipeline._id;
        return this.$http.put(this.urlBase() + "/pipelines/" + pipelineId, pipeline);
    }

    getNotifications() {
        return this.$http.get(this.urlBase() +"/notifications");
    }

    getUnreadNotifications() {
        return this.$http.get(this.urlBase() +"/notifications/unread");
    }

    updateNotification(notificationId) {
        return this.$http.put(this.urlBase() +"/notifications/" +notificationId);
    }

    deleteNotifications(notificationId) {
        return this.$http({
            method: 'DELETE',
            url: this.urlBase() + "/notifications/" +notificationId
        });
    }

    getSepaById(elementId) {
        return this.$http.get(this.urlBase() +"/sepas/" + this.encodeURIComponent(elementId));
    }

    getActionById(elementId) {
        return this.$http.get(this.urlBase() +"/actions/" + this.encodeURIComponent(elementId));
    };

    getOntologyProperties() {
        return this.$http.get( this.getServerUrl() + "/ontology/properties");
    };

    getOntologyPropertyDetails(propertyId) {
        return this.$http.get(this.getServerUrl() + "/ontology/properties/" + this.encodeURIComponent(propertyId));
    }

    addOntologyProperty(propertyData) {
        return this.$http.post(this.getServerUrl() + "/ontology/properties", propertyData);
    }

    getOntologyConcepts() {
        return this.$http.get(this.getServerUrl() + "/ontology/types");
    };

    getOntologyConceptDetails(conceptId) {
        return this.$http.get(this.getServerUrl() + "/ontology/types/" + this.encodeURIComponent(conceptId));
    }

    getOntologyNamespaces() {
        return this.$http.get(this.getServerUrl() + "/ontology/namespaces");
    }

    addOntologyNamespace(namespace) {
        return this.$http.post(this.getServerUrl() + "/ontology/namespaces", namespace);
    }

    deleteOntologyNamespace(prefix) {
        return this.$http({
            method: 'DELETE',
            url: this.getServerUrl() + "/ontology/namespaces/" + this.encodeURIComponent(prefix)
        });
    }

    addOntologyConcept(conceptData) {
        return this.$http.post(this.getServerUrl() + "/ontology/types", conceptData);
    }

    addOntologyInstance(instanceData) {
        return this.$http.post(this.getServerUrl() + "/ontology/instances", instanceData);
    }

    getOntologyInstanceDetails(instanceId) {
        return this.$http.get(this.getServerUrl() + "/ontology/instances/" + this.encodeURIComponent(instanceId));
    }

    updateOntologyProperty(propertyId, propertyData) {
        return this.$http.put(this.getServerUrl() + "/ontology/properties/" + this.encodeURIComponent(propertyId), propertyData);
    }

    updateOntologyConcept(conceptId, conceptData) {
        return this.$http.put(this.getServerUrl() + "/ontology/types/" + this.encodeURIComponent(conceptId), conceptData);
    }

    updateOntologyInstance(instanceId, instanceData) {
        return this.$http.put(this.getServerUrl() + "/ontology/instances/" + this.encodeURIComponent(instanceId), instanceData);
    }

    deleteOntologyInstance(instanceId) {
        return this.$http({
            method: 'DELETE',
            url: this.getServerUrl() + "/ontology/instances/" + this.encodeURIComponent(instanceId)
        });
    }

    deleteOntologyProperty(propertyId) {
        return this.$http({
            method: 'DELETE',
            url: this.getServerUrl() + "/ontology/properties/" + this.encodeURIComponent(propertyId)
        });
    }

    deleteOntologyConcept(conceptId) {
        return this.$http({
            method: 'DELETE',
            url: this.getServerUrl() + "/ontology/types/" + this.encodeURIComponent(conceptId)
        });
    }

    getAvailableContexts() {
        return this.$http.get(this.getServerUrl()+ "/contexts");
    };

    deleteContext(contextId) {
        return this.$http({
            method: 'DELETE',
            url: this.getServerUrl() + "/contexts/" + this.encodeURIComponent(contextId)
        });
    }

    getDomainKnowledgeItems(query) {
        return this.$http.post(this.getServerUrl() + "/autocomplete/domain", query);
    };


    getSepasFromOntology() {
        return this.$http.get(this.getServerUrl() + "/ontology/sepas")
    }

    getSepaDetailsFromOntology(uri, keepIds) {
        return this.$http.get(this.getServerUrl() + "/ontology/sepas/" + this.encodeURIComponent(uri) +"?keepIds=" +keepIds);
    }

    getSourcesFromOntology() {
        return this.$http.get(this.getServerUrl() + "/ontology/sources")
    }

    getSourceDetailsFromOntology(uri, keepIds) {
        return this.$http.get(this.getServerUrl() + "/ontology/sources/" + this.encodeURIComponent(uri) +"?keepIds=" +keepIds);
    }

    getActionsFromOntology() {
        return this.$http.get(this.getServerUrl() + "/ontology/actions")
    }

    getActionDetailsFromOntology(uri, keepIds) {
        return this.$http.get(this.getServerUrl() + "/ontology/actions/" + this.encodeURIComponent(uri) +"?keepIds=" +keepIds);
    }

    getRunningVisualizations() {
        return this.$http.get(this.getServerUrl() + "/visualizations");
    }

    getAllUnits() {
        return this.$http.get(this.getServerUrl() + "/units/instances");
    }

    getAllUnitTypes() {
        return this.$http.get(this.getServerUrl() + "/units/types");
    }

    getUnit(resource) {
        return this.$http.get(this.getServerUrl() + "/units/instances/" + this.encodeURIComponent(resource));
    }

    getEpaCategories() {
        return this.$http.get(this.getServerUrl() + "/categories/epa");
    }

    getEcCategories() {
        return this.$http.get(this.getServerUrl() + "/categories/ec");
    }

    getEpCategories() {
        return this.$http.get(this.getServerUrl() + "/categories/ep");
    }

    getAvailableApps(elementId) {
        return this.$http.get(this.urlBase() +"/marketplace");
    }

    installApp(bundleInfo) {
        return this.$http.post(this.urlBase() +"/marketplace/install", bundleInfo);
    }

    uninstallApp(bundleInfo) {
        return this.$http.post(this.urlBase() +"/marketplace/uninstall", bundleInfo);
    }

    getTargetPods() {
        return this.$http.get(this.urlBase() +"/marketplace/pods");
    }

    getRdfEndpoints() {
        return this.$http.get(this.urlBase() +"/rdfendpoints");
    }

    getRdfEndpointItems() {
        return this.$http.get(this.urlBase() +"/rdfendpoints/items");
    }

    addRdfEndpoint(rdfEndpoint) {
        return this.$http.post(this.urlBase() +"/rdfendpoints", rdfEndpoint);
    }

    removeRdfEndpoint(rdfEndpointId) {
        return this.$http.delete(this.urlBase() +"/rdfendpoints/" +rdfEndpointId);
    }

    getAuthc() {
        return this.$http.get(this.getServerUrl() + "/admin/authc");
    }

    login(credentials) {
        return this.$http.post(this.getServerUrl() + "/admin/login", credentials);
    }

    loginSso(credentials, componentId, sessionId) {
        return this.$http.post(this.getServerUrl() + "/admin/login/" +componentId +"?session=" +sessionId, credentials);
    }

    logout() {
        return this.$http.get(this.getServerUrl() + "/admin/logout");
    }

    setupInstall(setup) {
        return this.$http.post(this.getServerUrl() + "/setup/install", setup);
    }

    register(payload) {
        return this.$http.post(this.getServerUrl() +"/admin/register", payload);
    }

    deployStorm(payload) {
        return this.$http({method: 'GET', responseType : 'arraybuffer', headers: {'Accept' : 'application/zip'}, url: this.urlBase() + '/deploy/storm'})

    }

    getApplicationLinks() {
        return this.$http.get(this.getServerUrl() + "/applink");
    };
}

RestApi.$inject = ['$http', 'apiConstants', 'AuthStatusService'];
