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
import { FileRestService}  from './service/filerest.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'sp-file-management',
  templateUrl: './file-management.component.html',
  styleUrls: ['./file-management.component.css']
})
export class FileManagementComponent implements OnInit {

  selectedUploadFile: File;
  fileName;

  urls;

  constructor(
      private restService: FileRestService,
      public snackBar: MatSnackBar,) { }

  ngOnInit() {
      this.getURLS();
  }

  getURLS() {
    this.restService.getURLS().subscribe(
        result => {
            this.urls = result;
        },
        error => {
            this.openSnackBar('Error while getting uploaded files', 'Ok');
        },
    );

  }

  delete(name: string) {
      // TODO: AppId
      this.restService.deleteFile('', name).subscribe(
          result => {
              this.openSnackBar('Deleted successful', 'Ok');
              this.getURLS();
          },
          error => {
              this.openSnackBar('Error while deleting file', 'Ok');
          },
      );
  }


  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
        duration: 5000,
        horizontalPosition: 'right',
        verticalPosition: 'top'
    });
  }

  copyText(val: string){
      let selBox = document.createElement('textarea');
      selBox.style.position = 'fixed';
      selBox.style.left = '0';
      selBox.style.top = '0';
      selBox.style.opacity = '0';
      selBox.value = val;
      document.body.appendChild(selBox);
      selBox.focus();
      selBox.select();
      document.execCommand('copy');
      document.body.removeChild(selBox);
      this.openSnackBar('Copied URL to Clipboard', 'Ok');
  }



}
