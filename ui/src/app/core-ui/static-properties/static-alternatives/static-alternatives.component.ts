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
    ChangeDetectorRef,
    Component,
    EventEmitter,
    OnInit,
    Output,
} from '@angular/core';
import { AbstractStaticPropertyRenderer } from '../base/abstract-static-property';
import {
    StaticPropertyAlternative,
    StaticPropertyAlternatives,
} from '@streampipes/platform-services';
import { ConfigurationInfo } from '../../../connect/model/ConfigurationInfo';
import { MatRadioChange } from '@angular/material/radio';

@Component({
    selector: 'sp-app-static-alternatives',
    templateUrl: './static-alternatives.component.html',
    styleUrls: ['./static-alternatives.component.css'],
})
export class StaticAlternativesComponent
    extends AbstractStaticPropertyRenderer<StaticPropertyAlternatives>
    implements OnInit
{
    @Output() inputEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    completedStaticProperty: ConfigurationInfo;
    dependentStaticPropertyIds: Map<string, boolean> = new Map<
        string,
        boolean
    >();

    constructor(private changeDetectorRef: ChangeDetectorRef) {
        super();
    }

    ngOnInit() {
        this.staticProperty.alternatives.forEach(al => {
            if (al.staticProperty) {
                const configuration = al.staticProperty.internalName;
                this.dependentStaticPropertyIds.set(configuration, false);
            }
        });
    }

    radioSelectionChange(event) {
        this.staticProperty.alternatives.forEach(alternative => {
            alternative.selected =
                alternative.elementId === event.value.elementId;
            this.checkFireCompleted(alternative);
        });
        this.changeDetectorRef.detectChanges();
    }

    handleConfigurationUpdate(configurationInfo: ConfigurationInfo) {
        this.dependentStaticPropertyIds.set(
            configurationInfo.staticPropertyInternalName,
            configurationInfo.configured,
        );
        if (this.alternativeCompleted()) {
            this.completedStaticProperty = { ...configurationInfo };
            this.emitUpdate(true);
        } else {
            this.emitUpdate();
        }
    }

    checkFireCompleted(alternative: StaticPropertyAlternative) {
        if (alternative.selected && alternative.staticProperty === null) {
            this.emitUpdate(true);
        }
    }

    alternativeCompleted(): boolean {
        return (
            this.staticProperty.alternatives.find(al => {
                if (al.selected && al.staticProperty === null) {
                    return true;
                } else {
                    if (al.staticProperty === null) {
                        return false;
                    } else {
                        return this.dependentStaticPropertyIds.get(
                            al.staticProperty.internalName,
                        );
                    }
                }
            }) !== undefined
        );
    }
}
