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
    EventEmitter,
    Input,
    Output,
    ViewChild,
    Directive,
} from '@angular/core';
import { CdkPortalOutlet, ComponentPortal, Portal } from '@angular/cdk/portal';
import { DialogRef } from './dialog-ref';

@Directive()
export abstract class BaseDialogComponent<T> {
    @Input()
    dialogTitle = '';

    @Input()
    comp: ComponentPortal<T>;

    @Output()
    containerEvent = new EventEmitter<{ key: 'CLOSE' }>();

    @ViewChild('portal', { read: CdkPortalOutlet, static: true })
    portal: CdkPortalOutlet;

    @Input()
    selectedPortal: Portal<T>;

    @Input()
    dialogRef: DialogRef<T>;

    protected constructor() {}

    attach(): any {
        const c = this.portal.attach(this.selectedPortal);
        return c.instance;
    }

    close() {
        this.dialogRef.close();
    }

    abstract closeDialog();
}
