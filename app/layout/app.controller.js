AppCtrl.$inject = ['$rootScope', '$scope', '$mdSidenav', '$mdUtil', 'restApi', '$state', '$window'];

export default function AppCtrl($rootScope, $scope, $mdSidenav, $mdUtil, restApi, $state, $window) {

    $rootScope.unreadNotifications = [];
    $rootScope.title = "StreamPipes";

    $scope.toggleLeft = buildToggler('left');
    $rootScope.userInfo = {
        Name: "D",
        Avatar: null
    };

    $rootScope.go = function (path) {
        $state.go(path);
        $rootScope.activePage = getPageTitle(path);
        $mdSidenav('left').close();
    };

    $rootScope.go = function (path, payload) {
        $state.go(path, payload);
        $rootScope.activePage = getPageTitle(path);
    };

    $scope.logout = function () {
        restApi.logout().then(function () {
            $scope.user = undefined;
            $rootScope.authenticated = false;
            $state.go("login");
        });
    };

    $scope.openDocumentation = function(){
        $window.open('/docs', '_blank');
    };

    $scope.isActivePage = function(path) {
        return ($state.current.name == path);
    }

    var getPageTitle = function (path) {
        var allMenuItems = $scope.menu.concat($scope.admin);
        var currentTitle = "Notifications";
        angular.forEach(allMenuItems, function (m) {
            if (m.link === path) {
                currentTitle = m.title;
            }
        });
        if (path == 'streampipes.pipelineDetails') {
            currentTitle = "Pipeline Details";
        } else if (path == 'streampipes.edit') {
            currentTitle = $scope.menu[0].title;
        }
        return currentTitle;
    }

    $scope.menu = [
        {
            link: 'streampipes',
            title: 'Home',
            icon: 'action:ic_home_24px'
        },
        {
            link: 'streampipes.editor',
            title: 'Pipeline Editor',
            icon: 'action:ic_dashboard_24px'
        },
        {
            link: 'streampipes.pipelines',
            title: 'Pipelines',
            icon: 'av:ic_play_arrow_24px'
        },
        {
            link: 'streampipes.dashboard',
            title: 'Live Dashboard',
            icon: 'editor:ic_insert_chart_24px'
        },
        {
            link: 'streampipes.appfiledownload',
            title: 'File Download',
            icon: 'file:ic_file_download_24px'
        }
    ];
    $scope.admin = [
        {
            link: 'streampipes.add',
            title: 'Install Pipeline Elements',
            icon: 'file:ic_cloud_download_24px'
        },
        {
            link: 'streampipes.myelements',
            title: 'My Elements',
            icon: 'image:ic_portrait_24px'
        },
        {
            link: 'streampipes.configuration',
            title: 'Configuration',
            icon: 'action:ic_settings_24px'
        }
    ];

    $rootScope.activePage = getPageTitle($state.current.name);

    function buildToggler(navID) {
        var debounceFn = $mdUtil.debounce(function () {
            $mdSidenav(navID)
                .toggle();
        }, 300);
        return debounceFn;
    }
};

