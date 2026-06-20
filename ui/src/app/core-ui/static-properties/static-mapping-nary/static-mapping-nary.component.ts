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
import { StaticMappingComponent } from '../static-mapping/static-mapping';
import { MappingPropertyNary } from '@streampipes/platform-services';
import { DisplayRecommendedPipe } from '../filter/display-recommended.pipe';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-app-static-mapping-nary',
    templateUrl: './static-mapping-nary.component.html',
    styleUrls: ['./static-mapping-nary.component.scss'],
    imports: [
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        MatButton,
        MatCheckbox,
        FormsModule,
        TranslatePipe,
        DisplayRecommendedPipe,
    ],
})
export class StaticMappingNaryComponent
    extends StaticMappingComponent<MappingPropertyNary>
    implements OnInit
{
    private displayRecommendedPipe = inject(DisplayRecommendedPipe);

    ngOnInit() {
        this.extractPossibleSelections();
        if (!this.staticProperty.selectedProperties) {
            this.selectNone();
        } else {
            const recommendedProperties = this.displayRecommendedPipe.transform(
                this.availableProperties,
                this.staticProperty.propertyScope,
                this.displayRecommended,
            );
            recommendedProperties.forEach(ep => {
                if (
                    this.staticProperty.selectedProperties.indexOf(
                        ep.propertySelector,
                    ) > -1
                ) {
                    ep['checked'] = true;
                }
            });
        }
    }

    selectOption(property: any) {
        if (property['checked']) {
            this.addProperty(property);
        } else {
            this.staticProperty.selectedProperties.splice(
                this.staticProperty.selectedProperties.indexOf(
                    this.makeSelector(property),
                ),
                1,
            );
            property['checked'] = false;
        }
    }

    addProperty(property: any) {
        if (
            this.staticProperty.selectedProperties.indexOf(
                property.propertySelector,
            ) < 0
        ) {
            this.staticProperty.selectedProperties.push(
                this.makeSelector(property),
            );
        }
    }

    makeSelector(property: any) {
        return property.propertySelector;
    }

    selectAll() {
        this.selectNone();
        const recommendedProperties = this.displayRecommendedPipe.transform(
            this.availableProperties,
            this.staticProperty.propertyScope,
            this.displayRecommended,
        );
        recommendedProperties.forEach(ep => {
            ep['checked'] = true;
            this.addProperty(ep);
        });
    }

    selectNone() {
        this.staticProperty.selectedProperties = [];
        this.availableProperties.forEach(ep => {
            ep['checked'] = false;
        });
    }

    onStatusChange(_status: any) {}

    onValueChange(_value: any) {
        this.applyCompletedConfiguration();
    }
}
