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

import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { animate, style, transition, trigger } from '@angular/animations';
import { CurrentUserService } from '@streampipes/shared-ui';
import { TranslateService } from '@ngx-translate/core';
import { CollapseService } from '../../collapse.service';
import { Subscription } from 'rxjs';
import { NgClass } from '@angular/common';
import { ClassDirective } from '@ngbracket/ngx-layout/extended';
import { IconbarComponent } from '../iconbar/iconbar.component';
import { ToolbarComponent } from '../toolbar/toolbar.component';
import { RouterOutlet } from '@angular/router';

@Component({
    selector: 'sp-streampipes',
    templateUrl: './streampipes.component.html',
    styleUrls: ['./streampipes.component.scss'],
    animations: [
        trigger('fadeSlideInOut', [
            transition(':enter', [
                style({ opacity: 0 }),
                animate('1000ms', style({ opacity: 1 })),
            ]),
            transition(':leave', [animate('1000ms', style({ opacity: 0 }))]),
        ]),
    ],
    imports: [
        NgClass,
        ClassDirective,
        IconbarComponent,
        ToolbarComponent,
        RouterOutlet,
    ],
})
export class StreampipesComponent implements OnInit, OnDestroy {
    darkMode: boolean;

    private translate = inject(TranslateService);
    private collapseService = inject(CollapseService);
    private currentUserService = inject(CurrentUserService);

    darkMode$: Subscription;
    user$: Subscription;

    collapsed = this.collapseService.isCollapsed;

    ngOnInit(): void {
        this.darkMode$ = this.currentUserService.darkMode$.subscribe(dm => {
            this.darkMode = dm;
            if (dm) {
                document.documentElement.classList.add('dark-mode');
                document.documentElement.classList.remove('light-mode');
            } else {
                document.documentElement.classList.remove('dark-mode');
                document.documentElement.classList.add('light-mode');
            }
        });
        this.user$ = this.currentUserService.user$.subscribe(user => {
            if (user.language !== null && user.language !== 'browser') {
                this.translate.use(user.language);
            }
        });
    }

    ngOnDestroy() {
        this.darkMode$?.unsubscribe();
        this.user$?.unsubscribe();
    }
}
