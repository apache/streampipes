SsoCtrl.$inject = ['$rootScope', '$scope', '$timeout', '$log', '$location', '$state', '$stateParams', 'restApi'];

export default function SsoCtrl($rootScope, $scope, $timeout, $log, $location, $state, $stateParams, restApi) {
	//console.log($stateParams.target);
	//$http.get("/semantic-epa-backend/api/v2/admin/authc").success(function(data){
	restApi.getAuthc().success(function(data){
		console.log(data);
		if (!data.success) window.top.location.href = "http://localhost:8080/semantic-epa-backend/#/streampipes/login/" +$stateParams.target;
		else $state.go("ssosuccess");
	})

};
