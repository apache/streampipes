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

import { Component, inject, signal } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-upload-sample-event-dialog',
    templateUrl: './upload-sample-event-dialog.component.html',
    standalone: false,
})
export class UploadSampleEventDialogComponent {
    private dialogRef = inject(DialogRef<UploadSampleEventDialogComponent>);

    samplePayload = signal('');

    isSampleValid(): boolean {
        const trimmed = this.samplePayload().trim();
        if (!trimmed) {
            return false;
        }
        try {
            JSON.parse(trimmed);
            return true;
        } catch {
            return false;
        }
    }

    isSampleInvalid(): boolean {
        const trimmed = this.samplePayload().trim();
        return trimmed.length > 0 && !this.isSampleValid();
    }

    submit(): void {
        const trimmed = this.samplePayload().trim();
        if (!trimmed || !this.isSampleValid()) {
            return;
        }
        this.dialogRef.close(trimmed);
    }

    close(): void {
        this.dialogRef.close();
    }
}
