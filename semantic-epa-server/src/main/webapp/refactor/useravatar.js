import angular from 'npm/angular';

var appModule = angular.module('useravatar', []);
appModule.directive('useravatar', ["avatarService", function (avatarService) {
	var controller = function ($scope) {
		$scope.ImageAvailable = false;
		if (!$scope.User.Avatar) {
			$scope.GenericAvatar = avatarService.getAvatar($scope.User);
		} else {
			$scope.ImageAvailable = true;
		}
	};
	return {
		restrict: 'C',
		scope: {
			User: '=user'
		},
		template: '<div class="generic-avatar">'+
		'<a class="thumb spacer animated fadeIn color" style="background-color:{{GenericAvatar.Background}}"></a>'+
		'<a class="name"  style="background: rgb(63,81,181); color:white; padding: 5px; margin-bottom: 20px;">{{GenericAvatar.Initials}}</a>' +
		'<a class="img" data-ng-if="ImageAvailable" style="background-image:url({{User.Avatar}})"></a>' +
		'</div>',
		controller: controller
	};
}])
.factory("avatarService", function(){
    var avatarService = function(user){
      var colorCodes = {
				1: "#F29691",
				2: "#92D6C2",
				3: "#CFD696",
				4: "#FACA82",
				5: "#D7ADE0"
			};
			var i1 = "", i2 = "", nameArray = [];
			
			if (angular.isDefined(user.Name)) {
				i1 = angular.uppercase(user.Name.charAt(0));
				nameArray = user.Name.split(" ");
				if (nameArray.length > 2) {
					i2 = angular.uppercase(nameArray[nameArray.length - 1].charAt(0));
				} else {
					i2 = "";//angular.uppercase(user.Name.charAt(0));
				}
			} else {
				i1 = angular.uppercase(user.FirstName.charAt(0));
				nameArray = user.LastName.split(" ");
				if (nameArray.length > 2) {
					i2 = nameArray[nameArray.length - 1].charAt(0);
				} else {
					i2 = angular.uppercase(nameArray[0].charAt(0));
				}
			}
			var initials = i1 + i2;
			var charCode = initials.charCodeAt(0) + initials.charCodeAt(1);
			charCode = charCode >= 130 && charCode <= 144 ? 1 : charCode >= 145 && charCode <= 158 ? 2 : charCode > 158 && charCode <= 172 ? 3 : charCode >= 173 && charCode <= 186 ? 4 : 5;
			var background = colorCodes[charCode];
			return ({ "Initials": initials, "Background": background });
    }
    return {
      getAvatar: avatarService
    }
});
