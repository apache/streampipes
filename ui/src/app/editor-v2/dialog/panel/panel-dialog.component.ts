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

import {CdkPortalOutlet, ComponentPortal, Portal} from "@angular/cdk/portal";
import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
  ViewEncapsulation
} from "@angular/core";

@Component({
  selector: "app-dialog-container",
  templateUrl: './panel-dialog.component.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: ['./panel-dialog.component.scss']
})
export class PanelDialogComponent<T> implements OnInit {

  @Input()
  dialogTitle = "";

  @Input()
  comp: ComponentPortal<T>;

  @Output()
  containerEvent = new EventEmitter<{ key: "CLOSE" }>();

  @ViewChild("portal", {read: CdkPortalOutlet, static: true})
  portal: CdkPortalOutlet;

  @Input()
  selectedPortal: Portal<T>;

  constructor() {
  }

  ngOnInit() {
  }

  attach() {
    const c = this.portal.attach(this.selectedPortal);
    return c.instance;
  }

  closeDialog() {
    this.containerEvent.emit({key: "CLOSE"});
  }
}