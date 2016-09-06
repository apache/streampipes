RegisterCtrl.$inject = ['$scope', '$timeout', '$mdSidenav', '$mdUtil', '$log', '$http'];

export default function RegisterCtrl($scope, $timeout, $mdSidenav, $mdUtil, $log, $http) {

		$scope.loading = false;
		$scope.registrationFailed = false;
		$scope.registrationSuccess = false;
		$scope.errorMessage = "";


		$scope.roles = [{"name" : "System Administrator", "internalName" : "SYSTEM_ADMINISTRATOR"},
			{"name" : "Manager", "internalName" : "MANAGER"},
			{"name" : "Operator", "internalName" : "OPERATOR"},
			{"name" : "Demo User", "internalName" : "USER_DEMO"}];

		$scope.selectedRole = $scope.roles[0].internalName;

		$scope.register = function() {
			var payload = {};
			payload.username = $scope.username;
			payload.password = $scope.password;
			payload.email = $scope.email;
			payload.role = $scope.selectedRole;
			$scope.loading = true;
			$scope.registrationFailed = false;
			$scope.registrationSuccess = false;
			$scope.errorMessage = "";

			$http.post("/semantic-epa-backend/api/v2/admin/register", payload)
				.then(
					function(response) {
						$scope.loading = false;
						if (response.data.success)
				{
					$scope.registrationSuccess = true;
				}
				else
				{
					$scope.registrationFailed = true;
					$scope.errorMessage = response.data.notifications[0].title;
				}

					}, function(response) { // error

						$scope.loading = false;
						$scope.registrationFailed = true;
					}
				)
		};
	};
