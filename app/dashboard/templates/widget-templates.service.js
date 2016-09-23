WidgetTemplates.$inject = ['TableDataModel', 'NumberDataModel', 'LineDataModel', 'VerticalbarDataModel', 'GaugeDataModel'];

export default function WidgetTemplates(TableDataModel, NumberDataModel, LineDataModel, VerticalbarDataModel, GaugeDataModel) {
    //Register the new widgets here
    var widgetTypes = {
        table: {
            name: 'table',
            directive: 'sp-table-widget',
            dataModel: TableDataModel,
        },
        number: {
            name: 'number',
            directive: 'sp-number-widget',
            dataModel: NumberDataModel,
        },
        line: {
            name: 'line',
            directive: 'sp-line-widget',
            dataModel: LineDataModel,
        },
        verticalbar: {
            name: 'verticalbar',
            directive: 'sp-verticalbar-widget',
            dataModel: VerticalbarDataModel,
        },
        gauge: {
            name: 'gauge',
            directive: 'sp-gauge-widget',
            dataModel: GaugeDataModel,
        }

    }

    var getDataModel = function (name) {
        return widgetTypes[name].dataModel;
    }

    var getDirectiveName = function (name) {
        return widgetTypes[name].directive;
    }

    var getAllNames = function () {
        var result = [];
        angular.forEach(widgetTypes, function (w) {
            result.push(w.name);
        });

        return result;
    }

    return {
        getDataModel: getDataModel,
        getDirectiveName: getDirectiveName,
        getAllNames: getAllNames
    }
};


