import {Component, Inject} from "@angular/core";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {RestApi} from "../../../services/rest-api.service";
import {RestService} from "../../services/rest.service";
import {ElementIconText} from "../../../services/get-element-icon-text.service";
import {SelectedVisualizationData} from "../../model/selected-visualization-data.model";

@Component({
    selector: 'add-pipeline-dialog-component',
    templateUrl: './add-pipeline-dialog.component.html',
    styleUrls: ['./add-pipeline-dialog.component.css']
})
export class AddPipelineDialogComponent {

    pages = [{
        type: "select-pipeline",
        title: "Select Pipeline",
        description: "Select a pipeline you'd like to visualize"
    }, {
        type: "select-measurement",
        title: "Measurement Value",
        description: "Select measurement"
    }, {
        type: "select-label",
        title: "Label",
        description: "Choose label"
    }];

    visualizablePipelines = [];

    selectedVisualization: any;
    selectedType: any;
    selectedMeasurement: any;
    page: any = "select-pipeline";

    selectedLabelBackgroundColor: string = "#FFFFFF";
    selectedLabelTextColor: string = "#000000";
    selectedMeasurementBackgroundColor: string = "#FFFFFF";
    selectedMeasurementTextColor: string = "#000000";
    selectedLabel: string;


    constructor(
        public dialogRef: MatDialogRef<AddPipelineDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: SelectedVisualizationData,
        private restApi: RestApi,
        private restService: RestService,
        public elementIconText: ElementIconText) {
    }

    ngOnInit() {
        this.restService.getVisualizablePipelines().subscribe(visualizations => {
            visualizations.rows.forEach(vis => {
                console.log(vis);
                this.restService.getPipeline(vis.doc.pipelineId)
                    .subscribe(pipeline => {
                        vis.doc.name = pipeline.name;
                        this.visualizablePipelines.push(vis);
                    });
            });
        });
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    getSelectedPipelineCss(vis) {
        return this.getSelectedCss(this.selectedVisualization, vis);
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
        console.log(page);
        if (page == this.page) return "md-fab md-accent";
        else return "md-fab md-accent wizard-inactive";
    }

    selectPipeline(vis) {
        this.selectedVisualization = vis;
        console.log(vis);
        this.next();

    }

    next() {
        if (this.page == 'select-pipeline') {
            this.page = 'select-measurement';
        } else if (this.page == 'select-measurement') {
            this.page = 'select-label';
        } else {

            let selectedConfig:SelectedVisualizationData = {} as SelectedVisualizationData;
            selectedConfig.labelBackgroundColor = this.selectedLabelBackgroundColor;
            selectedConfig.labelTextColor = this.selectedLabelTextColor;
            selectedConfig.measurementBackgroundColor = this.selectedMeasurementBackgroundColor;
            selectedConfig.measurementTextColor = this.selectedMeasurementTextColor;
            selectedConfig.measurement = this.selectedMeasurement;
            selectedConfig.visualizationId = this.selectedVisualization.pipelineId;
            selectedConfig.label = this.selectedLabel;
            selectedConfig.brokerUrl = this.selectedVisualization.broker;
            selectedConfig.topic = this.selectedVisualization.pipelineId;

            this.dialogRef.close(selectedConfig);
        }
    }

    back() {
        if (this.page == 'select-measurement') {
            this.page = 'select-pipeline';
        } else if (this.page == 'select-label') {
            this.page = 'select-measurement';
        }
    }

    iconText(s) {
        return this.elementIconText.getElementIconText(s);
    }

}