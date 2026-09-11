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

import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

/** A content toolbar for use below a page header or inside a workspace. */
@Component({
    selector: 'sp-secondary-toolbar',
    template: '<ng-content></ng-content>',
    styleUrls: ['./secondary-toolbar.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: {
        '[class.secondary-toolbar--padded]': 'padding',
    },
})
export class SpSecondaryToolbarComponent {
    /** Disable when projected content already provides its own toolbar spacing. */
    @Input()
    padding = true;
}
