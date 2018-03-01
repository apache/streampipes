// factory('WidgetDataModel', function () {
export class WidgetDataModel {

    constructor() {}

    setup(widget, scope) {
        this.dataAttrName = widget.dataAttrName;
        this.dataModelOptions = widget.dataModelOptions;
        this.widgetScope = scope;
    }

    updateScope(data) {
        this.widgetScope.widgetData = data;
    }

    init() {
        // to be overridden by subclasses
    }

    destroy() {
        // to be overridden by subclasses
    }

}
