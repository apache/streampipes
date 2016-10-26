AppCtrl.$inject = ['$rootScope', '$scope', '$mdSidenav', '$mdUtil', 'restApi', '$state'];

export default function AppCtrl($rootScope, $scope, $mdSidenav, $mdUtil, restApi, $state) {

	$rootScope.unreadNotifications = [];
	$rootScope.title = "StreamPipes";

	$scope.toggleLeft = buildToggler('left');
	$rootScope.userInfo = {
		Name : "D",
		Avatar : null
	};

	$rootScope.go = function ( path ) {
		$state.go(path);
		$mdSidenav('left').close();
	};

	$scope.logout = function() {
		restApi.logout().then(function() {
			$scope.user = undefined;
			$rootScope.authenticated = false;
			$state.go("streampipes.login");
		});
	};	      

	$scope.menu = [
		{
			link : 'streampipes',
			title: 'Editor',
			icon: 'action:ic_dashboard_24px' 
		},
		{
			link : 'streampipes.pipelines',
			title: 'Pipelines',
			icon: 'av:ic_play_arrow_24px'
		},
		{
			link : 'streampipes.dashboard',
			title: 'Dashboard',
			icon: 'editor:ic_insert_chart_24px'
		}
		//           },
		//           {
		//               link : 'streampipes.marketplace',
		//               title: 'Marketplace',
		//               icon: 'maps:ic_local_mall_24px'
		//           }
	];
	$scope.admin = [
		{
			link : 'streampipes.myelements',
			title: 'My Elements',
			icon: 'image:ic_portrait_24px'
		},
		{
			link : 'streampipes.add',
			title: 'Install Pipeline Elements',
			icon: 'content:ic_add_24px'
		},
		{
			link : 'streampipes.sensors',
			title: 'Pipeline Element Generator',
			icon: 'social:ic_share_24px'
		},
		{
			link : 'streampipes.ontology',
			title: 'Knowledge Management',
			icon: 'social:ic_share_24px'
		},
		{
			link : 'streampipes.applinks',
			title: 'Application Links',
			icon: 'action:ic_open_in_new_24px'
		},
		{
			link : 'streampipes.settings',
			title: 'Settings',
			icon: 'action:ic_settings_24px'
		}
	];

	function buildToggler(navID) {
		var debounceFn =  $mdUtil.debounce(function(){
			$mdSidenav(navID)
				.toggle();
		},300);
		return debounceFn;
	}
};

