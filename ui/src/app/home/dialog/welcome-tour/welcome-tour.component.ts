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

import {
    DialogRef,
    DialogService,
    PanelType,
    SpAlertBannerComponent,
} from '@streampipes/shared-ui';
import { Component, Input, OnInit, inject } from '@angular/core';
import { AppConstants } from '../../../services/app.constants';
import { AuthService } from '../../../services/auth.service';
import {
    AdapterDescription,
    AdapterService,
    NamedStreamPipesEntity,
    PipelineElementService,
    UserAccount,
    UserInfo,
} from '@streampipes/platform-services';
import { ProfileService } from '../../../profile/profile.service';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MissingElementsForTutorialComponent } from '../../../editor/dialog/missing-elements-for-tutorial/missing-elements-for-tutorial.component';
import { forkJoin } from 'rxjs';
import { LayoutGapDirective } from '@ngbracket/ngx-layout';

@Component({
    selector: 'sp-welcome-tour',
    templateUrl: './welcome-tour.component.html',
    styleUrls: ['./welcome-tour.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        LayoutAlignDirective,
        MatButton,
        MatDivider,
        MatProgressSpinner,
        LayoutGapDirective,
        SpAlertBannerComponent,
    ],
})
export class WelcomeTourComponent implements OnInit {
    private static readonly REQUIRED_ADAPTER_FOR_TUTORIAL_APP_ID =
        'org.apache.streampipes.connect.iiot.adapters.simulator.machine';
    private static readonly REQUIRED_PROCESSOR_FOR_TUTORIAL_APP_ID =
        'org.apache.streampipes.processors.filters.jvm.numericalfilter';
    private static readonly REQUIRED_SINK_FOR_TUTORIAL_APP_ID =
        'org.apache.streampipes.sinks.internal.jvm.datalake';

    private authService = inject(AuthService);
    private dialogRef = inject<DialogRef<WelcomeTourComponent>>(DialogRef);
    private profileService = inject(ProfileService);
    private dialogService = inject(DialogService);
    private adapterService = inject(AdapterService);
    private pipelineElementService = inject(PipelineElementService);
    appConstants = inject(AppConstants);

    @Input()
    userInfo: UserInfo;

    currentUser: UserAccount;
    availablePipelineElements: NamedStreamPipesEntity[] = [];
    availableAdapters: AdapterDescription[] = [];
    missingElementsForTutorial = [];
    tutorialAvailable = false;
    loadingTutorialResources = true;

    ngOnInit(): void {
        this.profileService
            .getUserProfile(this.userInfo.username)
            .subscribe(data => {
                this.currentUser = data;
            });

        forkJoin([
            this.adapterService.getAdapterDescriptions(),
            this.pipelineElementService.getDataStreams(),
            this.pipelineElementService.getDataProcessors(),
            this.pipelineElementService.getDataSinks(),
        ]).subscribe(res => {
            this.availableAdapters = res[0];
            this.availablePipelineElements = []
                .concat(...res[1])
                .concat(...res[2])
                .concat(...res[3]);
            this.missingElementsForTutorial =
                this.getMissingElementsForTutorial();
            this.tutorialAvailable =
                this.missingElementsForTutorial.length === 0;
            this.loadingTutorialResources = false;
        });
    }

    hideTourForever() {
        this.currentUser.hideTutorial = true;
        this.profileService
            .updateUserProfile(this.currentUser)
            .subscribe(_data => {
                this.authService.updateTokenAndUserInfo();
                this.close();
            });
    }

    startTutorial() {
        if (this.loadingTutorialResources) {
            return;
        }

        if (this.tutorialAvailable) {
            this.close(true);
        } else {
            this.dialogService.open(MissingElementsForTutorialComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Tutorial requires pipeline elements',
                data: {
                    missingElementsForTutorial: this.missingElementsForTutorial,
                },
            });
        }
    }

    close(startTutorial = false) {
        this.dialogRef.close(startTutorial);
    }

    get showMissingElementsHint() {
        return !this.loadingTutorialResources && !this.tutorialAvailable;
    }

    private getMissingElementsForTutorial() {
        const missingElements = [];

        if (
            !this.requiredPeForTourPresent(
                this.availableAdapters,
                WelcomeTourComponent.REQUIRED_ADAPTER_FOR_TUTORIAL_APP_ID,
            )
        ) {
            missingElements.push({
                name: 'Machine Data Simulator',
                appId: WelcomeTourComponent.REQUIRED_ADAPTER_FOR_TUTORIAL_APP_ID,
            });
        }

        if (
            !this.requiredPeForTourPresent(
                this.availablePipelineElements,
                WelcomeTourComponent.REQUIRED_PROCESSOR_FOR_TUTORIAL_APP_ID,
            )
        ) {
            missingElements.push({
                name: 'Numerical Filter',
                appId: WelcomeTourComponent.REQUIRED_PROCESSOR_FOR_TUTORIAL_APP_ID,
            });
        }

        if (
            !this.requiredPeForTourPresent(
                this.availablePipelineElements,
                WelcomeTourComponent.REQUIRED_SINK_FOR_TUTORIAL_APP_ID,
            )
        ) {
            missingElements.push({
                name: 'Dashboard Sink',
                appId: WelcomeTourComponent.REQUIRED_SINK_FOR_TUTORIAL_APP_ID,
            });
        }

        return missingElements;
    }

    private requiredPeForTourPresent(
        list: Array<{ appId: string }>,
        appId: string,
    ) {
        return list?.some(el => el.appId === appId);
    }
}
