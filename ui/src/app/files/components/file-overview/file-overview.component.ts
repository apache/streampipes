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

import { Component, OnInit, ViewChild } from '@angular/core';
import { FilesService, FileMetadata } from '@streampipes/platform-services';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { ConfirmDialogComponent } from '@streampipes/shared-ui';
import { MatDialog } from '@angular/material/dialog';
import { saveAs } from 'file-saver';

@Component({
    selector: 'sp-file-overview',
    templateUrl: './file-overview.component.html',
    styleUrls: ['./file-overview.component.scss'],
})
export class FileOverviewComponent implements OnInit {
    displayedColumns: string[] = ['filename', 'filetype', 'uploaded', 'action'];

    dataSource: MatTableDataSource<FileMetadata>;
    filesAvailable = false;

    paginator: MatPaginator;
    pageSize = 1;

    private fileTypeColors: { [key: string]: string } = {};

    constructor(
        private filesService: FilesService,
        private dialog: MatDialog,
    ) {}

    ngOnInit() {
        this.dataSource = new MatTableDataSource<FileMetadata>([]);
        this.refreshFiles();
    }

    refreshFiles() {
        this.filesService.getFileMetadata().subscribe(fm => {
            this.dataSource.data = fm;
            this.filesAvailable = fm && fm.length > 0;
            setTimeout(() => {
                this.dataSource.paginator = this.paginator;
            });
        });
    }

    deleteFile(fileMetadata: FileMetadata) {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            data: {
                title: 'Do you really want to delete this file?',
                subtitle: 'This cannot be undone.',
                cancelTitle: 'No',
                okTitle: 'Yes',
                confirmAndCancel: true,
            },
        });

        dialogRef.afterClosed().subscribe(ev => {
            if (ev) {
                this.filesService
                    .deleteFile(fileMetadata.fileId)
                    .subscribe(response => {
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

    @ViewChild(MatPaginator) set content(paginator: MatPaginator) {
        this.paginator = paginator;
    }
}
