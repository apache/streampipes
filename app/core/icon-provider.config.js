iconProviderConfig.$inject = ['$mdIconProvider'];
export default function iconProviderConfig($mdIconProvider) {
	var getServerUrl = function() {
		//return apiConstants.contextPath + apiConstants.api;
		return "http://localhost:8080";
	}


	$mdIconProvider
		.iconSet('action', getServerUrl() + '/semantic-epa-backend/img/svg/svg-sprite-action.svg', 24)
		.iconSet('alert', getServerUrl() + '/semantic-epa-backend/img/svg/svg-sprite-alert.svg', 24)
		.iconSet('av', getServerUrl() + '/semantic-epa-backend/img/svg/svg-sprite-av.svg', 24)
		.iconSet('communication', getServerUrl() + '/semantic-epa-backend/img/svg/svg-sprite-communication.svg', 24)
		.iconSet('content', getServerUrl() + '/semantic-epa-backend/img/svg/svg-sprite-content.svg', 24)
		.iconSet('device', getServerUrl() + '/semantic-epa-backend/img/svg/svg-sprite-device.svg', 24)
		.iconSet('editor', getServerUrl() + '/semantic-epa-backend/img/svg/svg-sprite-editor.svg', 24)
		.iconSet('file', getServerUrl() + '/semantic-epa-backend/img/svg/svg-sprite-file.svg', 24)
		.iconSet('hardware', getServerUrl() + '/semantic-epa-backend/img/svg/svg-sprite-hardware.svg', 24)
		.iconSet('image', getServerUrl() + '/semantic-epa-backend/img/svg/svg-sprite-image.svg', 24)
		.iconSet('maps', getServerUrl() + '/semantic-epa-backend/img/svg/svg-sprite-maps.svg', 24)
		.iconSet('navigation', getServerUrl() + '/semantic-epa-backend/img/svg/svg-sprite-navigation.svg', 24)
		.iconSet('notification', getServerUrl() + '/semantic-epa-backend/img/svg/svg-sprite-notification.svg', 24)
		.iconSet('social', getServerUrl() + '/semantic-epa-backend/img/svg/svg-sprite-social.svg', 24)
		.iconSet('toggle', getServerUrl() + '/semantic-epa-backend/img/svg/svg-sprite-toggle.svg', 24)
		.iconSet('avatars', getServerUrl() + '/semantic-epa-backend/img/svg/avatar-icons.svg', 24)
		.defaultIconSet(getServerUrl() + '/semantic-epa-backend/img/svg/svg-sprite-action.svg', 24);
};
