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

import { Component, OnInit, inject } from '@angular/core';
import { FileMetadata, FilesService } from '@streampipes/platform-services';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatTableDataSource,
} from '@angular/material/table';
import {
    ConfirmDialogComponent,
    SpLabelComponent,
    SpTableComponent,
} from '@streampipes/shared-ui';
import { MatDialog } from '@angular/material/dialog';
import { saveAs } from 'file-saver';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { DatePipe } from '@angular/common';

@Component({
    selector: 'sp-file-overview',
    templateUrl: './file-overview.component.html',
    styleUrls: ['./file-overview.component.scss'],
    imports: [
        SpTableComponent,
        FlexDirective,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatCellDef,
        MatCell,
        SpLabelComponent,
        LayoutDirective,
        LayoutAlignDirective,
        MatIconButton,
        MatTooltip,
        DatePipe,
        TranslatePipe,
    ],
})
export class FileOverviewComponent implements OnInit {
    private filesService = inject(FilesService);
    private dialog = inject(MatDialog);
    private translateService = inject(TranslateService);

    displayedColumns: string[] = ['filename', 'filetype', 'uploaded', 'action'];

    dataSource: MatTableDataSource<FileMetadata> = new MatTableDataSource();
    filesAvailable = false;

    private fileTypeColors: { [key: string]: string } = {};

    ngOnInit() {
        this.refreshFiles();
    }

    refreshFiles() {
        this.filesService.getFileMetadata().subscribe(fm => {
            this.dataSource.data = fm;
            this.filesAvailable = fm && fm.length > 0;
        });
    }

    deleteFile(fileMetadata: FileMetadata) {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            data: {
                title: this.translateService.instant(
                    'Do you really want to delete this file?',
                ),
                subtitle: this.translateService.instant(
                    'This cannot be undone.',
                ),
                cancelTitle: this.translateService.instant('No'),
                confirmTitle: this.translateService.instant('Yes'),
            },
        });

        dialogRef.afterClosed().subscribe(ev => {
            if (ev === 'confirm') {
                this.filesService
                    .deleteFile(fileMetadata.fileId)
                    .subscribe(_response => {
                        this.refreshFiles();
                    });
            }
        });
    }

    downloadFile(fileMetadata: FileMetadata) {
        this.filesService.getFile(fileMetadata.filename).subscribe(response => {
            saveAs(response, fileMetadata.filename);
        });
    }

    getFileColor(fileType: string) {
        if (!this.fileTypeColors.hasOwnProperty(fileType)) {
            this.fileTypeColors[fileType] = this.generateColorHash(fileType);
        }

        return this.fileTypeColors[fileType];
    }

    private generateColorHash(fileType: string) {
        let hash = 0;

        fileType.split('').forEach(char => {
            hash = char.charCodeAt(0) + ((hash << 5) - hash);
        });

        const color = (Math.abs(hash) & 0x00ffffff).toString(16).toUpperCase();
        const paddedColor = color.padStart(6, '0');

        return `#${paddedColor}`;
    }
}
