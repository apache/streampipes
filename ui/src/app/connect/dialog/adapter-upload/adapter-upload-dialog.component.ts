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
import { RestService } from '../../services/rest.service';
import { DialogRef } from '@streampipes/shared-ui';

@Component({
  selector: 'sp-dialog-adapter-started-dialog',
  templateUrl: './adapter-upload-dialog.html',
  styleUrls: ['./adapter-upload-dialog.component.scss']
})
export class AdapterUploadDialog implements OnInit {

  private selectedUploadFile: File;
  uploaded = false;

  constructor(private dialogRef: DialogRef<AdapterUploadDialog>,
              private restService: RestService) {

  }

  ngOnInit() {
  }

  handleFileInput(files: any) {
    this.selectedUploadFile = files[0];

    const fileReader = new FileReader();
    fileReader.onload = (e) => {
      this.uploaded = true;

      const jsonString: any = fileReader.result;
      const allTemplates: any[] = JSON.parse(jsonString);

      // allTemplates.forEach(adapterTemplate => {
      //     this.restService.addAdapterTemplate(adapterTemplate).subscribe(x => {
      //     });
      // });

    };
    fileReader.readAsText(this.selectedUploadFile);
  }

  onCloseConfirm() {
    this.dialogRef.close();
  }

}
