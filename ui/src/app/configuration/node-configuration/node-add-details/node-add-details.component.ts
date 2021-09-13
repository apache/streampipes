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

import { Component, OnInit } from '@angular/core';
import {NodeService} from "../../../platform-services/apis/node.service";
import {DialogRef} from "../../../core-ui/dialog/base-dialog/dialog-ref";
import {MatSnackBar} from "@angular/material/snack-bar";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {STEPPER_GLOBAL_OPTIONS} from "@angular/cdk/stepper";
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {MatChipInputEvent} from "@angular/material/chips";
import {VersionInfo} from "../../../info/versions/service/version-info.model";
import {RestApi} from "../../../services/rest-api.service";

@Component({
  selector: 'node-add-details',
  templateUrl: './node-add-details.component.html',
  styleUrls: ['./node-add-details.component.scss'],
  // providers: [{
  //   provide: STEPPER_GLOBAL_OPTIONS, useValue: {showError: true}
  // }]
})
export class NodeAddDetailsComponent implements OnInit {

  nodeMetadataFormGroup: FormGroup = new FormGroup({});

  added: boolean = false;
  advancedSettings: boolean;
  dockerCommandCreated: boolean;
  errorMessage: string = '';

  selectable = true;
  visible = true;
  removable = true;
  addOnBlur = true;
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];
  tmpTags: string[];

  versionInfo: VersionInfo;
  backend: string = '';
  nodeIp: string = '';
  apiToken: string = '';
  latitude: number;
  longitude: number;
  storagePath: string = '/var/lib/streampipes';
  resourceUpdateFreq: string = '30';
  dockerNodePruningFreq: string = '3600';
  eventBufferSize: string = '1000';
  associatedResourceLayer: string;
  resourceLayers: string[] = ['edge', 'fog', 'cloud'];
  nodeType: string;
  nodeTypes: string[] = ['virtual','phyiscal'];

  dockerRunCommand: string;

  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;


  constructor(private nodeService: NodeService,
              private dialogRef: DialogRef<NodeAddDetailsComponent>,
              private restApi: RestApi,
              private _snackBar: MatSnackBar,
              private _formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.dockerCommandCreated = false;
    this.advancedSettings = false;
    this.associatedResourceLayer = 'edge';
    this.tmpTags = []
    this.getVersion();

    this.nodeMetadataFormGroup.addControl("backendHost", new FormControl(this.backend,
        [Validators.required,
          Validators.maxLength(40)]))
    this.nodeMetadataFormGroup.addControl("nodeHost", new FormControl(this.nodeIp,
        [Validators.required,
          Validators.maxLength(40)]))
    this.nodeMetadataFormGroup.addControl("token", new FormControl(this.apiToken,
        [Validators.maxLength(80)]))
    this.nodeMetadataFormGroup.addControl("latitude", new FormControl(this.latitude,
        [Validators.required]))
    this.nodeMetadataFormGroup.addControl("longitude", new FormControl(this.longitude,
        [Validators.required]))

    this.nodeMetadataFormGroup.controls["backendHost"].valueChanges.subscribe(value => {
      this.backend = value;
    });
    this.nodeMetadataFormGroup.controls["nodeHost"].valueChanges.subscribe(value => {
      this.nodeIp = value;
    });
    this.nodeMetadataFormGroup.controls["token"].valueChanges.subscribe(value => {
      this.apiToken = value;
    });
    this.nodeMetadataFormGroup.controls["latitude"].valueChanges.subscribe(value => {
      this.latitude = value;
    });
    this.nodeMetadataFormGroup.controls["longitude"].valueChanges.subscribe(value => {
      this.longitude = value;
    });

    this.firstFormGroup = this._formBuilder.group({
      firstCtrl: ['', Validators.required]
    });
    this.secondFormGroup = this._formBuilder.group({
      secondCtrl: ['', Validators.required]
    });

  }

  hide() {
    this.dialogRef.close();
  }

  add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;
    if ((value || '').trim()) {
      this.tmpTags.push(value.trim());
    }
    // Reset the input value
    if (input) {
      input.value = '';
    }
  }

  remove(tag: string): void {
    const index = this.tmpTags.indexOf(tag);
    if (index >= 0) {
      this.tmpTags.splice(index, 1);
    }
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, "close", {
      duration: 3000,
    });
  }

  createDocker() {
    // add new node
    this.buildDockerCommand();
    this.dockerCommandCreated = true;
  }

  buildDockerCommand() {
    this.dockerRunCommand = 'docker run -d -p 7077:7077 ';
    this.dockerRunCommand += '--restart always ';
    this.dockerRunCommand += '--hostname streampipes-node-controller '
    this.dockerRunCommand += '--name streampipes-node-controller ';
    this.dockerRunCommand += `-e SP_URL=http://${this.backend}:8030 `;
    this.dockerRunCommand += `-e SP_NODE_CONTROLLER_URL=http://${this.nodeIp}:7077 `;
    this.dockerRunCommand += `-e SP_API_KEY=${this.apiToken} `;
    if (this.tmpTags.length > 0) {
      this.dockerRunCommand += `-e SP_NODE_LOCATION="${this.tagsToString(this.tmpTags)}" `;
    }
    if (this.advancedSettings) {
      this.dockerRunCommand += `-e SP_DOCKER_PRUNING_FREQ_SECS=${this.dockerNodePruningFreq} `;
      this.dockerRunCommand += `-e SP_NODE_RESOURCE_UPDATE_FREQ_SECS=${this.resourceUpdateFreq} `;
      this.dockerRunCommand += `-e SP_NODE_EVENT_BUFFER_SIZE=${this.eventBufferSize} `;
    }
    this.dockerRunCommand += '-v /var/run/docker.sock:/var/run/docker.sock ';
    this.dockerRunCommand += '-v /var/lib/streampipes:/var/lib/streampipes ';
    this.dockerRunCommand += `apachestreampipes/node-controller:${this.versionInfo.backendVersion}`;

  }

  tagsToString(tags: string[]){
    return tags.join(';')
  }

  getVersion(){
    this.restApi.getVersionInfo().subscribe((response) => {
      this.versionInfo = response as VersionInfo;
    })
  }

  copyDockerCmd() {

  }
}
