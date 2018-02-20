import { Injectable } from '@angular/core';

@Injectable()
export class HomeService {

    constructor() {}

    getServiceLinks() {
        return [
            {
                name: 'Pipeline Editor',
                description: 'The editor can be used to model new pipelines',
                imageUrl: 'assets/img/home/editor.png',
                link: {
                    newWindow: false,
                    value: 'streampipes.editor'
                }
            },
            {
                name: 'Pipelines',
                description: 'Pipelines',
                imageUrl: 'assets/img/home/pipelines.png',
                link: {
                    newWindow: false,
                    value: 'streampipes.pipelines'
                }
            },
            {
                name: 'Live Dashboard',
                description: 'The live dashboard visualizes data in real-time',
                imageUrl: 'assets/img/home/dashboard.png',
                link: {
                    newWindow: false,
                    value: 'streampipes.dashboard'
                }
            },
            {
                name: 'File Download',
                description: 'File Download',
                imageUrl: 'assets/img/home/appfiledownload.png',
                link: {
                    newWindow: false,
                    value: 'streampipes.appfiledownload'
                }
            },
            {
                name: 'Knowledge Management',
                description: 'Knowledge Management',
                imageUrl: 'assets/img/home/ontology.png',
                link: {
                    newWindow: false,
                    value: 'streampipes.ontology'
                }
            },
            {
                name: 'Pipeline Element Generator',
                description: 'Pipeline Element Generator',
                imageUrl: 'assets/img/home/sensors.png',
                link: {
                    newWindow: false,
                    value: 'streampipes.sensors'
                }
            },
            {
                name: 'Install Pipeline Elements',
                description: 'The marketplace can be used to extend StreamPipes with new algorithms and data sinks',
                imageUrl: 'assets/img/home/add.png',
                link: {
                    newWindow: false,
                    value: 'streampipes.add'
                }
            },
            {
                name: 'My Elements',
                description: 'My Elements',
                imageUrl: 'assets/img/home/myelements.png',
                link: {
                    newWindow: false,
                    value: 'streampipes.myelements'
                }
            },
            {
                name: 'Configuration',
                description: 'Configuration',
                imageUrl: 'assets/img/home/configuration.png',
                link: {
                    newWindow: false,
                    value: 'streampipes.configuration'
                }
            },
            {
                name: 'Notifications',
                description: 'Integrated notification manager',
                imageUrl: 'assets/img/home/notifications.png',
                link: {
                    newWindow: false,
                    value: 'streampipes.notifications'
                }
            },
            {
                name: 'ElasticSearch',
                description: 'Offline visual analytics of data',
                imageUrl: 'assets/img/home/elastic.png',
                link: {
                    newWindow: true,
                    value: window.location.protocol + '//' + window.location.hostname + ':5601'
                }
            }
        ];
    }
}