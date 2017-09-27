HomeCtrl.$inject = ['$scope', '$window'];

export default function HomeCtrl($scope, $window) {


    $scope.openLink = function(l) {
        if (l.link.newWindow) {
            $window.open(l.link.value);
        } else {
            $scope.$parent.go(l.link.value);
        }

    };

    $scope.serviceLinks = [
        {
            name: "Pipeline Editor",
            description: "The editor can be used to model new pipelines",
            imageUrl: "img/home/editor.png",
            link: {
                newWindow: false,
                value: "streampipes.editor"
            }
        },
        {
            name: "Live Dashboard",
            description: "The live dashboard visualizes Testfeld data in real-time",
            imageUrl: "img/home/dashboard.png",
            link: {
                newWindow: false,
                value: "streampipes.dashboard"
            }
        },
        {
            name: "Marketplace",
            description: "The marketplace can be used to extend StreamPipes with new algorithms and data sinks",
            imageUrl: "img/home/marketplace.png",
            link: {
                newWindow: false,
                value: "streampipes.add"
            }
        },
        {
            name: "Notifications",
            description: "Integrated notification manager",
            imageUrl: "img/notification_icon.png",
            link: {
                newWindow: false,
                value: "streampipes.notifications"
            }
        },
        {
            name: "ElasticSearch",
            description: "Offline visual analytics of Testfeld data",
            imageUrl: "img/home/elastic.png",
            link: {
                newWindow: true,
                value: "http://ipe-koi05.fzi.de:5601"
            }
        },

    ]

};
