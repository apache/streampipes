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
import { EmailConfig, MailConfigService } from '@streampipes/platform-services';
import { SpConfigurationTabs } from '../configuration-tabs';
import { SpConfigurationRoutes } from '../configuration.routes';
import { SpBreadcrumbService } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-email-configuration',
    templateUrl: './email-configuration.component.html',
    styleUrls: ['./email-configuration.component.scss'],
})
export class EmailConfigurationComponent implements OnInit {
    tabs = SpConfigurationTabs.getTabs();

    parentForm: UntypedFormGroup;

    mailConfig: EmailConfig;
    formReady = false;
    defaultRecipient = '';

    attemptSendingTestMail = false;
    sendingTestMailInProgress = false;
    sendingTestMailSuccess = false;
    sendingEmailErrorMessage = '';

    constructor(
        private fb: UntypedFormBuilder,
        private mailConfigService: MailConfigService,
        private breadcrumbService: SpBreadcrumbService,
    ) {}

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: SpConfigurationTabs.getTabs()[5].itemTitle },
        ]);
        this.loadMailConfig(true);
    }

    loadMailConfig(initializeControls?: boolean) {
        this.mailConfigService.getMailConfig().subscribe(response => {
            this.mailConfig = response;
            if (initializeControls) {
                this.initForm();
            }
        });
    }

    initForm() {
        this.formReady = true;
        this.parentForm = this.fb.group({});
        this.parentForm.addControl(
            'smtpServerHost',
            new UntypedFormControl(
                this.mailConfig.smtpServerHost,
                Validators.required,
            ),
        );
        this.parentForm.addControl(
            'smtpServerPort',
            new UntypedFormControl(
                this.mailConfig.smtpServerPort,
                Validators.required,
            ),
        );
        this.parentForm.addControl(
            'usesAuthentication',
            new UntypedFormControl(this.mailConfig.usesAuthentication),
        );
        this.parentForm.addControl(
            'smtpUsername',
            new UntypedFormControl(this.mailConfig.smtpUsername),
        );
        this.parentForm.addControl(
            'smtpPassword',
            new UntypedFormControl(this.mailConfig.smtpPassword),
        );
        this.parentForm.addControl(
            'usesProxy',
            new UntypedFormControl(this.mailConfig.usesProxy),
        );
        this.parentForm.addControl(
            'proxyHost',
            new UntypedFormControl(this.mailConfig.proxyHost),
        );
        this.parentForm.addControl(
            'proxyPort',
            new UntypedFormControl(this.mailConfig.proxyPort),
        );
        this.parentForm.addControl(
            'usesProxyAuthentication',
            new UntypedFormControl(this.mailConfig.usesProxyAuthentication),
        );
        this.parentForm.addControl(
            'proxyUsername',
            new UntypedFormControl(this.mailConfig.proxyUser),
        );
        this.parentForm.addControl(
            'proxyPassword',
            new UntypedFormControl(this.mailConfig.proxyPassword),
        );
        this.parentForm.addControl(
            'senderAddress',
            new UntypedFormControl(this.mailConfig.senderAddress, [
                Validators.required,
                Validators.email,
            ]),
        );
        this.parentForm.addControl(
            'senderName',
            new UntypedFormControl(this.mailConfig.senderName),
        );
        this.parentForm.addControl(
            'transport',
            new UntypedFormControl(
                this.mailConfig.transportStrategy,
                Validators.required,
            ),
        );
        this.parentForm.addControl(
            'defaultRecipient',
            new UntypedFormControl(this.defaultRecipient, Validators.email),
        );

        this.parentForm.valueChanges.subscribe(v => {
            this.mailConfig.smtpServerHost = v.smtpServerHost;
            this.mailConfig.smtpServerPort = v.smtpServerPort;
            this.mailConfig.usesAuthentication = v.usesAuthentication;
            this.mailConfig.transportStrategy = v.transport;
            if (this.mailConfig.usesAuthentication) {
                this.mailConfig.smtpUsername = v.smtpUsername;
                if (this.mailConfig.smtpPassword !== v.smtpPassword) {
                    this.mailConfig.smtpPassword = v.smtpPassword;
                    this.mailConfig.smtpPassEncrypted = false;
                }
            }
            this.mailConfig.usesProxy = v.usesProxy;
            if (this.mailConfig.usesProxy) {
                this.mailConfig.proxyHost = v.proxyHost;
                this.mailConfig.proxyPort = v.proxyPort;
            }
            this.mailConfig.usesProxyAuthentication = v.usesProxyAuthentication;
            if (this.mailConfig.usesProxyAuthentication) {
                this.mailConfig.proxyUser = v.proxyUsername;
                if (this.mailConfig.proxyPassword !== v.proxyPassword) {
                    this.mailConfig.proxyPassword = v.proxyPassword;
                    this.mailConfig.proxyPassEncrypted = false;
                }
            }
            this.mailConfig.senderAddress = v.senderAddress;
            this.mailConfig.senderName = v.senderName;
            this.defaultRecipient = v.defaultRecipient;
        });
    }

    save() {
        this.mailConfig.testRecipientAddress = '';
        this.mailConfigService
            .updateMailConfig(this.mailConfig)
            .subscribe(() => {
                this.loadMailConfig();
            });
    }

    sendTestMail() {
        this.sendingEmailErrorMessage = '';
        this.attemptSendingTestMail = true;
        this.sendingTestMailInProgress = true;
        this.mailConfig.testRecipientAddress = this.defaultRecipient;
        this.mailConfigService.sendTestMail(this.mailConfig).subscribe(
            result => {
                this.sendingTestMailInProgress = false;
                this.sendingTestMailSuccess = true;
            },
            error => {
                this.sendingTestMailInProgress = false;
                this.sendingTestMailSuccess = false;
                this.sendingEmailErrorMessage = error.error.localizedMessage;
            },
        );
    }
}
