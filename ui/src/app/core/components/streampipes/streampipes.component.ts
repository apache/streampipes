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
import { AuthService } from '../../../services/auth.service';
import { animate, style, transition, trigger } from '@angular/animations';
import { CurrentUserService } from '@streampipes/shared-ui';

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
})
export class StreampipesComponent implements OnInit {
    darkMode: boolean;

    constructor(public currentUserService: CurrentUserService) {}

    ngOnInit(): void {
        this.currentUserService.darkMode$.subscribe(dm => (this.darkMode = dm));
    }
}
