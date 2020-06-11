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

import {Component, Input, OnInit} from "@angular/core";
import {InvocablePipelineElementUnion, PipelineElementConfig} from "../../model/editor.model";
import {DialogRef} from "../../../core-ui/dialog/base-dialog/dialog-ref";
import {JsplumbService} from "../../services/jsplumb.service";
import {EventSchema} from "../../../core-model/gen/streampipes-model";

@Component({
  selector: 'customize-pipeline-element',
  templateUrl: './customize.component.html',
  styleUrls: ['./customize.component.css']
})
export class CustomizeComponent implements OnInit {

  @Input()
  pipelineElement: PipelineElementConfig;

  cachedPipelineElement: InvocablePipelineElementUnion;
  eventSchemas: EventSchema[] = [];

  displayRecommended: boolean;
  showDocumentation: boolean = false;
  customizeForm: any;
  restrictedEditMode: boolean;

  selectedElement: any;
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

  //ShepherdService: ShepherdService;



  constructor(private dialogRef: DialogRef<CustomizeComponent>,
              private JsPlumbService: JsplumbService) {

  }

  ngOnInit(): void {
    this.cachedPipelineElement = this.JsPlumbService.clone(this.pipelineElement.payload) as InvocablePipelineElementUnion;
    this.cachedPipelineElement.inputStreams.forEach(is => {
      this.eventSchemas = this.eventSchemas.concat(is.eventSchema);
    });
  }

  close() {
    this.dialogRef.close();
  }

  save() {
    this.pipelineElement.payload = this.cachedPipelineElement;
    this.pipelineElement.settings.completed = true;
    this.pipelineElement.payload.configured = true;
    this.dialogRef.close(this.pipelineElement);
  }

  validConfiguration(event: any) {
  }

}