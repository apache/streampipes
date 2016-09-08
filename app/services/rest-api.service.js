restApi.$inject = ['$rootScope', '$http', 'apiConstants'];

//export default angular.module('sp.services', [spConstants])

	//.factory('restApi', function ($rootScope, $http, apiConstants) {

export default function restApi($rootScope, $http, apiConstants) {

	var restApi = {};

	var getServerUrl = function() {
		//return apiConstants.contextPath + apiConstants.api;
		return "http://localhost:8080"  + apiConstants.contextPath + apiConstants.api;
	}

	var urlBase = function() {
		return getServerUrl() +'/users/' +$rootScope.email;
	};

	restApi.getBlocks = function () {
		return $http.get(urlBase() + "/block");
	};
	restApi.saveBlock = function(block) {
		return $http.post(urlBase() + "/block", block);
	};

	restApi.getOwnActions = function () {
		return $http.get(urlBase() +"/actions/own");
	};

	restApi.getAvailableActions = function () {
		return $http.get(urlBase() +"/actions/available");
	};

	restApi.getPreferredActions = function () {
		return $http.get(urlBase() +"/actions/favorites");
	};

	restApi.addPreferredAction = function(elementUri) {
		return $http({
			method: 'POST',
			url: urlBase() + "/actions/favorites",
			data: $.param({uri: elementUri}),
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
	}

	restApi.removePreferredAction = function(elementUri) {
		return $http({
			method: 'DELETE',
			url: urlBase() + "/actions/favorites/" + encodeURIComponent(elementUri)
		})
	}

	restApi.getOwnSepas = function () {
		return $http.get(urlBase() +"/sepas/own");
	};

	restApi.getAvailableSepas = function () {
		return $http.get(urlBase() +"/sepas/available");
	};

	restApi.getPreferredSepas = function () {
		return $http.get(urlBase() +"/sepas/favorites");
	};

	restApi.addPreferredSepa = function(elementUri) {
		return $http({
			method: 'POST',
			url: urlBase() + "/sepas/favorites",
			data: $.param({uri: elementUri}),
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
	}

	restApi.removePreferredSepa = function(elementUri) {
		return $http({
			method: 'DELETE',
			url: urlBase() + "/sepas/favorites/" + encodeURIComponent(elementUri)
		})
	}

	restApi.getOwnSources = function () {
		return $http.get(urlBase() +"/sources/own");
	};

	restApi.getAvailableSources = function () {
		return $http.get(urlBase() +"/sources/available");
	};

	restApi.getPreferredSources = function () {
		return $http.get(urlBase() +"/sources/favorites");
	};

	restApi.addPreferredSource = function(elementUri) {
		return $http({
			method: 'POST',
			url: urlBase() + "/sources/favorites",
			data: $.param({uri: elementUri}),
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
	}

	restApi.removePreferredSource = function(elementUri) {
		return $http({
			method: 'DELETE',
			url: urlBase() + "/sources/favorites/" + encodeURIComponent(elementUri)
		})
	}

	restApi.getOwnStreams = function(source){
		return $http.get(urlBase() + "/sources/" + encodeURIComponent(source.uri) + "/streams");

	};

	restApi.add = function(elementUri, ispublic) {
		return $http({
			method: 'POST',
			url: urlBase() + "/element",
			data: $.param({uri: elementUri, publicElement: ispublic}),
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
	}

	restApi.addBatch = function(elementUris, ispublic) {
		return $http({
			method: 'POST',
			url: urlBase() + "/element/batch",
			data: $.param({uri: elementUris, publicElement: ispublic}),
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
	}

	restApi.update = function(elementUri) {
		return $http({
			method: 'PUT',
			url: urlBase() + "/element/" + encodeURIComponent(elementUri)
		})
	}

	restApi.del = function(elementUri) {
		return $http({
			method: 'DELETE',
			url: urlBase() + "/element/" + encodeURIComponent(elementUri)
		})
	}

	restApi.jsonld = function(elementUri) {
		return $http.get(urlBase() +"/element/" + encodeURIComponent(elementUri) +"/jsonld");
	}

	restApi.configured = function() {
		return $http.get(getServerUrl() + "/setup/configured");
		//return $http.get(getServerUrl() +"/semantic-epa-backend/api/v2/setup/configured");

	}

	restApi.getConfiguration = function() {
		return $http.get(getServerUrl() +"/setup/configuration");
	}

	restApi.updateConfiguration = function(config) {
		return $http.put(getServerUrl() +"/setup/configuration", config);
	};

	restApi.getOwnPipelines = function() {
		return $http.get(urlBase() +"/pipelines/own");
		//return $http.get("/semantic-epa-backend/api/pipelines");
	};

	restApi.storePipeline = function(pipeline) {
		return $http.post(urlBase() +"/pipelines", pipeline);
	}

	restApi.deleteOwnPipeline = function(pipelineId) {
		return $http({
			method: 'DELETE',
			url: urlBase() + "/pipelines/" +pipelineId
		})
	}

	restApi.getPipelineCategories = function() {
		return $http.get(urlBase() +"/pipelinecategories");
	};

	restApi.storePipelineCategory = function(pipelineCategory) {
		return $http.post(urlBase() +"/pipelinecategories", pipelineCategory);
	};

	restApi.deletePipelineCategory = function(categoryId) {
		return $http({
			method: 'DELETE',
			url: urlBase() + "/pipelinecategories/" +categoryId
		});
	}

	restApi.recommendPipelineElement = function(pipeline) {
		return $http.post(urlBase() +"/pipelines/recommend", pipeline);
	}

	restApi.updatePartialPipeline = function(pipeline) {
		return $http.post(urlBase() +"/pipelines/update", pipeline);
	}

	restApi.startPipeline = function(pipelineId) {
		return $http.get(urlBase() +"/pipelines/" +pipelineId +"/start");
	}

	restApi.stopPipeline = function(pipelineId) {
		return $http.get(urlBase() +"/pipelines/" +pipelineId +"/stop");
	}

	restApi.getPipelineById = function(pipelineId) {
		return $http.get(urlBase() + "/pipelines/" + pipelineId);
	}

	restApi.getPipelineStatusById = function(pipelineId) {
		return $http.get(urlBase() + "/pipelines/" + pipelineId +"/status");
	}

	restApi.updatePipeline = function(pipeline){
		var pipelineId = pipeline._id;
		return $http.put(urlBase() + "/pipelines/" + pipelineId, pipeline);
	}

	restApi.getNotifications = function() {
		return $http.get(urlBase() +"/notifications");
	}

	restApi.getUnreadNotifications = function() {
		return $http.get(urlBase() +"/notifications/unread");
	}

	restApi.updateNotification = function(notificationId) {
		return $http.put(urlBase() +"/notifications/" +notificationId);
	}

	restApi.deleteNotifications = function(notificationId) {
		return $http({
			method: 'DELETE',
			url: urlBase() + "/notifications/" +notificationId
		});
	}

	restApi.getSepaById = function(elementId) {
		return $http.get(urlBase() +"/sepas/" + encodeURIComponent(elementId));
	}

	restApi.getActionById = function(elementId) {
		return $http.get(urlBase() +"/actions/" + encodeURIComponent(elementId));
	};

	restApi.getOntologyProperties = function() {
		return $http.get( getServerUrl() + "/ontology/properties");
	};

	restApi.getOntologyPropertyDetails = function(propertyId) {
		return $http.get(getServerUrl() + "/ontology/properties/" + encodeURIComponent(propertyId));
	}

	restApi.addOntologyProperty = function(propertyData) {
		return $http.post(getServerUrl() + "/ontology/properties", propertyData);
	}

	restApi.getOntologyConcepts = function() {
		return $http.get(getServerUrl() + "/ontology/types");
	};

	restApi.getOntologyConceptDetails = function(conceptId) {
		return $http.get(getServerUrl() + "/ontology/types/" + encodeURIComponent(conceptId));
	}

	restApi.getOntologyNamespaces = function() {
		return $http.get(getServerUrl() + "/ontology/namespaces");
	}

	restApi.addOntologyNamespace = function(namespace) {
		return $http.post(getServerUrl() + "/ontology/namespaces", namespace);
	}

	restApi.deleteOntologyNamespace = function(prefix) {
		return $http({
			method: 'DELETE',
			url: getServerUrl() + "/ontology/namespaces/" + encodeURIComponent(prefix)
		});
	}

	restApi.addOntologyConcept = function(conceptData) {
		return $http.post(getServerUrl() + "/ontology/types", conceptData);
	}

	restApi.addOntologyInstance = function(instanceData) {
		return $http.post(getServerUrl() + "/ontology/instances", instanceData);
	}

	restApi.getOntologyInstanceDetails = function(instanceId) {
		return $http.get(getServerUrl() + "/ontology/instances/" + encodeURIComponent(instanceId));
	}

	restApi.updateOntologyProperty = function(propertyId, propertyData) {
		return $http.put(getServerUrl() + "/ontology/properties/" + encodeURIComponent(propertyId), propertyData);
	}

	restApi.updateOntologyConcept = function(conceptId, conceptData) {
		return $http.put(getServerUrl() + "/ontology/types/" + encodeURIComponent(conceptId), conceptData);
	}

	restApi.updateOntologyInstance = function(instanceId, instanceData) {
		return $http.put(getServerUrl() + "/ontology/instances/" + encodeURIComponent(instanceId), instanceData);
	}

	restApi.deleteOntologyInstance = function(instanceId) {
		return $http({
			method: 'DELETE',
			url: getServerUrl() + "/ontology/instances/" + encodeURIComponent(instanceId)
		});
	}

	restApi.deleteOntologyProperty = function(propertyId) {
		return $http({
			method: 'DELETE',
			url: getServerUrl() + "/ontology/properties/" + encodeURIComponent(propertyId)
		});
	}

	restApi.deleteOntologyConcept = function(conceptId) {
		return $http({
			method: 'DELETE',
			url: getServerUrl() + "/ontology/types/" + encodeURIComponent(conceptId)
		});
	}

	restApi.getAvailableContexts = function() {
		return $http.get(getServerUrl()+ "/contexts");
	};

	restApi.deleteContext = function(contextId) {
		return $http({
			method: 'DELETE',
			url: getServerUrl() + "/contexts/" + encodeURIComponent(contextId)
		});
	}

	restApi.getDomainKnowledgeItems = function(query) {
		return $http.post(urlBase() + "/autocomplete/domain", query);
	};


	restApi.getSepasFromOntology = function() {
		return $http.get(getServerUrl() + "/ontology/sepas")
	}

	restApi.getSepaDetailsFromOntology = function(uri, keepIds) {
		return $http.get(getServerUrl() + "/ontology/sepas/" + encodeURIComponent(uri) +"?keepIds=" +keepIds);
	}

	restApi.getSourcesFromOntology = function() {
		return $http.get(getServerUrl() + "/ontology/sources")
	}

	restApi.getSourceDetailsFromOntology = function(uri, keepIds) {
		return $http.get(getServerUrl() + "/ontology/sources/" + encodeURIComponent(uri) +"?keepIds=" +keepIds);
	}

	restApi.getActionsFromOntology = function() {
		return $http.get(getServerUrl() + "/ontology/actions")
	}

	restApi.getActionDetailsFromOntology = function(uri, keepIds) {
		return $http.get(getServerUrl() + "/ontology/actions/" + encodeURIComponent(uri) +"?keepIds=" +keepIds);
	}

	restApi.getRunningVisualizations = function() {
		return $http.get(getServerUrl() + "/visualizations");
	}

	restApi.getAllUnits = function() {
		return $http.get(getServerUrl() + "/units/instances");
	}

	restApi.getAllUnitTypes = function() {
		return $http.get(getServerUrl() + "/units/types");
	}

	restApi.getUnit = function(resource) {
		return $http.get(getServerUrl() + "/units/instances/" + encodeURIComponent(resource));
	}

	restApi.getEpaCategories = function() {
		return $http.get(getServerUrl() + "/categories/epa");
	}

	restApi.getEcCategories = function() {
		return $http.get(getServerUrl() + "/categories/ec");
	}

	restApi.getEpCategories = function() {
		return $http.get(getServerUrl() + "/categories/ep");
	}

	restApi.getAvailableApps = function(elementId) {
		return $http.get(urlBase() +"/marketplace");
	}

	restApi.installApp = function(bundleInfo) {
		return $http.post(urlBase() +"/marketplace/install", bundleInfo);
	}

	restApi.uninstallApp = function(bundleInfo) {
		return $http.post(urlBase() +"/marketplace/uninstall", bundleInfo);
	}

	restApi.getTargetPods = function() {
		return $http.get(urlBase() +"/marketplace/pods");
	}

	restApi.getAuthc = function() {
		return $http.get(getServerUrl() + "/admin/authc");
	}

	restApi.login = function(credentials) {
		return $http.post(getServerUrl() + "/admin/login", credentials);
	}

	restApi.logout = function() {
		return $http.get(getServerUrl() + "/admin/logout");
	}

	restApi.setupInstall = function(setup) {
			return $http.post(getServerUrl() + "/setup/install", setup);
	}

	restApi.register = function(payload) {
			return $http.post(getServerUrl() +"/admin/register", payload);
	}
	
	restApi.deployStorm = function(payload) {
		return $http({method: 'GET', responseType : 'arraybuffer', headers: {'Accept' : 'application/zip'}, url: urlBase() + '/deploy/storm'})

	}

	return restApi;
};
