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
    AfterViewInit,
    Component,
    ElementRef,
    Input,
    OnDestroy,
    OnInit,
} from '@angular/core';
import { JsplumbBridge } from '../../../../services/jsplumb-bridge.service';
import Panzoom, { PanzoomObject } from '@panzoom/panzoom';
import { Subscription } from 'rxjs';
import { PipelineElementDraggedService } from '../../../../services/pipeline-element-dragged.service';

@Component({
    selector: 'sp-pipeline-assembly-drawing-area-pan-zoom',
    templateUrl: './pipeline-assembly-drawing-area-pan-zoom.component.html',
    styleUrls: ['./pipeline-assembly-drawing-area-pan-zoom.component.scss'],
})
export class PipelineAssemblyDrawingAreaPanZoomComponent
    implements OnInit, AfterViewInit, OnDestroy
{
    @Input()
    jsplumbBridge: JsplumbBridge;

    @Input()
    pipelineCanvas: ElementRef;

    panzoom: PanzoomObject;
    moveSub: Subscription;
    currentZoomLevel = 1;

    constructor(
        private pipelineElementDraggedService: PipelineElementDraggedService,
    ) {}

    ngOnInit() {
        this.moveSub =
            this.pipelineElementDraggedService.pipelineElementMovedSubject.subscribe(
                position => {
                    const offsetHeight =
                        this.pipelineCanvas.nativeElement.offsetHeight;
                    const offsetWidth =
                        this.pipelineCanvas.nativeElement.offsetWidth;
                    const currentPan = this.panzoom.getPan();
                    let xOffset = 0;
                    let yOffset = 0;
                    if (position.y + currentPan.y > offsetHeight - 100) {
                        yOffset = -10;
                    }
                    if (position.x + currentPan.x > offsetWidth - 100) {
                        xOffset = -10;
                    }
                    if (xOffset < 0 || yOffset < 0) {
                        this.pan(xOffset, yOffset);
                    }
                },
            );
    }

    ngAfterViewInit() {
        const elem = document.getElementById('assembly');
        this.panzoom = Panzoom(elem, {
            maxScale: 5,
            excludeClass: 'sp-no-pan',
            canvas: true,
            contain: 'outside',
        });
    }

    zoomOut() {
        this.doZoom(true);
    }

    zoomIn() {
        this.doZoom(false);
    }

    doZoom(zoomOut) {
        zoomOut ? this.panzoom.zoomOut() : this.panzoom.zoomIn();
        this.currentZoomLevel = this.panzoom.getScale();
        this.jsplumbBridge.setZoom(this.currentZoomLevel);
        this.jsplumbBridge.repaintEverything();
    }

    panLeft() {
        this.pan(100, 0);
    }

    panRight() {
        this.pan(-100, 0);
    }

    panUp() {
        this.pan(0, 100);
    }

    panDown() {
        this.pan(0, -100);
    }

    panHome() {
        this.panAbsolute(0, 0);
    }

    pan(xOffset: number, yOffset: number) {
        const currentPan = this.panzoom.getPan();
        const panX = Math.min(0, currentPan.x + xOffset);
        const panY = Math.min(0, currentPan.y + yOffset);
        this.panzoom.pan(panX, panY);
    }

    panAbsolute(x: number, y: number) {
        this.panzoom.pan(x, y);
    }

    resetZoom(): void {
        this.currentZoomLevel = 1;
        this.jsplumbBridge.setZoom(this.currentZoomLevel);
    }

    ngOnDestroy() {
        this.moveSub?.unsubscribe();
    }
}
