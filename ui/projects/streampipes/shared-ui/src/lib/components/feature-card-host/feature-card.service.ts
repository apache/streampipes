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

import { inject, Injectable } from '@angular/core';
import { FeatureCardHostComponent } from './feature-card-host.component';
import { Router } from '@angular/router';
import { FeatureCardRouteData } from './feature-card.model';
import { DialogService } from '../../dialog/base-dialog/base-dialog.service';
import { PanelType } from '../../dialog/base-dialog/base-dialog.model';

@Injectable({ providedIn: 'root' })
export class FeatureCardService {
    private dialogService = inject(DialogService);
    private router = inject(Router);

    cards: FeatureCardRouteData[] = [];

    constructor() {
        const configs = this.router.config;
        this.cards = configs
            .find(config => config.path === '')
            .children.filter(r => r.data && (r.data as any).cards)
            .flatMap(r => (r.data as any).cards as FeatureCardRouteData[]);
    }

    openFeatureCard(cardId: string, resourceId: string) {
        if (this.supportsFeatureCard(cardId)) {
            const card = this.cards.find(card => card.id === cardId);
            this.dialogService.open(FeatureCardHostComponent, {
                panelType: PanelType.CARD,
                title: '',
                width: '400px',
                data: {
                    card,
                    resourceId,
                },
            });
        }
    }

    supportsFeatureCard(cardId: string) {
        return this.cards.find(card => card.id === cardId) !== undefined;
    }
}
