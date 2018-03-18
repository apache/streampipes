import * as angular from 'angular';
import * as _ from 'lodash';

export class WidgetInstances {

    $http: any;
    WidgetTemplates: any;

    constructor($http, WidgetTemplates) {
        this.$http = $http;
        this.WidgetTemplates = WidgetTemplates;
    }

    getId() {
        return Math.floor((1 + Math.random()) * 0x10000);
    }

    getWidgets() {
        return this.$http.get('/dashboard/_all_docs?include_docs=true').then(data => {
            var result = [];
            angular.forEach(data.data.rows, d => {
                result.push(d.doc);
            });

            return result;
        });
    }

    add(widget) {
        var id = this.getId();
        widget.id = id;
        this.$http.post('/dashboard', widget).then(() => {
        }, err => {
            console.log(err);
        });
    }

    remove(widget) {
        return this.$http.delete('/dashboard/'+ widget._id + '?rev=' + widget._rev);
    }

    get(id) {
        return this.getWidgets().then(data => {
            var result = _.filter(data, d => {
                return d.id == id;
            });

            return result[0];
        });
    }


    getWidgetDashboardDefinition(widget) {
        var name = widget.visualisation.name + '[' + widget.visualisationType + ']';
        var directive = this.WidgetTemplates.getDirectiveName(widget.visualisationType);
        var dataModel = this.WidgetTemplates.getDataModel(widget.visualisationType);

        return {
            name: name,
            directive: directive,
            title: widget.id,
            dataAttrName: 'data',
            dataModelType: dataModel,
            dataModelArgs: widget.visualisationId,
            attrs: {
                'widget-id': widget.id
            },
            style: {
                width: '30%'
            },
            layoutId: widget.layoutId
        }
    }

    getAllWidgetDefinitions() {
        var result = [];

        return this.getWidgets().then(data => {
            angular.forEach(data, (w, key) => {
                result.push(this.getWidgetDashboardDefinition(w));
            });

            return result;
        });
    }

};

WidgetInstances.$inject = ['$http', 'WidgetTemplates'];

