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

import { Component } from '@angular/core';
import { BaseWidgetConfig } from '../../base/base-widget-config';
import {
    ImageWidgetModel,
    ImageWidgetVisConfig,
} from '../model/image-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';
import { config } from 'rxjs';

@Component({
    selector: 'sp-data-explorer-image-widget-config',
    templateUrl: './image-widget-config.component.html',
    styleUrls: ['./image-widget-config.component.scss'],
})
export class ImageWidgetConfigComponent extends BaseWidgetConfig<
    ImageWidgetModel,
    ImageWidgetVisConfig
> {
    imageSemanticType = 'https://image.com';
    imageFields: DataExplorerField[];

    protected applyWidgetConfig(config: ImageWidgetVisConfig): void {
        this.imageFields = this.getImageFields();
        config.selectedField = this.fieldService.getSelectedField(
            config.selectedField,
            this.imageFields,
            () => this.imageFields[0],
        );
    }

    private getImageFields(): DataExplorerField[] {
        return this.fieldProvider.allFields.filter(field =>
            field.fieldCharacteristics.semanticTypes.find(
                st => st === this.imageSemanticType,
            ),
        );
    }

    setSelectedImageProperty(field: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.selectedField =
            field;
        this.triggerDataRefresh();
    }

    protected requiredFieldsForChartPresent(): boolean {
        return this.getImageFields().length > 0;
    }
}
