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
    ConnectedPosition,
    CdkConnectedOverlay,
    CdkOverlayOrigin,
} from '@angular/cdk/overlay';
import {
    Component,
    EventEmitter,
    Input,
    Output,
    ViewChild,
} from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

export interface SpSplitButtonAction {
    label: string;
    action: string;
    icon?: string;
    disabled?: boolean;
}

@Component({
    selector: 'sp-split-button',
    templateUrl: './split-button.component.html',
    styleUrls: ['./split-button.component.scss'],
    imports: [
        CdkOverlayOrigin,
        CdkConnectedOverlay,
        MatButton,
        MatIcon,
        TranslatePipe,
    ],
})
export class SpSplitButtonComponent {
    @Input() label = '';
    @Input() icon?: string;
    @Input() actions: SpSplitButtonAction[] = [];
    @Input() appearance: 'primary' | 'mat-basic' = 'primary';
    @Input() disabled = false;
    @Input() menuDisabled = false;
    @Input() buttonType: 'button' | 'submit' | 'reset' = 'button';
    @Input() menuAriaLabel = 'Additional actions';

    @Output() primaryAction = new EventEmitter<void>();
    @Output() actionSelected = new EventEmitter<SpSplitButtonAction>();

    @ViewChild(CdkOverlayOrigin) overlayOrigin?: CdkOverlayOrigin;

    menuOpen = false;
    menuWidth = 0;

    readonly overlayPositions: ConnectedPosition[] = [
        {
            originX: 'start',
            originY: 'bottom',
            overlayX: 'start',
            overlayY: 'top',
            offsetY: 0,
        },
        {
            originX: 'end',
            originY: 'bottom',
            overlayX: 'end',
            overlayY: 'top',
            offsetY: 0,
        },
    ];

    onPrimaryActionClicked(): void {
        this.closeMenu();
        this.primaryAction.emit();
    }

    onSplitActionClicked(action: SpSplitButtonAction): void {
        this.closeMenu();
        this.actionSelected.emit(action);
    }

    toggleMenu(event: MouseEvent): void {
        event.stopPropagation();

        if (this.isDropdownDisabled()) {
            return;
        }

        this.menuWidth =
            this.overlayOrigin?.elementRef.nativeElement.offsetWidth ?? 0;
        this.menuOpen = !this.menuOpen;
    }

    closeMenu(): void {
        this.menuOpen = false;
    }

    isDropdownDisabled(): boolean {
        return this.disabled || this.menuDisabled || this.actions.length === 0;
    }
}
