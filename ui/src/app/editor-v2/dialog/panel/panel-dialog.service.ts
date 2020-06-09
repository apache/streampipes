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

import {ComponentType, Overlay, OverlayRef,} from "@angular/cdk/overlay";
import {ComponentPortal, PortalInjector,} from "@angular/cdk/portal";
import {PanelDialogComponent} from "./panel-dialog.component";
import {DialogConfig} from "../../model/editor.model";
import {ComponentRef, Injectable, Injector} from "@angular/core";
import {DialogRef} from "./dialog-ref";

@Injectable({
  providedIn: "root"
})
export class PanelDialogService {

  constructor(private overlay: Overlay, private injector: Injector) {

  }

  public open<T>(component: ComponentType<T>,
                 config?: DialogConfig,
                 inputMap?: Object): DialogRef<T> {
    config = config || {width: "auto", title: ""};

    const positionStrategy = this.overlay
        .position()
        .global()
        .top("0")
        .right("0");

    const overlay = this.overlay.create({
      hasBackdrop: true,
      positionStrategy,
      panelClass: "dialog-container",
      width: config.width,
      maxWidth: "90vw",
      height: "100vh",
    });

    const panelDialogContainer = new ComponentPortal(PanelDialogComponent);
    const panelDialogContainerRef = overlay.attach(panelDialogContainer);
    panelDialogContainerRef.instance.dialogTitle = config.title;
    const dialogRef = new DialogRef<T>(overlay, panelDialogContainerRef);

    const injector = this.createInjector(dialogRef);
    panelDialogContainerRef.instance.selectedPortal = new ComponentPortal(component,
        null, injector);
    panelDialogContainerRef.instance.dialogRef = dialogRef;
    dialogRef.componentInstance = panelDialogContainerRef.instance.attach();

    Object.keys(inputMap).forEach(key => {
      dialogRef.componentInstance[key] = inputMap[key];
    })

    this.applyDialogProperties(panelDialogContainerRef, overlay, config);

    return dialogRef;
  }

  private applyDialogProperties(panelDialogComponentRef: ComponentRef<any>,
                                overlayRef: OverlayRef,
                                config: DialogConfig
  ) {
    panelDialogComponentRef.instance.containerEvent.subscribe(e => {
      if (e.key === "CLOSE") {
        overlayRef.dispose();
      }
    });
    if (!config.disableClose) {
      overlayRef.backdropClick().subscribe(() => overlayRef.dispose());
    }
  }

  private createInjector<T>(dialogRef: DialogRef<T>) {
    const injectorMap = new WeakMap();
    injectorMap.set(DialogRef, dialogRef);
    return new PortalInjector(this.injector, injectorMap);
  }
}




