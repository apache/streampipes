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

import { ComponentRef } from '@angular/core';
import { OverlayRef } from '@angular/cdk/overlay';
import { Observable, Subject } from 'rxjs';
import { BaseDialogComponentUnion } from './base-dialog.model';
import { OverlayConfig, OverlaySizeConfig } from '@angular/cdk/overlay';

export class DialogRef<T> {
    private _componentInstance: ComponentRef<T>;
    private _afterClosed: Subject<any> = new Subject<any>();

    constructor(
        private overlayRef: OverlayRef,
        private dialogContainerRef: ComponentRef<BaseDialogComponentUnion>,
    ) {}

    get componentInstance() {
        return this._componentInstance;
    }

    set componentInstance(c: ComponentRef<T>) {
        this._componentInstance = c;
    }

    public close(data?: any) {
        this.dialogContainerRef.instance.closeDialog();
        this._afterClosed.next(data);
        this._afterClosed.complete();
    }

    public afterClosed(): Observable<any> {
        return this._afterClosed.asObservable();
    }

    public changeDialogSize(sizeConfig: OverlaySizeConfig) {
        this.overlayRef.updateSize(sizeConfig);
    }

    public currentConfig(): OverlayConfig {
        return this.overlayRef.getConfig();
    }
}
