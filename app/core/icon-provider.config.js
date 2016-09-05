iconProviderConfig.$inject = ['$mdIconProvider'];
export default function iconProviderConfig($mdIconProvider) {
	$mdIconProvider
		.iconSet('action', '/semantic-epa-backend/img/svg/svg-sprite-action.svg', 24)
		.iconSet('alert', '/semantic-epa-backend/img/svg/svg-sprite-alert.svg', 24)
		.iconSet('av', '/semantic-epa-backend/img/svg/svg-sprite-av.svg', 24)
		.iconSet('communication', '/semantic-epa-backend/img/svg/svg-sprite-communication.svg', 24)
		.iconSet('content', '/semantic-epa-backend/img/svg/svg-sprite-content.svg', 24)
		.iconSet('device', '/semantic-epa-backend/img/svg/svg-sprite-device.svg', 24)
		.iconSet('editor', '/semantic-epa-backend/img/svg/svg-sprite-editor.svg', 24)
		.iconSet('file', '/semantic-epa-backend/img/svg/svg-sprite-file.svg', 24)
		.iconSet('hardware', '/semantic-epa-backend/img/svg/svg-sprite-hardware.svg', 24)
		.iconSet('image', '/semantic-epa-backend/img/svg/svg-sprite-image.svg', 24)
		.iconSet('maps', '/semantic-epa-backend/img/svg/svg-sprite-maps.svg', 24)
		.iconSet('navigation', '/semantic-epa-backend/img/svg/svg-sprite-navigation.svg', 24)
		.iconSet('notification', '/semantic-epa-backend/img/svg/svg-sprite-notification.svg', 24)
		.iconSet('social', '/semantic-epa-backend/img/svg/svg-sprite-social.svg', 24)
		.iconSet('toggle', '/semantic-epa-backend/img/svg/svg-sprite-toggle.svg', 24)
		.iconSet('avatars', '/semantic-epa-backend/img/svg/avatar-icons.svg', 24)
		.defaultIconSet('/semantic-epa-backend/img/svg/svg-sprite-action.svg', 24);
};
