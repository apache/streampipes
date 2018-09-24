import * as angular from 'angular';

export class AddWidgetCtrl {

    page: any;
    $mdDialog: any;
    ElementIconText: any;
    $http: any;
    rerenderDashboard: any;
    dashboard: any;
    layoutId: any;
    WidgetInstaces: any;
    pages: any;
    selectedVisualisation: any;
    possibleVisualisationTypes: any;
    selectedVisualisationType: any;
    visualizablePipelines: any;
    selectedType: any;
    ShepherdService: any;

    constructor($mdDialog, WidgetTemplates, WidgetInstances, ElementIconText, $http, rerenderDashboard, dashboard, layoutId, ShepherdService) {
        this.page = 'select-viz';
        this.$mdDialog = $mdDialog;
        this.ElementIconText = ElementIconText;
        this.$http = $http;
        this.rerenderDashboard = rerenderDashboard;
        this.dashboard = dashboard;
        this.layoutId = layoutId;
        this.ShepherdService = ShepherdService;

        this.WidgetInstaces = WidgetInstances;

        this.pages = [{
            type: "select-viz",
            title: "Data Stream",
            description: "Select a data stream you'd like to visualize"
        }, {
            type: "select-type",
            title: "Visualization Type",
            description: "Select a visualization type"
        }, {
            type: "select-scheme",
            title: "Visualization Settings",
            description: "Customize your visualization"
        }];

        // this.visualizablePipelines = angular.copy(visualizablePipelines);

        // This is the object that the user manipulates
        this.selectedVisualisation = {};

        this.possibleVisualisationTypes = WidgetTemplates.getAllNames();
        this.selectedVisualisationType = '';

        this.visualizablePipelines = [];

        this.$http.get('/visualizablepipeline/_all_docs?include_docs=true')
            .success(data => {
                var tempVisPipelines = data.rows;

                // get the names for each pipeline
                angular.forEach(tempVisPipelines, vis => {
                    this.$http.get('/pipeline/' + vis.doc.pipelineId)
                        .success(pipeline => {
                            vis.doc.name = pipeline.name;
                            this.visualizablePipelines.push(vis);
                        });
                });
                this.ShepherdService.trigger("add-viz");
            });


    }


    iconText(elementName) {
        return this.ElementIconText.getElementIconText(elementName);
    }

    selectPipeline(vis) {
        this.selectedVisualisation = vis;
        this.next();
        this.ShepherdService.trigger("select-pipeline");

    }

    selectVisType(type) {
        this.selectedType = type;
        this.next();
        this.ShepherdService.trigger("select-viz");

    }

    getSelectedPipelineCss(vis) {
        return this.getSelectedCss(this.selectedVisualisation, vis);
    }

    getSelectedVisTypeCss(type) {
        return this.getSelectedCss(this.selectedType, type);
    }

    getSelectedCss(selected, current) {
        if (selected == current) {
            return "wizard-preview wizard-preview-selected";
        } else {
            return "wizard-preview";
        }
    }

    getTabCss(page) {
        if (page == this.page) return "md-fab md-accent";
        else return "md-fab md-accent wizard-inactive";
    }

    back() {
        if (this.page == 'select-type') {
            this.page = 'select-viz';
        } else if (this.page == 'select-scheme') {
            this.page = 'select-type';
        }
    }

    next() {
        if (this.page == 'select-viz') {
            this.page = 'select-type';
        } else if (this.page == 'select-type') {
            this.page = 'select-scheme';

            // var directiveName = 'sp-' + this.selectedType + '-widget-config'
            // var widgetConfig = this.$compile( '<'+ directiveName + ' wid=selectedVisualisation></' + directiveName + '>')( this );
            //
            // var schemaSelection = angular.element( document.querySelector( '#scheme-selection' ) );
            // schemaSelection.append( widgetConfig );


        } else {

            var widget = {};
            widget['visualisationType'] = this.selectedType;
            widget['visualisation'] = this.selectedVisualisation;
            widget['layoutId'] = this.layoutId;


            widget['visualisationId'] = this.selectedVisualisation._id;
            this.WidgetInstaces.add(widget);
            //this.rerenderDashboard(this.dashboard);
            this.dashboard.addWidget(widget);
            this.$mdDialog.cancel();
            this.ShepherdService.trigger("save-viz");

        }
    }

    cancel() {
        this.$mdDialog.cancel();
    };
}

AddWidgetCtrl.$inject = ['$mdDialog', 'WidgetTemplates', 'WidgetInstances', 'ElementIconText', '$http', 'rerenderDashboard', 'dashboard', 'layoutId', 'ShepherdService'];
