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

import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { MessagingConfigurationComponent } from './messaging-configuration/messaging-configuration.component';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { DatalakeConfigurationComponent } from './datalake-configuration/datalake-configuration.component';
import { DeleteDatalakeIndexComponent } from './dialog/delete-datalake-index/delete-datalake-index-dialog.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { SecurityConfigurationComponent } from './security-configuration/security-configuration.component';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { MatDividerModule } from '@angular/material/divider';
import { SecurityUserConfigComponent } from './security-configuration/security-user-configuration/security-user-config.component';
import { SecurityServiceConfigComponent } from './security-configuration/security-service-configuration/security-service-config.component';
import { EditUserDialogComponent } from './security-configuration/edit-user-dialog/edit-user-dialog.component';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { SecurityUserGroupConfigComponent } from './security-configuration/user-group-configuration/user-group-configuration.component';
import { EditGroupDialogComponent } from './security-configuration/edit-group-dialog/edit-group-dialog.component';
import { EmailConfigurationComponent } from './email-configuration/email-configuration.component';
import { GeneralConfigurationComponent } from './general-configuration/general-configuration.component';
import { SecurityAuthenticationConfigurationComponent } from './security-configuration/authentication-configuration/authentication-configuration.component';
import { RouterModule } from '@angular/router';
import { SharedUiModule } from '@streampipes/shared-ui';
import { SpDataExportImportComponent } from './export/data-export-import.component';
import { SpDataExportDialogComponent } from './export/export-dialog/data-export-dialog.component';
import { SpDataImportDialogComponent } from './export/import-dialog/data-import-dialog.component';
import { SpDataExportItemComponent } from './export/export-dialog/data-export-item/data-export-item.component';
import { SpEditLabelComponent } from './label-configuration/edit-label/edit-label.component';
import { SpLabelConfigurationComponent } from './label-configuration/label-configuration.component';
import { ColorPickerModule } from 'ngx-color-picker';
import { ExtensionsServiceManagementComponent } from './extensions-service-management/extensions-service-management.component';
import { ServiceConfigsComponent } from './extensions-service-management/extensions-service-configuration/service-configs/service-configs.component';
import { ServiceConfigsTextComponent } from './extensions-service-management/extensions-service-configuration/service-configs/service-configs-text/service-configs-text.component';
import { ServiceConfigsPasswordComponent } from './extensions-service-management/extensions-service-configuration/service-configs/service-configs-password/service-configs-password.component';
import { ServiceConfigsBooleanComponent } from './extensions-service-management/extensions-service-configuration/service-configs/service-configs-boolean/service-configs-boolean.component';
import { ServiceConfigsNumberComponent } from './extensions-service-management/extensions-service-configuration/service-configs/service-configs-number/service-configs-number.component';
import { SpRegisteredExtensionsServiceComponent } from './extensions-service-management/registered-extensions-services/registered-extensions-services.component';
import { SpExtensionsServiceConfigurationComponent } from './extensions-service-management/extensions-service-configuration/extensions-service-configuration.component';
import { SpMessagingBrokerConfigComponent } from './messaging-configuration/broker-config/broker-config.component';
import { SpExtensionsServiceDetailsDialogComponent } from './dialog/extensions-service-details/extensions-service-details-dialog.component';
import { SpEmailTemplateConfigurationComponent } from './email-configuration/email-template-configuration/email-template-configuration.component';
import { CodemirrorModule } from '@ctrl/ngx-codemirror';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { PipelineElementTypeFilter } from './extensions-installation/filter/pipeline-element-type.pipe';
import { PipelineElementNameFilter } from './extensions-installation/filter/pipeline-element-name.pipe';
import { PipelineElementInstallationStatusFilter } from './extensions-installation/filter/pipeline-element-installation-status.pipe';
import { OrderByPipe } from './extensions-installation/filter/order-by.pipe';
import { SpExtensionsInstallationDialogComponent } from './dialog/extensions-installation/extensions-installation.component';
import { EndpointItemComponent } from './extensions-installation/endpoint-item/endpoint-item.component';
import { SpExtensionsInstallationComponent } from './extensions-installation/extensions-installation.component';
import { MatMenuModule } from '@angular/material/menu';
import { SpConfigurationLinkSettingsComponent } from './general-configuration/link-settings/link-settings.component';
import { SitesConfigurationComponent } from './sites-configuration/sites-configuration.component';
import { LocationFeaturesConfigurationComponent } from './sites-configuration/location-features-configuration/location-features-configuration.component';
import { SiteAreaConfigurationComponent } from './sites-configuration/site-area-configuration/site-area-configuration.component';
import { MatSort } from '@angular/material/sort';
import { ManageSiteDialogComponent } from './dialog/manage-site/manage-site-dialog.component';
import { EditAssetLocationComponent } from './dialog/manage-site/edit-location/edit-location.component';
import { EditAssetLocationAreaComponent } from './dialog/manage-site/edit-location/edit-location-area/edit-location-area.component';
import { SecurityRoleConfigComponent } from './security-configuration/role-configuration/role-configuration.component';
import { EditRoleDialogComponent } from './security-configuration/edit-role-dialog/edit-role-dialog.component';
import { MatListModule } from '@angular/material/list';
import { configurationRouteGuard } from './configuration-route.guard';
import { ServiceConfigsItemComponent } from './extensions-service-management/extensions-service-configuration/service-configs/service-configs-item/service-configs-item.component';
import { FilesComponent } from './files/files.component';
import { FileOverviewComponent } from './files/file-overview/file-overview.component';
import { FileUploadDialogComponent } from './dialog/file-upload/file-upload-dialog.component';
import { FileRenameDialogComponent } from './dialog/file-rename/file-rename-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';

@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule,
        MatGridListModule,
        MatButtonModule,
        MatButtonToggleModule,
        MatProgressSpinnerModule,
        MatIconModule,
        MatInputModule,
        MatCheckboxModule,
        MatDividerModule,
        MatMenuModule,
        MatTooltipModule,
        MatTableModule,
        MatPaginatorModule,
        MatRadioModule,
        MatSelectModule,
        FormsModule,
        DragDropModule,
        CoreUiModule,
        ReactiveFormsModule,
        PlatformServicesModule,
        RouterModule.forChild([
            {
                path: '',
                children: [
                    {
                        path: '',
                        redirectTo: 'general',
                        pathMatch: 'full',
                    },
                    {
                        path: 'general',
                        component: GeneralConfigurationComponent,
                        canActivate: [configurationRouteGuard],
                    },
                    {
                        path: 'datalake',
                        component: DatalakeConfigurationComponent,
                        canActivate: [configurationRouteGuard],
                    },
                    {
                        path: 'email',
                        component: EmailConfigurationComponent,
                        canActivate: [configurationRouteGuard],
                    },
                    {
                        path: 'export',
                        component: SpDataExportImportComponent,
                        canActivate: [configurationRouteGuard],
                    },
                    {
                        path: 'labels',
                        component: SpLabelConfigurationComponent,
                        canActivate: [configurationRouteGuard],
                    },
                    {
                        path: 'messaging',
                        component: MessagingConfigurationComponent,
                        canActivate: [configurationRouteGuard],
                    },
                    {
                        path: 'extensions-installation',
                        component: SpExtensionsInstallationComponent,
                        canActivate: [configurationRouteGuard],
                    },
                    {
                        path: 'extensions-services',
                        component: ExtensionsServiceManagementComponent,
                        canActivate: [configurationRouteGuard],
                    },
                    {
                        path: 'files',
                        component: FilesComponent,
                        canActivate: [configurationRouteGuard],
                    },
                    {
                        path: 'security',
                        component: SecurityConfigurationComponent,
                        canActivate: [configurationRouteGuard],
                    },
                    {
                        path: 'sites',
                        component: SitesConfigurationComponent,
                        canActivate: [configurationRouteGuard],
                    },
                ],
            },
        ]),
        SharedUiModule,
        ColorPickerModule,
        CodemirrorModule,
        MatSort,
        MatListModule,
        MatDialogModule,
    ],
    declarations: [
        ServiceConfigsComponent,
        ServiceConfigsTextComponent,
        ServiceConfigsPasswordComponent,
        ServiceConfigsBooleanComponent,
        ServiceConfigsItemComponent,
        ServiceConfigsNumberComponent,
        DeleteDatalakeIndexComponent,
        EditAssetLocationComponent,
        EditAssetLocationAreaComponent,
        EditRoleDialogComponent,
        EditUserDialogComponent,
        EditGroupDialogComponent,
        EmailConfigurationComponent,
        FilesComponent,
        FileOverviewComponent,
        FileUploadDialogComponent,
        FileRenameDialogComponent,
        GeneralConfigurationComponent,
        ExtensionsServiceManagementComponent,
        LocationFeaturesConfigurationComponent,
        ManageSiteDialogComponent,
        SitesConfigurationComponent,
        SecurityAuthenticationConfigurationComponent,
        SecurityConfigurationComponent,
        SecurityRoleConfigComponent,
        SecurityUserConfigComponent,
        SecurityUserGroupConfigComponent,
        SecurityServiceConfigComponent,
        SiteAreaConfigurationComponent,
        MessagingConfigurationComponent,
        DatalakeConfigurationComponent,
        SpConfigurationLinkSettingsComponent,
        SpDataExportImportComponent,
        SpDataExportDialogComponent,
        SpDataExportItemComponent,
        SpDataImportDialogComponent,
        SpEditLabelComponent,
        SpEmailTemplateConfigurationComponent,
        SpExtensionsServiceDetailsDialogComponent,
        SpLabelConfigurationComponent,
        SpMessagingBrokerConfigComponent,
        SpRegisteredExtensionsServiceComponent,
        SpExtensionsServiceConfigurationComponent,

        SpExtensionsInstallationComponent,
        SpExtensionsInstallationDialogComponent,
        EndpointItemComponent,
        OrderByPipe,
        PipelineElementNameFilter,
        PipelineElementInstallationStatusFilter,
        PipelineElementTypeFilter,
    ],
    providers: [
        OrderByPipe,
        PipelineElementInstallationStatusFilter,
        PipelineElementNameFilter,
        PipelineElementTypeFilter,
    ],
})
export class ConfigurationModule {}
