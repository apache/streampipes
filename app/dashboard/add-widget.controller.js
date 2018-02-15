import angular from 'angular';

export class AddWidget {

    constructor($mdDialog, visualizablePipelines, rerenderDashboard, layoutId, WidgetInstances, $compile, WidgetTemplates, ElementIconText) {
        this.layoutId = layoutId;
        this.page = 'select-viz';
        this.rerenderDashboard = rerenderDashboard;
        this.$mdDialog = $mdDialog;
        this.$compile = $compile;
        this.ElementIconText = ElementIconText;

        this.WidgetInstaces = WidgetInstances;

        this.pages = [{
            type : "select-viz",
            title : "Data Stream",
            description : "Select a data stream you'd like to visualize"
        },{
            type : "select-type",
            title : "Visualization Type",
            description : "Select a visualization type"
        },{
            type : "select-scheme",
            title : "Visualization Settings",
            description : "Customize your visualization"
        }];

        this.visualizablePipelines = angular.copy(visualizablePipelines);

        // This is the object that the user manipulates
        this.selectedVisualisation = {};

        this.possibleVisualisationTypes = WidgetTemplates.getAllNames();
        this.selectedVisualisationType = '';


    }


    iconText(elementName) {
        return this.ElementIconText.getElementIconText(elementName);
    }

    selectPipeline(vis) {
        this.selectedVisualisation = vis;
    }

    selectVisType(type) {
        this.selectedType = type;
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


    next() {
        if (this.page == 'select-viz') {
            this.page = 'select-type';
        } else if (this.page == 'select-type') {
            this.page = 'select-scheme';

            var directiveName = 'sp-' + this.selectedType + '-widget-config'
            var widgetConfig = this.$compile( '<'+ directiveName + ' wid=selectedVisualisation></' + directiveName + '>')( this );

            var schemaSelection = angular.element( document.querySelector( '#scheme-selection' ) );
            schemaSelection.append( widgetConfig );

        } else {

            var widget = {};
            widget.visualisationType = this.selectedType;
            widget.visualisation = this.selectedVisualisation;
            widget.layoutId = this.layoutId;


            widget.visualisationId = this.selectedVisualisation._id;
            this.WidgetInstances.add(widget);
            this.rerenderDashboard();
            this.$mdDialog.cancel();

        }
    }

    cancel() {
        this.$mdDialog.cancel();
    };
}

AddWidget.$inject = ['$mdDialog', 'visualizablePipelines', 'rerenderDashboard', 'layoutId', '$compile', 'WidgetTemplates', 'ElementIconText'];

