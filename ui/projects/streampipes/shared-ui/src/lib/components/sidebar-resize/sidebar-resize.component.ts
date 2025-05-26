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

import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { CdkDragMove, CdkDragStart } from '@angular/cdk/drag-drop';
import { MatDrawerContainer } from '@angular/material/sidenav';

@Component({
    selector: 'sp-sidebar-resize',
    templateUrl: './sidebar-resize.component.html',
    styleUrls: ['./sidebar-resize.component.scss'],
    standalone: false,
})
export class SidebarResizeComponent {
    @Input() currentWidth: number = 450;
    @Input() minWidth: number = 450;
    @Input() maxWidth: number = 1000;

    @Output() widthChanged = new EventEmitter<number>();

    private drawerContainer = inject(MatDrawerContainer);

    isDragging = false;
    ghostLeft = 0;
    startX = 0;
    startWidth = 0;

    protected onDragStarted(event: CdkDragStart) {
        this.isDragging = true;

        const element = event.source.element.nativeElement.parentElement;
        const rect = element.getBoundingClientRect();

        this.startX = rect.left;
        this.startWidth = this.currentWidth;

        this.ghostLeft = this.startWidth;
    }

    protected onDragMoved(event: CdkDragMove) {
        const deltaX = -event.distance.x;
        this.ghostLeft = Math.min(
            Math.max(this.startWidth + deltaX, this.minWidth),
            this.maxWidth,
        );
        const element = event.source.element.nativeElement;
        element.style.transform = 'none';
    }

    protected onDragEnded() {
        this.isDragging = false;
        this.currentWidth = this.ghostLeft;
        this.widthChanged.emit(this.currentWidth);
        setTimeout(() => this.drawerContainer.updateContentMargins(), 0);
    }
}
