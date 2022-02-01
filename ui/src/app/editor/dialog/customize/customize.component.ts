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
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  Input,
  OnInit,
  ViewEncapsulation
} from "@angular/core";
import {InvocablePipelineElementUnion, PipelineElementConfig} from "../../model/editor.model";
import {DialogRef} from "../../../core-ui/dialog/base-dialog/dialog-ref";
import {JsplumbService} from "../../services/jsplumb.service";
import {
  DataProcessorInvocation,
  EventSchema,
  PipelineElementTemplate, PipelineElementTemplateConfig
} from "../../../../../projects/streampipes/platform-services/src/lib/model/gen/streampipes-model";
import {FormBuilder, FormGroup} from "@angular/forms";
import {ShepherdService} from "../../../services/tour/shepherd.service";
import {ConfigurationInfo} from "../../../connect/model/ConfigurationInfo";
import {PipelineElementTemplateService} from "../../../../../projects/streampipes/platform-services/src/lib/apis/pipeline-element-template.service";

@Component({
  selector: 'customize-pipeline-element',
  templateUrl: './customize.component.html',
  styleUrls: ['./customize.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class CustomizeComponent implements OnInit, AfterViewInit {

  @Input()
  pipelineElement: PipelineElementConfig;

  @Input()
  restrictedEditMode: boolean;

  cachedPipelineElement: InvocablePipelineElementUnion;
  eventSchemas: EventSchema[] = [];

  displayRecommended: boolean = true;
  _showDocumentation: boolean = false;

  selection: any;
  matchingSelectionLeft: any;
  matchingSelectionRight: any;
  invalid: any;
  helpDialogVisible: any;
  currentStaticProperty: any;
  validationErrors: any;
  configVisible: any;

  sourceEndpoint: any;
  sepa: any;

  parentForm: FormGroup;
  formValid: boolean;
  viewInitialized: boolean = false;

  isDataProcessor: boolean = false;
  originalDialogWidth: string | number;
  completedStaticProperty: ConfigurationInfo;

  availableTemplates: Array<PipelineElementTemplate>;
  selectedTemplate: any = false;
  templateMode: boolean = false;
  template: PipelineElementTemplate;
  templateConfigs: Map<string, any> = new Map();

  constructor(private dialogRef: DialogRef<CustomizeComponent>,
              private JsPlumbService: JsplumbService,
              private ShepherdService: ShepherdService,
              private fb: FormBuilder,
              private changeDetectorRef: ChangeDetectorRef,
              private pipelineElementTemplateService: PipelineElementTemplateService) {

  }

  ngOnInit(): void {
    this.originalDialogWidth = this.dialogRef.currentConfig().width;
    this.cachedPipelineElement = this.JsPlumbService.clone(this.pipelineElement.payload) as InvocablePipelineElementUnion;
    this.isDataProcessor = this.cachedPipelineElement instanceof DataProcessorInvocation;
    this.cachedPipelineElement.inputStreams.forEach(is => {
      this.eventSchemas = this.eventSchemas.concat(is.eventSchema);
    });
    this.formValid = this.pipelineElement.settings.completed;

    this.parentForm = this.fb.group({});

    this.parentForm.valueChanges.subscribe(v => {
    });

    this.parentForm.statusChanges.subscribe((status) => {
      this.formValid = this.viewInitialized && this.parentForm.valid;
    })
    if (this.ShepherdService.isTourActive()) {
      this.ShepherdService.trigger("customize-" + this.pipelineElement.type);
    }
    this.loadPipelineElementTemplates();
  }

  loadPipelineElementTemplates() {
    this.pipelineElementTemplateService
        .getPipelineElementTemplates(this.cachedPipelineElement.appId)
        .subscribe(templates => {
          this.availableTemplates = templates;
        });
  }

  close() {
    this.dialogRef.close();
  }

  save() {
    this.pipelineElement.payload = this.cachedPipelineElement;
    this.pipelineElement.settings.completed = true;
    this.pipelineElement.payload.configured = true;
    if (this.ShepherdService.isTourActive()) {
      this.ShepherdService.trigger("save-" + this.pipelineElement.type);
    }
    this.dialogRef.close(this.pipelineElement);
  }

  validConfiguration(event: any) {

  }

  set showDocumentation(value: boolean) {
    if (value) {
      this.dialogRef.changeDialogSize({width: "90vw"})
    } else {
      this.dialogRef.changeDialogSize({width: this.originalDialogWidth})
    }
    this._showDocumentation = value;
  }

  get showDocumentation(): boolean {
    return this._showDocumentation;
  }

  ngAfterViewInit(): void {
    this.viewInitialized = true;
    this.formValid = this.viewInitialized && this.parentForm.valid;
    this.changeDetectorRef.detectChanges();
  }

  triggerUpdate(configurationInfo: ConfigurationInfo) {
    this.completedStaticProperty = {...configurationInfo};
  }

  triggerTemplateMode() {
    this.template = new PipelineElementTemplate();
    this.templateMode = true;
  }

  saveTemplate() {
    this.template.templateConfigs = this.convert(this.templateConfigs);
    this.pipelineElementTemplateService.storePipelineElementTemplate(this.template).subscribe(result => {
      this.loadPipelineElementTemplates();
      this.templateMode = false;
    });
  }

  convert(templateConfigs: Map<string, any>): any {
    let configs: { [index: string]: PipelineElementTemplateConfig } = {};
    templateConfigs.forEach((value, key) => {
      configs[key] = new PipelineElementTemplateConfig();
      configs[key].editable = value.editable;
      configs[key].displayed = value.displayed;
      configs[key].value = value.value;
    });
    return configs;
  }

  cancelTemplateMode() {
    this.templateMode = false;
  }

  loadTemplate(event: any) {
    if (!event.value) {
      this.cachedPipelineElement = this.JsPlumbService.clone(this.pipelineElement.payload) as InvocablePipelineElementUnion;
      this.selectedTemplate = false;
    } else {
      this.selectedTemplate = event.value;
      if (this.cachedPipelineElement instanceof DataProcessorInvocation) {
        this.pipelineElementTemplateService.getConfiguredDataProcessorForTemplate(event.value._id, this.cachedPipelineElement).subscribe(pe => {
          this.cachedPipelineElement = pe as InvocablePipelineElementUnion;
        })
      } else {
        this.pipelineElementTemplateService.getConfiguredDataSinkForTemplate(event.value._id, this.cachedPipelineElement).subscribe(pe => {
          this.cachedPipelineElement = pe as InvocablePipelineElementUnion;
        })
      }

    }
  }


}
