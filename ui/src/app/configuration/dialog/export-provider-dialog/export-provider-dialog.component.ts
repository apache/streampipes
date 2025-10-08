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

import { Component, Input } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import {
    AwsRegion,
    ExportProviderSettings,
} from '@streampipes/platform-services';
import { ExportProviderService } from 'projects/streampipes/platform-services/src/lib/apis/export-provider.service';

@Component({
    selector: 'sp-export-provider-dialog',
    templateUrl: './export-provider-dialog.component.html',
    standalone: false,
})
export class ExportProviderComponent {
    exportProviderSetting: ExportProviderSettings = {
        providerType: 'FOLDER',
        accessKey: '',
        secretKey: '',
        bucketName: '',
        endPoint: '',
        providerId: 'us-east-1',
        awsRegion: 'us-east-1',
        secretEncrypted: false,
    };

    awsRegions: AwsRegion[] = [
        'us-east-1',
        'us-east-2',
        'us-west-1',
        'us-west-2',
        'ca-central-1',
        'ca-west-1',
        'eu-north-1',
        'eu-west-1',
        'eu-west-2',
        'eu-west-3',
        'eu-central-1',
        'eu-south-1',
        'eu-south-2',
        'eu-central-2',
        'ap-south-1',
        'ap-east-1',
        'ap-northeast-1',
        'ap-northeast-2',
        'ap-northeast-3',
        'ap-southeast-1',
        'ap-southeast-2',
        'ap-southeast-3',
        'sa-east-1',
        'me-south-1',
        'me-central-1',
        'us-gov-east-1',
        'us-gov-west-1',
    ];

    constructor(
        private dialogRef: DialogRef<ExportProviderComponent>,
        private exportProviderRestService: ExportProviderService,
    ) {}

    close(refreshDataLakeIndex: boolean) {
        this.dialogRef.close(refreshDataLakeIndex);
    }

    addData() {
        this.exportProviderSetting.providerId = this.makeProviderId();

        this.exportProviderRestService.updateExportProvider(
            this.exportProviderSetting,
        );

        this.dialogRef.close();
    }

    private makeProviderId(): string {
        return 'p' + Math.random().toString(36).substring(2, 9);
    }
}
