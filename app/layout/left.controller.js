LeftCtrl.$inject = ['$scope', '$timeout', '$mdSidenav', '$log'];
//.controller('LeftCtrl', function ($scope, $timeout, $mdSidenav, $log) {
function LeftCtrl($scope, $timeout, $mdSidenav, $log) {
	$scope.close = function () {
		$mdSidenav('left').close();
	};
};
