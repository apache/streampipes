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

import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { AdapterDescriptionUnion } from '../../../../../../projects/streampipes/platform-services/src/lib/model/gen/streampipes-model';
import { MatTableDataSource } from '@angular/material/table';
import { ConnectService } from '../../../services/connect.service';
import { DataMarketplaceService } from '../../../services/data-marketplace.service';
import { DialogRef } from '../../../../core-ui/dialog/base-dialog/dialog-ref';
import { PanelType } from '../../../../core-ui/dialog/base-dialog/base-dialog.model';
import { DialogService } from '../../../../core-ui/dialog/base-dialog/base-dialog.service';
import { DeleteAdapterDialogComponent } from '../../../dialog/delete-adapter-dialog/delete-adapter-dialog.component';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { ObjectPermissionDialogComponent } from '../../../../core-ui/object-permission-dialog/object-permission-dialog.component';
import { UserRole } from '../../../../_enums/user-role.enum';
import { AuthService } from '../../../../services/auth.service';
import {PipelineElementService} from "../../../../../../projects/streampipes/platform-services/src/lib/apis/pipeline-element.service";
import {HelpComponent} from "../../../../editor/dialog/help/help.component";

@Component({
  selector: 'sp-existing-adapters',
  templateUrl: './existing-adapters.component.html',
  styleUrls: ['./existing-adapters.component.scss']
})
export class ExistingAdaptersComponent implements OnInit {

  _existingAdapters: AdapterDescriptionUnion[];

  @Input()
  filterTerm: string;

  @Output()
  updateAdapterEmitter: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  createTemplateEmitter: EventEmitter<AdapterDescriptionUnion> = new EventEmitter<AdapterDescriptionUnion>();

  @ViewChild(MatPaginator) paginator: MatPaginator;
  pageSize = 1;
  @ViewChild(MatSort) sort: MatSort;

  displayedColumns: string[] = ['start', 'name', 'adapterBase', 'adapterType', 'lastModified', 'action'];

  dataSource: MatTableDataSource<AdapterDescriptionUnion>;
  isAdmin = false;

  constructor(public connectService: ConnectService,
              private dataMarketplaceService: DataMarketplaceService,
              private dialogService: DialogService,
              private authService: AuthService,
              private pipelineElementService: PipelineElementService) {

  }

  ngOnInit(): void {
    this.authService.user$.subscribe(user => {
      this.isAdmin = user.roles.indexOf(UserRole.ROLE_ADMIN) > -1;
    });
    this.dataSource = new MatTableDataSource(this.existingAdapters);
    setTimeout(() => {
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    });
  }

  startAdapter(adapter: AdapterDescriptionUnion) {
    this.dataMarketplaceService.startAdapter(adapter).subscribe(response => {
      this.updateAdapterEmitter.emit();
    });
  }

  stopAdapter(adapter: AdapterDescriptionUnion) {
    this.dataMarketplaceService.stopAdapter(adapter).subscribe(response => {
      this.updateAdapterEmitter.emit();
    });
  }

  getIconUrl(adapter: AdapterDescriptionUnion) {
    if (adapter.includedAssets.length > 0) {
      return this.dataMarketplaceService.getAssetUrl(adapter.appId) + '/icon';
    } else {
      return 'assets/img/connect/' + adapter.iconUrl;
    }
  }

  showPermissionsDialog(adapter: AdapterDescriptionUnion) {

    const dialogRef = this.dialogService.open(ObjectPermissionDialogComponent, {
      panelType: PanelType.SLIDE_IN_PANEL,
      title: 'Manage permissions',
      width: '50vw',
      data: {
        'objectInstanceId': adapter.correspondingDataStreamElementId,
        'headerTitle': 'Manage permissions for adapter ' + adapter.name
      }
    });

    dialogRef.afterClosed().subscribe(refresh => {
      if (refresh) {
        this.updateAdapterEmitter.emit();
      }
    });
  }

  deleteAdapter(adapter: AdapterDescriptionUnion): void {
    const dialogRef: DialogRef<DeleteAdapterDialogComponent> = this.dialogService.open(DeleteAdapterDialogComponent, {
      panelType: PanelType.STANDARD_PANEL,
      title: 'Delete Adapter',
      width: '70vw',
      data: {
        'adapter': adapter
      }
    });

    dialogRef.afterClosed().subscribe(data => {
      if (data) {
        this.updateAdapterEmitter.emit();
      }
    });
  }

  openHelpDialog(adapter: AdapterDescriptionUnion) {
    const streamId = adapter.correspondingDataStreamElementId;

    this.pipelineElementService.getDataStreamByElementId(streamId).subscribe(stream => {
      if (stream) {
        this.dialogService.open(HelpComponent, {
          panelType: PanelType.STANDARD_PANEL,
          title: stream.name,
          width: '70vw',
          data: {
            'pipelineElement': stream
          }
        });
      }
    });
  }


  createTemplate(adapter: AdapterDescriptionUnion): void {
    this.createTemplateEmitter.emit(adapter);
  }

  @Input()
  set existingAdapters(adapters: AdapterDescriptionUnion[]) {
    this._existingAdapters = adapters;
    this.dataSource = new MatTableDataSource(adapters);
  }

  get existingAdapters(): AdapterDescriptionUnion[] {
    return this._existingAdapters;
  }

}
