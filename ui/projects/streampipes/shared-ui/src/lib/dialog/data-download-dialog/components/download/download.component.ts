/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import {
    Component,
    OnDestroy,
    OnInit,
    inject,
    ChangeDetectionStrategy,
} from '@angular/core';
import { DataExportService } from '../../services/data-export.service';
import {
    DownloadProgress,
    FailedDownload,
} from '../../model/download-progress.model';
import { Subscription } from 'rxjs';
import { MatStepLabel } from '@angular/material/stepper';
import { DownloadStatusComponent } from '../download-status/download-status.component';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'sp-download',
    templateUrl: './download.component.html',
    styleUrls: ['./download.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [MatStepLabel, DownloadStatusComponent, TranslatePipe],
})
export class DownloadComponent implements OnInit, OnDestroy {
    dataExportService = inject(DataExportService);
    private translateService = inject(TranslateService);

    downloadProgress: DownloadProgress;
    downloadProgressSubscription: Subscription;

    downloading = true;
    multipleFiles = false;
    allFailed = false;
    failedDownloads: FailedDownload[] = [];
    fileProgressText = '';
    cancelledText = '';
    partialFailureText = '';
    summaryText = '';

    ngOnInit(): void {
        this.applyProgress({
            downloadedMBs: 0,
            finished: false,
        });

        this.downloadProgressSubscription =
            this.dataExportService.updateDownloadProgress.subscribe(progress =>
                this.applyProgress(progress),
            );
    }

    ngOnDestroy(): void {
        this.downloadProgressSubscription.unsubscribe();
    }

    cancelDownload() {
        this.dataExportService.cancelDownload();
    }

    private applyProgress(progress: DownloadProgress): void {
        this.downloadProgress = progress;
        const completed = progress.completedFiles ?? 0;
        const total = progress.totalFiles ?? 1;

        this.downloading = !progress.finished && !progress.cancelled;
        this.multipleFiles = total > 1;
        this.failedDownloads = progress.failedDownloads ?? [];
        this.allFailed =
            progress.finished && this.failedDownloads.length > 0 && !completed;

        this.fileProgressText = this.translateService.instant(
            'Downloading dataset {{current}} of {{total}}: {{name}}',
            {
                current: completed + 1,
                total,
                name: progress.currentMeasurement,
            },
        );
        this.cancelledText = this.translateService.instant(
            'Download cancelled after {{completed}} of {{total}} files',
            { completed, total },
        );
        this.partialFailureText = this.translateService.instant(
            'Could not download: {{names}}',
            {
                names: this.failedDownloads
                    .map(failed => `${failed.measurement} (${failed.reason})`)
                    .join(', '),
            },
        );
        this.summaryText = this.translateService.instant(
            '{{completed}} of {{total}} datasets downloaded',
            { completed, total },
        );
    }
}
