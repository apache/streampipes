declare const require: any;

export let spTableWidgetConfig = {
    template: require('./tableConfig.html'),
    bindings: {
        wid: '='
    },
    controller: class TableConfigCtrl {

        wid: any;

        constructor() {}

        selectAll() {
            this.wid.schema.eventProperties.forEach(ep => {
                ep.isSelected = true;
            });
        }

        deselectAll() {
            this.wid.schema.eventProperties.forEach(ep => {
                ep.isSelected = false;
            });
        }
    },
    controllerAs: 'ctrl'
};
