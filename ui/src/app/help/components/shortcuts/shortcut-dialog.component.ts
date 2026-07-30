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
import { MatIconButton } from '@angular/material/button';
import { MatDialogClose, MatDialogContent } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { ShortcutsTabComponent } from './shortcuts.component';

@Component({
    selector: 'sp-shortcut-dialog',
    templateUrl: './shortcut-dialog.component.html',
    styleUrl: './shortcut-dialog.component.scss',
    imports: [
        MatDialogClose,
        MatDialogContent,
        MatIcon,
        MatIconButton,
        ShortcutsTabComponent,
        TranslatePipe,
    ],
})
export class ShortcutDialogComponent {}
