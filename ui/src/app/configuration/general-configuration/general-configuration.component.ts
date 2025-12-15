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
    Role,
} from '@streampipes/platform-services';
import { Observable, zip } from 'rxjs';
import { AvailableRolesService } from '../../services/available-roles.service';
import { UserRole } from '../../_enums/user-role.enum';
import { AppConstants } from '../../services/app.constants';
import { SpConfigurationTabsService } from '../configuration-tabs.service';
import { SpBreadcrumbService, SpNavigationItem } from '@streampipes/shared-ui';
import { SpConfigurationRoutes } from '../configuration.routes';
import { map } from 'rxjs/operators';

@Component({
    selector: 'sp-general-configuration',
    templateUrl: './general-configuration.component.html',
    styleUrls: ['./general-configuration.component.scss'],
    standalone: false,
})
export class GeneralConfigurationComponent implements OnInit {
    tabs: SpNavigationItem[] = [];

    parentForm: UntypedFormGroup;
    formReady = false;

    generalConfig: GeneralConfigModel;
    mailConfig: EmailConfig;

    availableRoles$: Observable<Role[]>;

    constructor(
        private fb: UntypedFormBuilder,
        private generalConfigService: GeneralConfigService,
        private mailConfigService: MailConfigService,
        private availableRolesService: AvailableRolesService,
        private appConstants: AppConstants,
        private breadcrumbService: SpBreadcrumbService,
        private tabService: SpConfigurationTabsService,
    ) {}

    ngOnInit(): void {
        this.tabs = this.tabService.getTabs();
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: this.tabService.getTabTitle('general') },
        ]);
        this.availableRoles$ = this.availableRolesService.availableRoles$.pipe(
            map(roles =>
                roles.filter(role => role.elementId !== UserRole.ROLE_ADMIN),
            ),
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
                    linkSettings: configs[0].linkSettings,
                    userAcknowledgment: {
                        required: false,
                        title: '',
                        text: '',
                    },
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

            this.parentForm.addControl(
                'documentationUrl',
                new UntypedFormControl(
                    this.generalConfig.linkSettings.documentationUrl,
                ),
            );
            this.parentForm.addControl(
                'showDocumentationLinkOnStartScreen',
                new UntypedFormControl(
                    this.generalConfig.linkSettings.showDocumentationLinkOnStartScreen,
                ),
            );
            this.parentForm.addControl(
                'showDocumentationLinkInProfileMenu',
                new UntypedFormControl(
                    this.generalConfig.linkSettings.showDocumentationLinkInProfileMenu,
                ),
            );
            this.parentForm.addControl(
                'supportUrl',
                new UntypedFormControl(
                    this.generalConfig.linkSettings.supportUrl,
                ),
            );
            this.parentForm.addControl(
                'showSupportUrlOnStartScreen',
                new UntypedFormControl(
                    this.generalConfig.linkSettings.showSupportUrlOnStartScreen,
                ),
            );
            this.parentForm.addControl(
                'showApiDocumentationLinkOnStartScreen',
                new UntypedFormControl(
                    this.generalConfig.linkSettings.showApiDocumentationLinkOnStartScreen,
                ),
            );

            this.parentForm.addControl(
                'requireTermsAcknowledgment',
                new UntypedFormControl(
                    this.generalConfig.userAcknowledgment?.required || false,
                ),
            );

            this.parentForm.addControl(
                'termsAcknowledgmentTitle',
                new UntypedFormControl(
                    this.generalConfig.userAcknowledgment?.title || '',
                ),
            );

            this.parentForm.addControl(
                'termsAcknowledgmentText',
                new UntypedFormControl(
                    this.generalConfig.userAcknowledgment?.text || '',
                ),
            );

            this.formReady = true;
        });
    }

    loadConfig() {
        this.generalConfigService.getGeneralConfig().subscribe(config => {
            this.generalConfig = config;
        });
    }

    updateConfig() {
        const formValue = this.parentForm.getRawValue();
        const toUserRole = (r: string | number) =>
            typeof r === 'number'
                ? r
                : UserRole[r as keyof typeof UserRole] ?? r;

        this.generalConfig = {
            ...this.generalConfig,
            appName: formValue.appName,
            protocol: formValue.protocol,
            port: formValue.port,
            hostname: formValue.hostname,
            allowPasswordRecovery: formValue.allowPasswordRecovery,
            allowSelfRegistration: formValue.allowSelfRegistration,
            defaultUserRoles: (formValue.defaultUserRoles || []).map(
                toUserRole,
            ),
            linkSettings: {
                documentationUrl: formValue.documentationUrl,
                supportUrl: formValue.supportUrl,
                showApiDocumentationLinkOnStartScreen:
                    formValue.showApiDocumentationLinkOnStartScreen,
                showSupportUrlOnStartScreen:
                    formValue.showSupportUrlOnStartScreen,
                showDocumentationLinkInProfileMenu:
                    formValue.showDocumentationLinkInProfileMenu,
                showDocumentationLinkOnStartScreen:
                    formValue.showDocumentationLinkOnStartScreen,
            },
            userAcknowledgment: {
                required: formValue.requireTermsAcknowledgment,
                title: formValue.termsAcknowledgmentTitle,
                text: formValue.termsAcknowledgmentText,
            },
        };

        this.generalConfigService
            .updateGeneralConfig(this.generalConfig)
            .subscribe(result => {
                this.loadConfig();
            });
    }
}
