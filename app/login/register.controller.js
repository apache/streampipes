RegisterCtrl.$inject = ['$scope', 'RestApi'];

export default function RegisterCtrl($scope, RestApi) {

		$scope.loading = false;
		$scope.registrationFailed = false;
		$scope.registrationSuccess = false;
		$scope.errorMessage = "";


		$scope.roles = [{"name" : "System Administrator", "internalName" : "SYSTEM_ADMINISTRATOR"},
			{"name" : "Manager", "internalName" : "MANAGER"},
			{"name" : "Operator", "internalName" : "OPERATOR"},
			{"name" : "Business Analyst", "internalName" : "BUSINESS_ANALYST"},
			{"name" : "Demo User", "internalName" : "USER_DEMO"}];

		$scope.selectedRole = $scope.roles[0].internalName;

		$scope.register = function() {
			var payload = {};
			payload.password = $scope.password;
			payload.email = $scope.email;
			payload.role = $scope.selectedRole;
			$scope.loading = true;
			$scope.registrationFailed = false;
			$scope.registrationSuccess = false;
			$scope.errorMessage = "";

			RestApi.register(payload)
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
