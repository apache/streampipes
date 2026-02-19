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
    Component,
    EventEmitter,
    inject,
    Input,
    OnInit,
    Output,
} from '@angular/core';
import {
    DataExplorerWidgetModel,
    TimeSettings,
    UserInfo,
} from '@streampipes/platform-services';
import {
    CurrentUserService,
    TimeRangeSelectorComponent,
} from '@streampipes/shared-ui';
import { UserRole } from '../../../../_enums/user-role.enum';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-chart-view-toolbar',
    templateUrl: './chart-view-toolbar.component.html',
    styleUrls: ['../chart-view.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        MatFormField,
        MatInput,
        FormsModule,
        MatButton,
        MatTooltip,
        MatIcon,
        MatIconButton,
        TimeRangeSelectorComponent,
        TranslatePipe,
    ],
})
export class ChartViewToolbarComponent implements OnInit {
    private readonly currentUserService = inject(CurrentUserService);

    @Input()
    editMode = true;

    @Input()
    timeSettings: TimeSettings;

    @Input()
    configuredWidget: DataExplorerWidgetModel;

    timeRangeVisible = true;

    @Output()
    saveDataViewEmitter: EventEmitter<void> = new EventEmitter();

    @Output()
    addToAssetEmitter: EventEmitter<void> = new EventEmitter();

    @Output()
    discardDataViewEmitter: EventEmitter<void> = new EventEmitter();

    @Output()
    updateDateRangeEmitter: EventEmitter<TimeSettings> = new EventEmitter();

    @Output()
    downloadFileEmitter: EventEmitter<void> = new EventEmitter();

    currentUser: UserInfo;
    isAssetAdmin = false;

    ngOnInit() {
        this.currentUser = this.currentUserService.getCurrentUser();
        this.isAssetAdmin = this.currentUserService.hasRole(
            UserRole.ROLE_ASSET_ADMIN,
        );
    }
}
