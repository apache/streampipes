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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Dashboard } from '../../models/dashboard.model';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { DashboardService } from '../../services/dashboard.service';
import { EditDashboardDialogComponent } from '../../dialogs/edit-dashboard/edit-dashboard-dialog.component';
import { Tuple2 } from '../../../core-model/base/Tuple2';
import { Router } from '@angular/router';
import { ObjectPermissionDialogComponent } from '../../../core-ui/object-permission-dialog/object-permission-dialog.component';
import { PanelType } from '../../../core-ui/dialog/base-dialog/base-dialog.model';
import { DialogService } from '../../../core-ui/dialog/base-dialog/base-dialog.service';
import { UserRole } from '../../../_enums/user-role.enum';
import { AuthService } from '../../../services/auth.service';
import { UserPrivilege } from '../../../_enums/user-privilege.enum';

@Component({
  selector: 'dashboard-overview',
  templateUrl: './dashboard-overview.component.html',
  styleUrls: ['./dashboard-overview.component.css']
})
export class DashboardOverviewComponent implements OnInit {

  @Input() dashboards: Dashboard[];
  @Output() reloadDashboardsEmitter = new EventEmitter<void>();
  @Output() selectDashboardEmitter = new EventEmitter<Tuple2<Dashboard, boolean>>();

  dataSource = new MatTableDataSource<Dashboard>();
  displayedColumns: string[] = [];

  isAdmin = false;
  hasDashboardWritePrivileges = false;
  hasDashboardDeletePrivileges = false;

  constructor(private dashboardService: DashboardService,
              public dialog: MatDialog,
              private router: Router,
              private dialogService: DialogService,
              private authService: AuthService) {

  }

  ngOnInit(): void {
    this.authService.user$.subscribe(user => {
      this.isAdmin = user.roles.indexOf(UserRole.ROLE_ADMIN) > -1;
      this.hasDashboardWritePrivileges = this.authService.hasRole(UserPrivilege.PRIVILEGE_WRITE_DASHBOARD);
      this.hasDashboardDeletePrivileges = this.authService.hasRole(UserPrivilege.PRIVILEGE_DELETE_DASHBOARD);
      this.displayedColumns = ['name', 'actions'];

    });
    this.dataSource.data = this.dashboards;
  }

  openNewDashboardDialog() {
    const dashboard = {} as Dashboard;
    dashboard.widgets = [];

    this.openDashboardModificationDialog(true, dashboard);
  }

  openDashboardModificationDialog(createMode: boolean, dashboard: Dashboard) {
    const dialogRef = this.dialogService.open(EditDashboardDialogComponent, {
      panelType: PanelType.STANDARD_PANEL,
      title: createMode ? 'New Dashboard' : 'Edit Dashboard',
      width: '50vw',
      data: {
        'createMode': createMode,
        'dashboard': dashboard
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      this.reloadDashboardsEmitter.emit();
    });
  }

  openEditDashboardDialog(dashboard: Dashboard) {
    this.openDashboardModificationDialog(false, dashboard);
  }

  openDeleteDashboardDialog(dashboard: Dashboard) {
    // TODO add confirm dialog
    this.dashboardService.deleteDashboard(dashboard).subscribe(result => {
      this.reloadDashboardsEmitter.emit();
    });
  }

  showDashboard(dashboard: Dashboard, openInEditMode: boolean) {
    const data: Tuple2<Dashboard, boolean> = {} as Tuple2<Dashboard, boolean>;
    data.a = dashboard;
    data.b = openInEditMode;
    this.selectDashboardEmitter.emit(data);
  }

  openExternalDashboard(dashboard: Dashboard) {
    const href = this.router.createUrlTree(['standalone', dashboard._id]);
    // TODO fixes bug that hashing strategy is ignored by createUrlTree
    window.open('#' + href.toString(), '_blank');
  }

  showPermissionsDialog(dashboard: Dashboard) {

    const dialogRef = this.dialogService.open(ObjectPermissionDialogComponent, {
      panelType: PanelType.SLIDE_IN_PANEL,
      title: 'Manage permissions',
      width: '50vw',
      data: {
        'objectInstanceId': dashboard._id,
        'headerTitle': 'Manage permissions for dashboard ' + dashboard.name
      }
    });

    dialogRef.afterClosed().subscribe(refresh => {
      if (refresh) {
        this.reloadDashboardsEmitter.emit();
      }
    });
  }

}
