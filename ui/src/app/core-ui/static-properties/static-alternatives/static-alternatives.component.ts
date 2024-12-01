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
    Input,
    OnInit,
    Output,
} from '@angular/core';
import { AbstractStaticPropertyRenderer } from '../base/abstract-static-property';
import {
    ExtensionDeploymentConfiguration,
    StaticPropertyAlternative,
    StaticPropertyAlternatives,
} from '@streampipes/platform-services';
import { ConfigurationInfo } from '../../../connect/model/ConfigurationInfo';

@Component({
    selector: 'sp-app-static-alternatives',
    templateUrl: './static-alternatives.component.html',
    styleUrls: ['./static-alternatives.component.scss'],
})
export class StaticAlternativesComponent
    extends AbstractStaticPropertyRenderer<StaticPropertyAlternatives>
    implements OnInit
{
    @Input()
    deploymentConfiguration: ExtensionDeploymentConfiguration;

    // dependentStaticPropertyIds: Map<string, boolean> = new Map<
    //     string,
    //     boolean
    // >();

    completedAlternativeConfigurations: ConfigurationInfo[] = [];

    constructor(private changeDetectorRef: ChangeDetectorRef) {
        super();
    }

    ngOnInit() {
        this.staticProperty.alternatives.forEach(al => {
            if (al.staticProperty) {
                this.completedAlternativeConfigurations.push({
                    staticPropertyInternalName: al.staticProperty.internalName,
                    configured: false,
                });
            }
        });
        if (!this.staticProperty.alternatives.some(a => a.selected)) {
            this.staticProperty.alternatives[0].selected = true;
            this.checkFireCompleted(this.staticProperty.alternatives[0]);
        }
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
        this.staticPropertyUtils.updateCompletedConfiguration(
            configurationInfo,
            this.completedAlternativeConfigurations,
        );
        if (this.alternativeCompleted()) {
            this.completedAlternativeConfigurations = [
                ...this.completedAlternativeConfigurations,
            ];
            this.applyCompletedConfiguration(true);
        } else {
            this.applyCompletedConfiguration(false);
        }
    }

    checkFireCompleted(alternative: StaticPropertyAlternative) {
        if (alternative.selected && alternative.staticProperty === null) {
            this.applyCompletedConfiguration(true);
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
                        return this.completedAlternativeConfigurations.find(
                            c =>
                                c.staticPropertyInternalName ===
                                al.staticProperty.internalName,
                        ).configured;
                    }
                }
            }) !== undefined
        );
    }
}
