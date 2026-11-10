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
    ElementRef,
    EventEmitter,
    Input,
    NgZone,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
    inject,
    ChangeDetectionStrategy,
} from '@angular/core';
import { JsplumbBridge } from '../../../services/jsplumb-bridge.service';
import { PipelineComponent } from '../../pipeline/pipeline.component';
import {
    LivePreviewState,
    PipelineElementConfig,
    PipelineElementUnion,
} from '../../../model/editor.model';
import { PipelineAssemblyDrawingAreaPanZoomComponent } from './pipeline-assembly-drawing-area-pan-zoom/pipeline-assembly-drawing-area-pan-zoom.component';
import { PipelineValidationService } from '../../../services/pipeline-validation.service';
import {
    LivePreviewService,
    PipelineCanvasMetadata,
    PipelinePreviewModel,
    SpMetricsEntry,
} from '@streampipes/platform-services';
import { EditorService } from '../../../services/editor.service';
import { PipelinePositioningService } from '../../../services/pipeline-positioning.service';
import { HttpDownloadProgressEvent } from '@angular/common/http';
import { ObjectProvider } from '../../../services/object-provider.service';
import { Subscription } from 'rxjs';
import { ErrorHintComponent } from '../../../../core-ui/error-hint/error-hint.component';
import { LivePreviewStore } from '../../../services/live-preview.store';
import { LivePreviewPanelComponent } from '../../live-preview-panel/live-preview-panel.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';

/** A result stream that ends sooner than this was not cut off by its time limit. */
const MIN_STREAM_DURATION_MS = 5000;
/** Clock and network delay allowed when telling the backend's maximum duration apart from other stream ends. */
const MAX_DURATION_TOLERANCE_MS = 5000;

@Component({
    selector: 'sp-pipeline-assembly-drawing-area',
    templateUrl: './pipeline-assembly-drawing-area.component.html',
    styleUrls: ['./pipeline-assembly-drawing-area.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        ErrorHintComponent,
        LivePreviewPanelComponent,
        PipelineAssemblyDrawingAreaPanZoomComponent,
        PipelineComponent,
    ],
    providers: [LivePreviewStore],
})
export class PipelineAssemblyDrawingAreaComponent implements OnInit, OnDestroy {
    pipelineValidationService = inject(PipelineValidationService);
    private ngZone = inject(NgZone);
    private editorService = inject(EditorService);
    private pipelinePositioningService = inject(PipelinePositioningService);
    private livePreviewService = inject(LivePreviewService);
    private objectProvider = inject(ObjectProvider);
    private snackBar = inject(MatSnackBar);
    private translateService = inject(TranslateService);
    private livePreviewStore = inject(LivePreviewStore);

    @Input()
    jsplumbBridge: JsplumbBridge;

    @Input()
    allElements: PipelineElementUnion[];

    @Input()
    rawPipelineModel: PipelineElementConfig[];

    @Input()
    pipelineCanvasMetadata: PipelineCanvasMetadata;

    @Input()
    pipelineCanvasMetadataAvailable: boolean;

    @Input()
    readonly: boolean;

    @Input()
    metricsInfo: Record<string, SpMetricsEntry>;

    @Output()
    triggerPipelineCacheUpdateEmitter: EventEmitter<void> = new EventEmitter();

    @Output()
    previewStateChange: EventEmitter<LivePreviewState> = new EventEmitter();

    pipelineValid = false;
    previewState: LivePreviewState = 'off';
    pipelinePreview: PipelinePreviewModel;
    pipelinePreviewSub: Subscription;

    private restartPreviewPending = false;
    private restartPreviewTimer: ReturnType<typeof setTimeout>;
    private previewLimitTimer: ReturnType<typeof setTimeout>;
    private destroyed = false;
    /** Identifies the latest start request; responses to earlier ones are released. */
    private previewAttempt = 0;
    private readonly stopPreviewOnPageHide = () => this.stopPreviewOnUnload();

    @ViewChild('pipelineComponent')
    pipelineComponent: PipelineComponent;

    @ViewChild('zoomComponent')
    zoomComponent: PipelineAssemblyDrawingAreaPanZoomComponent;
    @ViewChild('outerCanvas') pipelineCanvas: ElementRef;

    ngOnInit(): void {
        // Angular does not destroy components when the page itself is left
        window.addEventListener('pagehide', this.stopPreviewOnPageHide);
        if (this.rawPipelineModel.length > 0) {
            this.displayPipelineInEditor(
                !this.pipelineCanvasMetadataAvailable,
                this.pipelineCanvasMetadata,
            );
        }
    }

    ngOnDestroy(): void {
        window.removeEventListener('pagehide', this.stopPreviewOnPageHide);
        this.destroyed = true;
        clearTimeout(this.restartPreviewTimer);
        this.stopPreview();
    }

    isPipelineAssemblyEmpty() {
        return (
            this.rawPipelineModel.length === 0 ||
            this.rawPipelineModel.every(pe => pe.settings.disabled)
        );
    }

    displayPipelineInEditor(
        autoLayout: boolean,
        pipelineCanvasMetadata?: PipelineCanvasMetadata,
    ): void {
        setTimeout(() => {
            this.pipelinePositioningService.displayPipeline(
                this.rawPipelineModel,
                '#assembly',
                false,
                autoLayout,
                pipelineCanvasMetadata,
            );

            this.editorService.makePipelineAssemblyEmpty(false);
            this.ngZone.run(() => {
                this.pipelineValid =
                    this.pipelineValidationService.isValidPipeline(
                        this.rawPipelineModel.filter(
                            pe => !pe.settings.disabled,
                        ),
                        false,
                    );
            });
            if (!this.readonly) {
                this.pipelineComponent.triggerPipelineModification();
            }
        });
    }

    togglePipelineElementLivePreview(): void {
        if (this.previewState === 'off') {
            this.startPreview();
        } else if (this.previewState === 'active') {
            this.stopPreview();
        }
    }

    /**
     * Called when the pipeline changed in a way that makes the running preview outdated,
     * e.g. an element was configured, added or removed. Changes in quick succession
     * result in a single restart.
     */
    restartPreviewIfActive(): void {
        if (this.previewState === 'off') {
            return;
        }
        clearTimeout(this.restartPreviewTimer);
        this.restartPreviewTimer = setTimeout(() => {
            if (this.previewState === 'starting') {
                this.restartPreviewPending = true;
            } else if (this.previewState === 'active') {
                this.stopPreview();
                this.startPreview();
            }
        }, 300);
    }

    private startPreview(): void {
        this.restartPreviewPending = false;
        const attempt = ++this.previewAttempt;
        this.setPreviewState('starting');
        const pipeline = this.objectProvider.makePipeline(
            this.rawPipelineModel,
        );
        this.editorService.initiatePipelinePreview(pipeline).subscribe({
            next: pipelinePreviewModel => {
                if (this.destroyed || attempt !== this.previewAttempt) {
                    // stopped, restarted or left while this preview was starting
                    this.releasePreview(pipelinePreviewModel.previewId);
                    return;
                }
                this.pipelinePreview = pipelinePreviewModel;
                this.livePreviewStore.start(
                    pipelinePreviewModel,
                    this.rawPipelineModel,
                );
                this.setPreviewState('active');
                this.subscribeToPreview(pipelinePreviewModel.previewId);
                this.stopAtMaximumDuration(pipelinePreviewModel);
                if (this.restartPreviewPending) {
                    this.restartPreviewIfActive();
                }
            },
            error: () => {
                if (attempt !== this.previewAttempt) {
                    return;
                }
                this.setPreviewState('off');
                this.notify(
                    this.translateService.instant(
                        "The live preview couldn't be started.",
                    ),
                );
            },
        });
    }

    private subscribeToPreview(previewId: string): void {
        const attachedAt = Date.now();
        this.pipelinePreviewSub = this.editorService
            .getPipelinePreviewResult(previewId)
            .subscribe({
                next: res => {
                    const data = this.livePreviewService.convert(
                        res as HttpDownloadProgressEvent,
                    );
                    if (data) {
                        this.livePreviewStore.ingest(data);
                    }
                },
                error: () =>
                    this.maximumDurationReached()
                        ? this.endPreviewAtMaximumDuration()
                        : this.endPreview(
                              this.translateService.instant(
                                  'The connection to the live preview was lost.',
                              ),
                          ),
                // Each result stream has a maximum duration as a safeguard against previews
                // nobody cleans up. While the panel is open, attach to the same preview again.
                complete: () => {
                    if (
                        this.pipelinePreview?.previewId !== previewId ||
                        this.destroyed
                    ) {
                        return;
                    }
                    if (this.maximumDurationReached()) {
                        this.endPreviewAtMaximumDuration();
                    } else if (
                        Date.now() - attachedAt <
                        MIN_STREAM_DURATION_MS
                    ) {
                        this.endPreview(
                            this.translateService.instant(
                                'The live preview has stopped. Turn it on again to keep watching live data.',
                            ),
                        );
                    } else {
                        this.subscribeToPreview(previewId);
                    }
                },
            });
    }

    closeLivePreview(): void {
        this.stopPreview();
    }

    /** The backend stops every preview after a maximum duration; end it here at the same time. */
    private stopAtMaximumDuration(preview: PipelinePreviewModel): void {
        clearTimeout(this.previewLimitTimer);
        if (preview.maxDurationMillis > 0) {
            this.previewLimitTimer = setTimeout(
                () => this.endPreviewAtMaximumDuration(),
                preview.maxDurationMillis,
            );
        }
    }

    private maximumDurationReached(): boolean {
        const expiresAt = this.livePreviewStore.expiresAt();
        return (
            expiresAt !== undefined &&
            Date.now() >= expiresAt - MAX_DURATION_TOLERANCE_MS
        );
    }

    private endPreviewAtMaximumDuration(): void {
        const minutes = Math.round(
            (this.pipelinePreview?.maxDurationMillis ?? 0) / 60000,
        );
        this.endPreview(
            this.translateService.instant(
                'The live preview stopped after {{minutes}} minutes. Turn it on again to keep watching live data.',
                { minutes },
            ),
        );
    }

    /**
     * Releases the preview with a request that survives the page unload. If the page only goes
     * into the back/forward cache, the panel is closed as well, since its stream is gone.
     */
    private stopPreviewOnUnload(): void {
        if (this.pipelinePreview) {
            this.editorService.releasePipelinePreviewOnUnload(
                this.pipelinePreview.previewId,
            );
            this.pipelinePreview = undefined;
        }
        // a preview still starting is released by the backend when no stream attaches to it
        this.stopPreview();
    }

    private stopPreview(): void {
        this.previewAttempt++;
        clearTimeout(this.previewLimitTimer);
        this.pipelinePreviewSub?.unsubscribe();
        this.pipelinePreviewSub = undefined;
        if (this.pipelinePreview) {
            this.releasePreview(this.pipelinePreview.previewId);
            this.pipelinePreview = undefined;
        }
        this.livePreviewStore.stop();
        // a preview that is still starting is released once its start request returns
        if (this.previewState !== 'off') {
            this.setPreviewState('off');
        }
    }

    private endPreview(message: string): void {
        this.stopPreview();
        this.notify(message);
    }

    private releasePreview(previewId: string): void {
        // the backend also releases the preview when the stream ends; this makes it immediate
        this.editorService.deletePipelinePreview(previewId).subscribe({
            error: () => undefined,
        });
    }

    private setPreviewState(state: LivePreviewState): void {
        this.previewState = state;
        if (!this.destroyed) {
            this.previewStateChange.emit(state);
        }
    }

    private notify(translatedMessage: string): void {
        if (!this.destroyed) {
            this.snackBar.open(
                translatedMessage,
                this.translateService.instant('Close'),
                { duration: 6000 },
            );
        }
    }

    resetZoom(): void {
        this.zoomComponent.resetZoom();
    }
}
