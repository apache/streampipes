/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

// import _ from 'lodash';


import { Injectable } from '@angular/core';
import { PlatformServicesCommons } from '../platform-services/apis/commons.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class RestApi {

    encodeURIComponent: any;

    constructor (private platformServicesCommons: PlatformServicesCommons,
                 private $http: HttpClient) {
    }

    getServerUrl() {
        return this.platformServicesCommons.apiBasePath;
    }

    urlApiBase() {
        return this.platformServicesCommons.apiBasePath;
    }

    getAssetUrl(appId) {
        return this.getServerUrl() + '/pe/' + appId + '/assets';
    }

    updateUserDetails(user) {
        return this.$http.put(this.urlApiBase() + '/users/profile', user);
    }

    configured(): Observable<any> {
        return this.$http.get(this.getServerUrl() + '/setup/configured', {
            headers: { ignoreLoadingBar: '' }
        });
    }

    getUnreadNotificationsCount(): Observable<any> {
        return this.$http.get(this.urlApiBase() + '/notifications/count');
    }

    getOntologyProperties() {
        return this.$http.get( this.getServerUrl() + '/ontology/properties');
    }

    getOntologyPropertyDetails(propertyId) {
        return this.$http.get(this.getServerUrl() + '/ontology/properties/' + encodeURIComponent(propertyId));
    }

    addOntologyProperty(propertyData) {
        return this.$http.post(this.getServerUrl() + '/ontology/properties', propertyData);
    }

    getOntologyConcepts() {
        return this.$http.get(this.getServerUrl() + '/ontology/types');
    }

    getOntologyConceptDetails(conceptId) {
        return this.$http.get(this.getServerUrl() + '/ontology/types/' + encodeURIComponent(conceptId));
    }

    getOntologyNamespaces() {
        return this.$http.get(this.getServerUrl() + '/ontology/namespaces');
    }

    addOntologyNamespace(namespace) {
        return this.$http.post(this.getServerUrl() + '/ontology/namespaces', namespace);
    }

    addOntologyConcept(conceptData) {
        return this.$http.post(this.getServerUrl() + '/ontology/types', conceptData);
    }

    getOntologyInstanceDetails(instanceId) {
        return this.$http.get(this.getServerUrl() + '/ontology/instances/' + encodeURIComponent(instanceId));
    }

    updateOntologyConcept(conceptId, conceptData) {
        return this.$http.put(this.getServerUrl() + '/ontology/types/' + encodeURIComponent(conceptId), conceptData);
    }

    updateOntologyInstance(instanceId, instanceData) {
        return this.$http.put(this.getServerUrl() + '/ontology/instances/' + encodeURIComponent(instanceId), instanceData);
    }

    getAvailableContexts() {
        return this.$http.get(this.getServerUrl() + '/contexts');
    }

    getDomainKnowledgeItems(query) {
        return this.$http.post(this.getServerUrl() + '/autocomplete/domain', query);
    }


    getSepasFromOntology() {
        return this.$http.get(this.getServerUrl() + '/ontology/sepas');
    }

    getSepaDetailsFromOntology(uri, keepIds) {
        return this.$http.get(this.getServerUrl() + '/ontology/sepas/' + encodeURIComponent(uri) + '?keepIds=' + keepIds);
    }

    getSourcesFromOntology() {
        return this.$http.get(this.getServerUrl() + '/ontology/sources');
    }

    getSourceDetailsFromOntology(uri, keepIds) {
        return this.$http.get(this.getServerUrl() + '/ontology/sources/' + encodeURIComponent(uri) + '?keepIds=' + keepIds);
    }

    getActionsFromOntology() {
        return this.$http.get(this.getServerUrl() + '/ontology/actions');
    }

    getActionDetailsFromOntology(uri, keepIds) {
        return this.$http.get(this.getServerUrl() + '/ontology/actions/' + encodeURIComponent(uri) + '?keepIds=' + keepIds);
    }

    getAllUnits() {
        return this.$http.get(this.getServerUrl() + '/units/instances');
    }

    getAllUnitTypes() {
        return this.$http.get(this.getServerUrl() + '/units/types');
    }

    getUnit(resource) {
        return this.$http.get(this.getServerUrl() + '/units/instances/' + encodeURIComponent(resource));
    }

    getEpaCategories() {
        return this.$http.get(this.getServerUrl() + '/categories/epa');
    }

    getAdapterCategories() {
        return this.$http.get(this.getServerUrl() + '/categories/adapter');
    }

    getApplicationLinks() {
        return this.$http.get(this.getServerUrl() + '/applink');
    }

    getFileMetadata() {
        return this.$http.get(this.urlApiBase() + '/files');
    }

    getCachedPipeline() {
        return this.$http.get(this.urlApiBase() + '/pipeline-cache');
    }

    updateCachedPipeline(rawPipelineModel: any) {
        return this.$http.post(this.urlApiBase() + '/pipeline-cache', rawPipelineModel);
    }

    removePipelineFromCache() {
        return this.$http.delete(this.urlApiBase() + '/pipeline-cache');
    }

    getVersionInfo() {
        return this.$http.get(this.getServerUrl() + '/info/versions');
    }

    getSystemInfo() {
        return this.$http.get(this.getServerUrl() + '/info/system');
    }
}
