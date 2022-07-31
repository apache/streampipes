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

import { Component, OnInit, ViewChild } from '@angular/core';
import { AdapterDescriptionUnion, AdapterService, PipelineElementService } from '@streampipes/platform-services';
import { MatTableDataSource } from '@angular/material/table';
import { ConnectService } from '../../services/connect.service';
import { DialogRef, DialogService, PanelType, SpBreadcrumbService } from '@streampipes/shared-ui';
import { DeleteAdapterDialogComponent } from '../../dialog/delete-adapter-dialog/delete-adapter-dialog.component';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { ObjectPermissionDialogComponent } from '../../../core-ui/object-permission-dialog/object-permission-dialog.component';
import { UserRole } from '../../../_enums/user-role.enum';
import { AuthService } from '../../../services/auth.service';
import { HelpComponent } from '../../../editor/dialog/help/help.component';
import { Router } from '@angular/router';
import { AdapterFilterSettingsModel } from '../../model/adapter-filter-settings.model';
import { AdapterFilterPipe } from '../../filter/adapter-filter.pipe';
import { SpConnectRoutes } from '../../connect.routes';

@Component({
  selector: 'sp-existing-adapters',
  templateUrl: './existing-adapters.component.html',
  styleUrls: ['./existing-adapters.component.scss']
})
export class ExistingAdaptersComponent implements OnInit {

  existingAdapters: AdapterDescriptionUnion[] = [];
  filteredAdapters: AdapterDescriptionUnion[] = [];

  currentFilter: AdapterFilterSettingsModel;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  pageSize = 1;
  @ViewChild(MatSort) sort: MatSort;

  displayedColumns: string[] = ['start', 'name', 'adapterBase', 'adapterType', 'lastModified', 'action'];

  dataSource: MatTableDataSource<AdapterDescriptionUnion>;
  isAdmin = false;

  constructor(public connectService: ConnectService,
              private dataMarketplaceService: AdapterService,
              private dialogService: DialogService,
              private authService: AuthService,
              private pipelineElementService: PipelineElementService,
              private router: Router,
              private adapterFilter: AdapterFilterPipe,
              private breadcrumbService: SpBreadcrumbService) {

  }

  ngOnInit(): void {
    this.breadcrumbService.updateBreadcrumb(this.breadcrumbService.getRootLink(SpConnectRoutes.BASE));
    this.authService.user$.subscribe(user => {
      this.isAdmin = user.roles.indexOf(UserRole.ROLE_ADMIN) > -1;
      this.getAdaptersRunning();
    });
  }

  startAdapter(adapter: AdapterDescriptionUnion) {
    this.dataMarketplaceService.startAdapter(adapter).subscribe(response => {
      this.getAdaptersRunning();
    });
  }

  stopAdapter(adapter: AdapterDescriptionUnion) {
    this.dataMarketplaceService.stopAdapter(adapter).subscribe(response => {
      this.getAdaptersRunning();
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
        this.getAdaptersRunning();
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
        this.getAdaptersRunning();
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

  getAdaptersRunning(): void {
    this.dataMarketplaceService.getAdapters().subscribe(adapters => {
      this.existingAdapters = adapters;
      this.existingAdapters.sort((a, b) => a.name.localeCompare(b.name));
      this.filteredAdapters = this.adapterFilter.transform(this.existingAdapters, this.currentFilter);
      this.dataSource = new MatTableDataSource(this.filteredAdapters);
      setTimeout(() => {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      });
    });
  }

  createNewAdapter(): void {
    this.router.navigate(['connect', 'create']);
  }

  applyFilter(filter: AdapterFilterSettingsModel) {
    this.currentFilter = filter;
    if (this.dataSource) {
      this.dataSource.data = this.adapterFilter.transform(this.filteredAdapters, this.currentFilter);
    }
  }

}
