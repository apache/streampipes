iconProviderConfig.$inject = ['$mdIconProvider'];
export default function iconProviderConfig($mdIconProvider, apiConstants) {
	var getServerUrl = function() {
		return apiConstants.contextPath + apiConstants.api;
		//return "http://localhost:8080";
	}


	$mdIconProvider
		.iconSet('action', 'img/svg/svg-sprite-action.svg', 24)
		.iconSet('alert', 'img/svg/svg-sprite-alert.svg', 24)
		.iconSet('av', 'img/svg/svg-sprite-av.svg', 24)
		.iconSet('communication', 'img/svg/svg-sprite-communication.svg', 24)
		.iconSet('content', 'img/svg/svg-sprite-content.svg', 24)
		.iconSet('device', 'img/svg/svg-sprite-device.svg', 24)
		.iconSet('editor', 'img/svg/svg-sprite-editor.svg', 24)
		.iconSet('file', 'img/svg/svg-sprite-file.svg', 24)
		.iconSet('hardware', 'img/svg/svg-sprite-hardware.svg', 24)
		.iconSet('image', 'img/svg/svg-sprite-image.svg', 24)
		.iconSet('maps', 'img/svg/svg-sprite-maps.svg', 24)
		.iconSet('navigation', 'img/svg/svg-sprite-navigation.svg', 24)
		.iconSet('notification', 'img/svg/svg-sprite-notification.svg', 24)
		.iconSet('social', 'img/svg/svg-sprite-social.svg', 24)
		.iconSet('toggle', 'img/svg/svg-sprite-toggle.svg', 24)
		.iconSet('avatars', 'img/svg/avatar-icons.svg', 24)
        .iconSet('home', 'img/svg/ic_home_white_24px.svg', 24)
        .iconSet('download', 'img/svg/ic_file_download_white_24px.svg', 24)
		.defaultIconSet('img/svg/svg-sprite-action.svg', 24);
};
