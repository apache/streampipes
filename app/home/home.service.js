export class HomeService {
    constructor($window, $location, $state) {
        this.$location = $location;
        this.$state = $state;
        this.$window = $window;
    }

    openLink(l) {
        if (l.link.newWindow) {
            this.$window.open(l.link.value);
        } else {
            this.$state.go(l.link.value);
        }

    };

    getServiceLinks() {
        return [
            {
                name: "Pipeline Editor",
                description: "The editor can be used to model new pipelines",
                imageUrl: "/img/home/editor.png",
                link: {
                    newWindow: false,
                    value: "streampipes.editor"
                }
            },
            {
                name: "Pipelines",
                description: "Pipelines",
                imageUrl: "/img/home/pipelines.png",
                link: {
                    newWindow: false,
                    value: "streampipes.pipelines"
                }
            },
            {
                name: "Live Dashboard",
                description: "The live dashboard visualizes data in real-time",
                imageUrl: "/img/home/dashboard.png",
                link: {
                    newWindow: false,
                    value: "streampipes.dashboard"
                }
            },
            {
                name: "File Download",
                description: "File Download",
                imageUrl: "/img/home/appfiledownload.png",
                link: {
                    newWindow: false,
                    value: "streampipes.appfiledownload"
                }
            },
            {
                name: "Knowledge Management",
                description: "Knowledge Management",
                imageUrl: "/img/home/ontology.png",
                link: {
                    newWindow: false,
                    value: "streampipes.ontology"
                }
            },
            {
                name: "Pipeline Element Generator",
                description: "Pipeline Element Generator",
                imageUrl: "/img/home/sensors.png",
                link: {
                    newWindow: false,
                    value: "streampipes.sensors"
                }
            },
            {
                name: "Install Pipeline Elements",
                description: "The marketplace can be used to extend StreamPipes with new algorithms and data sinks",
                imageUrl: "/img/home/add.png",
                link: {
                    newWindow: false,
                    value: "streampipes.add"
                }
            },
            {
                name: "My Elements",
                description: "My Elements",
                imageUrl: "/img/home/myelements.png",
                link: {
                    newWindow: false,
                    value: "streampipes.myelements"
                }
            },
            {
                name: "Configuration",
                description: "Configuration",
                imageUrl: "/img/home/configuration.png",
                link: {
                    newWindow: false,
                    value: "streampipes.configuration"
                }
            },
            {
                name: "Notifications",
                description: "Integrated notification manager",
                imageUrl: "img/home/notifications.png",
                link: {
                    newWindow: false,
                    value: "streampipes.notifications"
                }
            },
            {
                name: "ElasticSearch",
                description: "Offline visual analytics of data",
                imageUrl: "img/home/elastic.png",
                link: {
                    newWindow: true,
                    value: this.$location.protocol() + "://" + this.$location.host() + ":5601"
                }
            }

        ]
    }
}

HomeService.$inject = ['$window', '$location', '$state'];
