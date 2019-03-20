import {AddWidgetCtrl} from './add-widget.controller';
import * as angular from 'angular';
import * as _ from 'lodash';
import {MissingElementsForTutorialDialogController} from "../editor/dialog/missing-elements-for-tutorial/missing-elements-for-tutorial-dialog.controller";
import {NoPipelinePresentDialogController} from "./dialog/no-pipeline-present-dialog.controller";

declare const require: any;

export class DashboardCtrl {

    $http: any;
    $mdDialog: any;
    WidgetInstances: any;
    $scope: any;
    layoutOptions: any;
    rerender: any;
    ShepherdService: any;
    maximized: any = false;
    pipelinePresent: any = false;
    $templateCache: any;

    constructor($http, $mdDialog, WidgetInstances, $scope, $templateCache, ShepherdService) {
        this.$http = $http;
        this.$mdDialog = $mdDialog;
        this.WidgetInstances = WidgetInstances;
        this.$scope = $scope;
        this.ShepherdService = ShepherdService;
        this.$templateCache = $templateCache;
    }

    $onInit() {
        this.$templateCache.put('dashboard-frame.html', require('./dashboard-frame.html'));
        this.$templateCache.put('dashboard-layout-frame.html', require('./dashboard-layout-frame.html'));

        // this.visualizablePipelines = [];
        this.layoutOptions = {
            widgetDefinitions: [],
            widgetButtons: false,
        };

        this.rerender = true;
        this.isPipelinePresent();
        this.rerenderDashboard(this);
    }

    addSpWidget(layout) {
        this.$mdDialog.show({
            controller: AddWidgetCtrl,
            controllerAs: 'ctrl',
            template: require('./add-widget-template.html'),
            parent: angular.element(document.body),
            clickOutsideToClose: false,
            bindToController: true,
            locals: {
                // visualizablePipelines: this.visualizablePipelines,
                rerenderDashboard: this.rerenderDashboard,
                dashboard: this,
                layoutId: layout.id
            }
        });
    };

    removeSpWidget(widget) {
        var self = this;
        this.WidgetInstances.get(widget.attrs['widget-id']).then(w => {
            this.WidgetInstances.remove(w).then(res => {
                this.layoutOptions.removeWidget(widget);
            });
        });
    };

    maximizeSpWidget(widget) {
        var widthUnits = widget.widthUnits;
        widget.maximized = true;
        widget.setWidth("100", widthUnits);
    }

    minimizeSpWidget(widget) {
        var widthUnits = widget.widthUnits;
        widget.maximized = false;
        widget.setWidth("30", widthUnits);
    }


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
            {
                title: 'Layout 1',
                id: 'Layout 1',
                active: true,
                defaultWidgets: this.getLayoutWidgets('Layout 1', widgets)
            },
            // { title: 'Layout 2', id: 'Layout 2', active: false, defaultWidgets: this.getLayoutWidgets('Layout 2', widgets)},
        ];

        return result;

    }

    getOptions() {
        return this.WidgetInstances.getAllWidgetDefinitions().then(widgets => {

            this.getLayouts(widgets);
            return {
                widgetDefinitions: widgets,
                widgetButtons: false,
                defaultLayouts: this.getLayouts(widgets)
            }
        });
    }

    startDashboardTour() {
        if (this.pipelinePresent) {
            this.ShepherdService.startDashboardTour();
        } else {
            this.$mdDialog.show({
                controller: NoPipelinePresentDialogController,
                controllerAs: 'ctrl',
                template: require('./dialog/no-pipeline-present-dialog.tmpl.html'),
                parent: angular.element(document.body),
                clickOutsideToClose: false,
                bindToController: true
            })
        }
    }

    isPipelinePresent() {
        this.$http.get('/visualizablepipeline/_all_docs?include_docs=true')
            .then(msg => {
                var data = msg.data;
                this.pipelinePresent = (data.rows.length > 0);
            });
    }

    addWidget(widget) {
        setTimeout(() => {
            this.layoutOptions.widgetDefinitions.push(this.WidgetInstances.getWidgetDashboardDefinition(widget));
            this.layoutOptions.addWidget(this.WidgetInstances.getWidgetDashboardDefinition(widget));
        }, 100);
    }
}

DashboardCtrl.$inject = ['$http', '$mdDialog', 'WidgetInstances', '$scope', '$templateCache', 'ShepherdService'];
