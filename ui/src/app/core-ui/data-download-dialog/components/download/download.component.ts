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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { DataExportService } from '../../services/data-export.service';
import { DownloadProgress } from '../../model/download-progress.model';
import { Subscription } from 'rxjs';

@Component({
    selector: 'sp-download',
    templateUrl: './download.component.html',
    styleUrls: ['./download.component.scss'],
})
export class DownloadComponent implements OnInit, OnDestroy {
    downloadProgress: DownloadProgress;
    downloadProgressSubscription: Subscription;

    constructor(public dataExportService: DataExportService) {}

    ngOnInit(): void {
        this.downloadProgress = {
            downloadedMBs: 0,
            finished: false,
        };

        this.downloadProgressSubscription =
            this.dataExportService.updateDownloadProgress.subscribe(
                progress => {
                    this.downloadProgress = progress;
                },
            );
    }

    ngOnDestroy(): void {
        this.downloadProgressSubscription.unsubscribe();
    }

    cancelDownload() {
        // TODO implement
    }
}
