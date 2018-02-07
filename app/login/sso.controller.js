SsoCtrl.$inject = ['$rootScope', '$scope', '$timeout', '$log', '$location', '$state', '$stateParams', 'RestApi'];

export default function SsoCtrl($rootScope, $scope, $timeout, $log, $location, $state, $stateParams, RestApi) {
	//console.log($stateParams.target);
	console.log($stateParams.target);
	console.log($stateParams);
	//$http.get("/semantic-epa-backend/api/v2/admin/authc").success(function(data){
	RestApi.getAuthc().success(function(data){
		console.log(data);
		console.log(window.location.host);
		console.log(window.location.host +"/#/streampipes/login/" +$stateParams.target);
		if (!data.success) window.top.location.href = "http://" +window.location.host +"/#/streampipes/login/" +$stateParams.target +"?session=" +$stateParams.session;
		else $state.go("ssosuccess");
	})

};
