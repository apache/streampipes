/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {
    Component, Input,
    OnInit,
} from "@angular/core";
import {JsplumbBridge} from "../../services/jsplumb-bridge.service";
import {PipelinePositioningService} from "../../services/pipeline-positioning.service";
import {EditorDialogManager} from "../../services/editor-dialog-manager.service";
import {PipelineValidationService} from "../../services/pipeline-validation.service";
import {JsplumbService} from "../../services/jsplumb.service";
import {RestApi} from "../../../services/rest-api.service";
import {TransitionService} from "../../../services/transition.service";
import {ShepherdService} from "../../../services/tour/shepherd.service";


@Component({
    selector: 'pipeline-assembly',
    templateUrl: './pipeline-assembly.component.html',
    styleUrls: ['./pipeline-assembly.component.css']
})
export class PipelineAssemblyComponent implements OnInit {

    PipelineEditorService: any;
    ObjectProvider: any;
    DialogBuilder: any;
    currentMouseOverElement: any;
    currentZoomLevel: any;
    preview: any;

    @Input()
    rawPipelineModel: any;
    selectMode: any;
    currentPipelineName: any;
    currentPipelineDescription: any;

    @Input()
    currentModifiedPipelineId: any;

    @Input()
    allElements: any;

    errorMessagesDisplayed: any = false;

    pipelineValid: boolean = false;

    pipelineCacheRunning: boolean = false;
    pipelineCached: boolean = false;

    // Remove later
    EditorDialogManager: any;
    TransitionService: any;

    constructor(private JsplumbBridge: JsplumbBridge,
                private PipelinePositioningService: PipelinePositioningService,
                //private EditorDialogManager: EditorDialogManager,
                public PipelineValidationService: PipelineValidationService,
                private RestApi: RestApi,
                private JsplumbService: JsplumbService,
                //private TransitionService: TransitionService,
                private ShepherdService: ShepherdService) {

        this.selectMode = true;
        this.currentZoomLevel = 1;


    }

    ngOnInit(): void {
        if (this.currentModifiedPipelineId) {
            //this.displayPipelineById();
        } else {
            //this.checkAndDisplayCachedPipeline();
        }

        // this.$rootScope.$on("pipeline.validate", () => {
        //     this.pipelineValid = this.PipelineValidationService.isValidPipeline(this.rawPipelineModel);
        // });
    }

    ngAfterViewInit() {
        ($("#assembly") as any).panzoom({
            disablePan: true,
            increment: 0.25,
            minScale: 0.5,
            maxScale: 1.5,
            contain: 'invert'
        });

        $("#assembly").on('panzoomzoom', (e, panzoom, scale) => {
            this.currentZoomLevel = scale;
            this.JsplumbBridge.setZoom(scale);
            this.JsplumbBridge.repaintEverything();
        });
    }

    autoLayout() {
        this.PipelinePositioningService.layoutGraph("#assembly", "span[id^='jsplumb']", 110, false);
        this.JsplumbBridge.repaintEverything();
    }

    toggleSelectMode() {
        if (this.selectMode) {
            ($("#assembly") as any).panzoom("option", "disablePan", false);
            ($("#assembly") as any).selectable("disable");
            this.selectMode = false;
        }
        else {
            ($("#assembly") as any).panzoom("option", "disablePan", true);
            ($("#assembly") as any).selectable("enable");
            this.selectMode = true;
        }
    }

    zoomOut() {
        this.doZoom(true);
    }

    zoomIn() {
        this.doZoom(false);
    }

    doZoom(zoomOut) {
        ($("#assembly") as any).panzoom("zoom", zoomOut);
    }

    showClearAssemblyConfirmDialog(ev) {
        this.EditorDialogManager.showClearAssemblyDialog(ev).then(() => {
            if (this.currentModifiedPipelineId) {
                this.currentModifiedPipelineId = undefined;
            }
            this.clearAssembly();
            this.TransitionService.makePipelineAssemblyEmpty(true);

        }, function () {
        });
    };

    /**
     * clears the Assembly of all elements
     */
    clearAssembly() {
        //$('#assembly').children().not('#clear, #submit').remove();
        this.JsplumbBridge.deleteEveryEndpoint();
        this.rawPipelineModel = [];
        ($("#assembly") as any).panzoom("reset", {
            disablePan: true,
            increment: 0.25,
            minScale: 0.5,
            maxScale: 1.5,
            contain: 'invert'
        });
        this.currentZoomLevel = 1;
        this.JsplumbBridge.setZoom(this.currentZoomLevel);
        this.JsplumbBridge.repaintEverything();
        this.RestApi.removePipelineFromCache().then(msg => {
            this.pipelineCached = false;
            this.pipelineCacheRunning = false;
        });
    };

    /**
     * Sends the pipeline to the server
     */
    submit() {
        var pipeline = this.ObjectProvider.makeFinalPipeline(this.rawPipelineModel);

        pipeline.name = this.currentPipelineName;
        pipeline.description = this.currentPipelineDescription;
        if (this.currentModifiedPipelineId) {
            pipeline._id = this.currentModifiedPipelineId;
        }

        this.openPipelineNameModal(pipeline, (!!this.currentModifiedPipelineId));
    }


    openPipelineNameModal(pipeline, modificationMode) {
        this.EditorDialogManager.showSavePipelineDialog(pipeline, modificationMode);
    }

    checkAndDisplayCachedPipeline() {
        this.RestApi.getCachedPipeline().then(msg => {
            if (msg.data !== "") {
                this.rawPipelineModel = msg.data;
                this.displayPipelineInEditor(true);
            }
        });
    }

    displayPipelineById() {
        this.RestApi.getPipelineById(this.currentModifiedPipelineId)
            .then((msg) => {
                let pipeline = msg.data;
                this.currentPipelineName = pipeline.name;
                this.currentPipelineDescription = pipeline.description;
                this.rawPipelineModel = this.JsplumbService.makeRawPipeline(pipeline, false);
                this.displayPipelineInEditor(true);
            });
    };

    displayPipelineInEditor(autoLayout) {
        this.PipelinePositioningService.displayPipeline(this.rawPipelineModel, "#assembly", false, autoLayout);
        this.TransitionService.makePipelineAssemblyEmpty(false);
        this.pipelineValid = this.PipelineValidationService.isValidPipeline(this.rawPipelineModel);
    }

    toggleErrorMessagesDisplayed() {
        this.errorMessagesDisplayed = !(this.errorMessagesDisplayed);
    }

    isPipelineAssemblyEmpty() {
        return this.rawPipelineModel.length === 0 || this.rawPipelineModel.every(pe => pe.settings.disabled);
    }

    elementDropped($event) {
        console.log($event);
    }

}