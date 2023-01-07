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
import {
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import {
    EmailConfig,
    GeneralConfigModel,
    GeneralConfigService,
    MailConfigService,
} from '@streampipes/platform-services';
import { zip } from 'rxjs';
import { AvailableRolesService } from '../../services/available-roles.service';
import { RoleDescription } from '../../_models/auth.model';
import { UserRole } from '../../_enums/user-role.enum';
import { AppConstants } from '../../services/app.constants';
import { SpConfigurationTabs } from '../configuration-tabs';
import { SpBreadcrumbService } from '@streampipes/shared-ui';
import { SpConfigurationRoutes } from '../configuration.routes';

@Component({
    selector: 'sp-general-configuration',
    templateUrl: './general-configuration.component.html',
    styleUrls: ['./general-configuration.component.scss'],
})
export class GeneralConfigurationComponent implements OnInit {
    tabs = SpConfigurationTabs.getTabs();

    parentForm: UntypedFormGroup;
    formReady = false;

    generalConfig: GeneralConfigModel;
    mailConfig: EmailConfig;

    availableRoles: RoleDescription[];

    constructor(
        private fb: UntypedFormBuilder,
        private generalConfigService: GeneralConfigService,
        private mailConfigService: MailConfigService,
        private availableRolesService: AvailableRolesService,
        private appConstants: AppConstants,
        private breadcrumbService: SpBreadcrumbService,
    ) {}

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: SpConfigurationTabs.getTabs()[0].itemTitle },
        ]);
        this.availableRoles = this.availableRolesService.availableRoles.filter(
            role => role.role !== UserRole.ROLE_ADMIN,
        );
        zip(
            this.generalConfigService.getGeneralConfig(),
            this.mailConfigService.getMailConfig(),
        ).subscribe(configs => {
            if (configs[0].configured) {
                this.generalConfig = configs[0];
            } else {
                this.generalConfig = {
                    configured: false,
                    hostname: window.location.hostname,
                    port: window.location.port as unknown as number,
                    protocol: window.location.protocol.replace(
                        ':',
                        '',
                    ) as unknown as 'http' | 'https',
                    allowSelfRegistration: false,
                    allowPasswordRecovery: false,
                    defaultUserRoles: [UserRole.ROLE_PIPELINE_USER],
                    appName: this.appConstants.APP_NAME,
                };
            }
            this.mailConfig = configs[1];
            this.parentForm = this.fb.group({});
            this.parentForm.addControl(
                'appName',
                new UntypedFormControl(
                    this.generalConfig.appName,
                    Validators.required,
                ),
            );
            this.parentForm.addControl(
                'protocol',
                new UntypedFormControl(
                    this.generalConfig.protocol,
                    Validators.required,
                ),
            );
            this.parentForm.addControl(
                'port',
                new UntypedFormControl(
                    this.generalConfig.port,
                    Validators.required,
                ),
            );
            this.parentForm.addControl(
                'hostname',
                new UntypedFormControl(
                    this.generalConfig.hostname,
                    Validators.required,
                ),
            );
            this.parentForm.addControl(
                'allowSelfRegistration',
                new UntypedFormControl(
                    this.generalConfig.allowSelfRegistration,
                ),
            );
            this.parentForm.addControl(
                'allowPasswordRecovery',
                new UntypedFormControl(
                    this.generalConfig.allowPasswordRecovery,
                ),
            );
            this.parentForm.addControl(
                'defaultUserRoles',
                new UntypedFormControl(
                    [UserRole.ROLE_PIPELINE_USER],
                    Validators.required,
                ),
            );
            this.parentForm.valueChanges.subscribe(v => {
                this.generalConfig.appName = v.appName;
                this.generalConfig.protocol = v.protocol;
                this.generalConfig.port = v.port;
                this.generalConfig.hostname = v.hostname;
                this.generalConfig.allowPasswordRecovery =
                    v.allowPasswordRecovery;
                this.generalConfig.allowSelfRegistration =
                    v.allowSelfRegistration;
                this.generalConfig.defaultUserRoles = v.defaultUserRoles.map(
                    r => UserRole[r],
                );
                console.log(this.generalConfig);
            });

            this.formReady = true;
        });
    }

    loadConfig() {
        this.generalConfigService.getGeneralConfig().subscribe(config => {
            this.generalConfig = config;
        });
    }

    updateConfig() {
        this.generalConfigService
            .updateGeneralConfig(this.generalConfig)
            .subscribe(result => {
                this.loadConfig();
            });
    }
}
