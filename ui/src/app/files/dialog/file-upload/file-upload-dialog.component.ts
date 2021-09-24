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

import { Component, OnInit } from '@angular/core';
import { DialogRef } from '../../../core-ui/dialog/base-dialog/dialog-ref';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { FilesService } from '../../../platform-services/apis/files.service';

@Component({
  selector: 'file-upload-dialog-component',
  templateUrl: './file-upload-dialog.component.html',
  styleUrls: ['./file-upload-dialog.component.scss']
})
export class FileUploadDialogComponent implements OnInit {

  inputValue: string;
  fileName: string;

  selectedUploadFile: File;

  hasInput: boolean;
  errorMessage = 'Please enter a value';

  uploadStatus = 0;

  constructor(private dialogRef: DialogRef<FileUploadDialogComponent>,
              private filesService: FilesService) {

  }

  ngOnInit(): void {
  }

  handleFileInput(files: any) {
    this.selectedUploadFile = files[0];
    this.fileName = this.selectedUploadFile.name;
    this.uploadStatus = 0;
  }

  store() {
    this.uploadStatus = 0;
    if (this.selectedUploadFile !== undefined) {
      this.filesService.uploadFile(this.selectedUploadFile).subscribe(
          event => {
            if (event.type === HttpEventType.UploadProgress) {
              this.uploadStatus = Math.round(100 * event.loaded / event.total);
            } else if (event instanceof HttpResponse) {
              this.dialogRef.close();
            }
          },
          error => {
          },
      );
    }
  }

  cancel() {
    this.dialogRef.close();
  }

}
