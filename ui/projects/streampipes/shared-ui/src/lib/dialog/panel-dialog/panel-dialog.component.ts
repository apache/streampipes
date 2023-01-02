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
    HostBinding,
    HostListener,
    OnInit,
    Output,
    ViewEncapsulation,
} from '@angular/core';
import {
    animate,
    state,
    style,
    transition,
    trigger,
} from '@angular/animations';
import { BaseDialogComponent } from '../base-dialog/base-dialog.component';

@Component({
    selector: 'app-dialog-container',
    templateUrl: './panel-dialog.component.html',
    encapsulation: ViewEncapsulation.None,
    styleUrls: ['./panel-dialog.component.scss'],
    animations: [
        trigger('flyInOut', [
            state(
                'void',
                style({
                    transform: 'translateX(80vw)',
                }),
            ),
            state(
                'in',
                style({
                    transform: 'translateX(0)',
                }),
            ),
            state(
                'out',
                style({
                    transform: 'translateX(80vw)',
                }),
            ),
            transition('* => *', animate(300)),
        ]),
    ],
})
export class PanelDialogComponent<T>
    extends BaseDialogComponent<T>
    implements OnInit
{
    constructor() {
        super();
    }

    @HostBinding('@flyInOut') slideDown = 'in';

    @Output()
    animationStateChanged = new EventEmitter<AnimationEvent>();

    @HostListener('@flyInOut.done', ['$event']) startDrawerHandler(
        event: any,
    ): void {
        if (event.toState === 'out') {
            this.containerEvent.emit({ key: 'CLOSE' });
        }
    }

    ngOnInit() {}

    closeDialog() {
        this.slideDown = 'out';
    }
}
