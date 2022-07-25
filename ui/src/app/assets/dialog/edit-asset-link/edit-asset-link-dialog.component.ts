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

import { Component, Input, OnInit } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import {
  AssetLink,
  AssetLinkType, Dashboard, DashboardService,
  DataViewDataExplorerService,
  GenericStorageService, Pipeline,
  PipelineService
} from '@streampipes/platform-services';
import { AssetConstants } from '../../constants/asset.constants';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { zip } from 'rxjs';
import { MatSelectChange } from '@angular/material/select';

@Component({
  selector: 'sp-edit-asset-link-dialog-component',
  templateUrl: './edit-asset-link-dialog.component.html',
  styleUrls: ['./edit-asset-link-dialog.component.scss']
})
export class EditAssetLinkDialogComponent implements OnInit {

  @Input()
  assetLink: AssetLink;

  @Input()
  assetLinkTypes: AssetLinkType[];

  @Input()
  createMode: boolean;

  parentForm: FormGroup;

  clonedAssetLink: AssetLink;

  // Resources
  pipelines: Pipeline[];
  dataViews: Dashboard[];
  dashboards: Dashboard[];

  selectedLinkType: string;

  constructor(private dialogRef: DialogRef<EditAssetLinkDialogComponent>,
              private genericStorageService: GenericStorageService,
              private pipelineService: PipelineService,
              private dataViewService: DataViewDataExplorerService,
              private dashboardService: DashboardService) {
  }

  ngOnInit(): void {
    this.getAllResources();
    this.clonedAssetLink = {...this.assetLink};
  }

  getCurrAssetLinkType(): AssetLinkType {
    if (this.createMode) {
      return this.assetLinkTypes[0];
    } else {
      return this.assetLinkTypes.find(a => a.linkType === this.assetLink.linkType);
    }
  }

  store() {
    this.assetLink = this.clonedAssetLink;
    this.dialogRef.close(this.assetLink);
  }

  cancel() {
    this.dialogRef.close();
  }

  getAllResources() {
    zip(
      this.pipelineService.getOwnPipelines(),
      this.dataViewService.getDataViews(),
      this.dashboardService.getDashboards()).subscribe(response => {
      this.pipelines = response[0];
      this.dataViews = response[1];
      this.dashboards = response[2];
    });
  }

  onLinkTypeChanged(event: MatSelectChange): void {
    this.selectedLinkType = event.value;
  }

}
