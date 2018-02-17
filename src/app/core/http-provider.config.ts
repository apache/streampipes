//httpProviderConfig.$inject = ['$httpProvider'];
export default function httpProviderConfig($httpProvider) {
	$httpProvider.defaults.withCredentials = true;
	//$httpProvider.interceptors.push('httpInterceptor');
};
