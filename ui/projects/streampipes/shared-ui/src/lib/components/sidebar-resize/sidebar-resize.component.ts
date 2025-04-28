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
    Output,
    WritableSignal,
    Input,
} from '@angular/core';
import { CdkDragMove } from '@angular/cdk/drag-drop';

@Component({
    selector: 'sidebar-resize',
    templateUrl: './sidebar-resize.component.html',
    styleUrls: ['./sidebar-resize.component.scss'],
})
export class SidebarResizeComponent {
    @Input() currentWidth: WritableSignal<number>;
    @Input() minWidth: number = 450;
    @Input() maxWidth: number = 1000;

    @Output() currentWidthChanged = new EventEmitter<number>();

    protected onDragMoved(event: CdkDragMove) {
        const deltaX = -event.distance.x * 0.4;
        const newWidth = Math.min(
            Math.max(this.currentWidth() + deltaX, this.minWidth),
            this.maxWidth,
        );

        const element = event.source.element.nativeElement;
        element.style.transform = 'none';

        this.currentWidthChanged.emit(newWidth);
    }
}
