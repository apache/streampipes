declare const require: any;

export let spMapWidgetConfig = {
    template: require('./mapConfig.html'),
    bindings: {
        wid: '='
    },
    controller: class MapConfigCtrl {
        markerTypes: string[];

        constructor() {
            this.markerTypes = ['Default', 'Car'];
        }


    },
    controllerAs: 'ctrl'
};