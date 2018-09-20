declare const require: any;

export let spTableWidgetConfig = {
    template: require('./tableConfig.html'),
    bindings: {
        wid: '='
    },
    controller: class TableConfigCtrl {

        wid: any;
        ShepherdService: any;

        constructor(ShepherdService) {
            this.ShepherdService = ShepherdService;
        }

        selectAll() {
            this.wid.schema.eventProperties.forEach(ep => {
                ep.isSelected = true;
            });
            this.ShepherdService.trigger("customize-viz")

        }

        deselectAll() {
            this.wid.schema.eventProperties.forEach(ep => {
                ep.isSelected = false;
            });
        }
    },
    controllerAs: 'ctrl'
};
