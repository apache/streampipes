import { AddWidgetCtrl } from './add-widget.controller';

export class DashboardCtrl {


    constructor($http, $mdDialog, WidgetInstances, $scope) {
        this.$http = $http;
        this.$mdDialog = $mdDialog;
        this.WidgetInstances = WidgetInstances;
        this.$scope = $scope;

        // this.visualizablePipelines = [];
        this.layoutOptions = {
            widgetDefinitions: [],
            widgetButtons: false,
        };

        this.rerender = true;

        // this.$http.get('/visualizablepipeline/_all_docs?include_docs=true')
        //     .success(data => {
        //         var tempVisPipelines = data.rows;
        //
        //         // get the names for each pipeline
        //         angular.forEach(tempVisPipelines, vis => {
        //             this.$http.get('/pipeline/' + vis.doc.pipelineId)
        //                 .success(pipeline => {
        //                     vis.doc.name = pipeline.name;
        //                     this.visualizablePipelines.push(vis);
        //                 });
        //         });
        //     });


        this.rerenderDashboard(this);
    }

    addSpWidget(layout) {
        this.$mdDialog.show({
            controller: AddWidgetCtrl,
            controllerAs: 'ctrl',
            templateUrl: 'app/dashboard/add-widget-template.html',
            parent: angular.element(document.body),
            clickOutsideToClose:false,
            bindToController: true,
            locals : {
                // visualizablePipelines: this.visualizablePipelines,
                rerenderDashboard: this.rerenderDashboard,
                dashboard: this,
                layoutId: layout.id
            }
        });
    };

    removeSpWidget(widget) {
        this.WidgetInstances.get(widget.attrs['widget-id']).then(w =>  {
            this.WidgetInstances.remove(w).then(res => {
                this.rerenderDashboard(this);
            });
        });
    };


    // TODO Helper to add new Widgets to the dashboard
    // Find a better solution
    rerenderDashboard(dashboard) {
        dashboard.rerender = false;
        setTimeout(() => {
            dashboard.$scope.$apply(() => {
                dashboard.getOptions().then(options => {
                    dashboard.layoutOptions = options;
                    dashboard.rerender = true;
                });
            });
        }, 100);
    }

    getLayoutWidgets(layoutId, widgets) {
        return _.filter(widgets, w => {
            return w.layoutId == layoutId;
        });
    }

    //TODO Add support here to add more Layouts
    getLayouts(widgets) {
        var result = [
            { title: 'Layout 1', id: 'Layout 1', active: true , defaultWidgets: this.getLayoutWidgets('Layout 1', widgets)},
            { title: 'Layout 2', id: 'Layout 2', active: false, defaultWidgets: this.getLayoutWidgets('Layout 2', widgets)},
        ];

        return result;

    }

    getOptions() {
        return this.WidgetInstances.getAllWidgetDefinitions().then(widgets => {

            this.getLayouts(widgets);
            return 	{
                widgetDefinitions: widgets,
                widgetButtons: false,
                defaultLayouts: this.getLayouts(widgets)
            }
        });
    }
}

DashboardCtrl.$inject = ['$http', '$mdDialog', 'WidgetInstances', '$scope'];
