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
    inject,
    Input,
    OnInit,
    ChangeDetectionStrategy,
} from '@angular/core';
import { DialogRef, SpSpinnerComponent } from '@streampipes/shared-ui';
import { DatalakeRestService } from '@streampipes/platform-services';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { catchError, from, map, mergeMap, of, toArray } from 'rxjs';
import { MatDivider } from '@angular/material/divider';

@Component({
    selector: 'sp-delete-dataset-dialog',
    templateUrl: './delete-dataset-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        LayoutDirective,
        FlexDirective,
        LayoutAlignDirective,
        MatButton,
        SpSpinnerComponent,
        MatDivider,
        TranslatePipe,
    ],
})
export class DeleteDatasetDialogComponent implements OnInit {
    @Input()
    datasetName: string;

    @Input()
    deleteDialog: boolean;

    @Input()
    datasetNames: string[];

    @Input()
    skippedDatasetNames: string[] = [];

    failedDatasetNames: string[] = [];
    hasChanges = false;
    isInProgress = false;
    currentStatus: any;

    private dialogRef = inject(DialogRef<DeleteDatasetDialogComponent>);
    private datalakeRestService = inject(DatalakeRestService);
    private translateService = inject(TranslateService);

    confirmDeleteMessage = '';
    confirmTruncateMessage = '';

    ngOnInit() {
        this.datasetNames ??= [this.datasetName];
        this.confirmDeleteMessage = this.translateService.instant(
            this.datasetName
                ? 'Do you really want to delete the dataset {{index}}?'
                : 'Delete the selected datasets?',
            { index: this.datasetName },
        );
        this.confirmTruncateMessage = this.translateService.instant(
            this.datasetName
                ? 'Do you really want to truncate the data in {{index}}?'
                : 'Truncate all data in the selected datasets?',
            { index: this.datasetName },
        );
    }

    close(refreshDataLakeIndex: boolean) {
        this.dialogRef.close(refreshDataLakeIndex || this.hasChanges);
    }

    truncateData() {
        this.execute(false);
    }

    deleteData() {
        this.execute(true);
    }

    private execute(deleteDatasets: boolean): void {
        if (this.isInProgress || !this.datasetNames.length) {
            return;
        }
        this.isInProgress = true;
        this.failedDatasetNames = [];
        this.currentStatus = this.translateService.instant(
            deleteDatasets ? 'Deleting data...' : 'Truncating data...',
        );
        from(this.datasetNames)
            .pipe(
                mergeMap(
                    name =>
                        (deleteDatasets
                            ? this.datalakeRestService.dropSingleMeasurementSeries(
                                  name,
                              )
                            : this.datalakeRestService.removeData(name)
                        ).pipe(
                            map(() => ({ name, success: true })),
                            catchError(() => of({ name, success: false })),
                        ),
                    4,
                ),
                toArray(),
            )
            .subscribe(results => {
                this.isInProgress = false;
                this.hasChanges ||= results.some(result => result.success);
                this.failedDatasetNames = results
                    .filter(result => !result.success)
                    .map(result => result.name);
                if (!this.failedDatasetNames.length) {
                    this.close(true);
                } else {
                    this.datasetNames = [...this.failedDatasetNames];
                }
            });
    }
}
