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

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AdapterDescriptionUnion} from "../../../../core-model/gen/streampipes-model";
import {MatTableDataSource} from "@angular/material/table";
import {ConnectService} from "../../../services/connect.service";
import {DataMarketplaceService} from "../../../services/data-marketplace.service";

@Component({
  selector: 'sp-existing-adapters',
  templateUrl: './existing-adapters.component.html',
  styleUrls: ['./existing-adapters.component.scss'],
})
export class ExistingAdaptersComponent implements OnInit {

  _existingAdapters: Array<AdapterDescriptionUnion>;

  @Input()
  filterTerm: string;

  @Output()
  updateAdapterEmitter: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  createTemplateEmitter: EventEmitter<AdapterDescriptionUnion> = new EventEmitter<AdapterDescriptionUnion>();

  displayedColumns: string[] = ['start', 'name', 'adapterBase', 'adapterType', 'lastModified', 'action'];

  dataSource: MatTableDataSource<AdapterDescriptionUnion>;

  constructor(public connectService: ConnectService,
              private dataMarketplaceService: DataMarketplaceService) {

  }

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource(this.existingAdapters);
  }

  startAdapter(adapter: AdapterDescriptionUnion) {
    this.dataMarketplaceService.startAdapter(adapter).subscribe(response => {
      this.updateAdapterEmitter.emit();
    });
  }

  stopAdapter(adapter: AdapterDescriptionUnion) {
    this.dataMarketplaceService.stopAdapter(adapter).subscribe(response => {
      this.updateAdapterEmitter.emit();
    });
  }

  getIconUrl(adapter: AdapterDescriptionUnion) {
    if (adapter.includedAssets.length > 0) {
      return this.dataMarketplaceService.getAssetUrl(adapter.appId) + "/icon";
    } else {
      return 'assets/img/connect/' + adapter.iconUrl;
    }
  }

  deleteAdapter(adapter: AdapterDescriptionUnion): void {
    this.dataMarketplaceService.deleteAdapter(adapter).subscribe(res => {
      this.updateAdapterEmitter.emit();
    });
  }

  createTemplate(adapter: AdapterDescriptionUnion): void {
    this.createTemplateEmitter.emit(adapter);
  }

  @Input()
  set existingAdapters(adapters: Array<AdapterDescriptionUnion>) {
    this._existingAdapters = adapters;
    this.dataSource = new MatTableDataSource(adapters);
  }

  get existingAdapters(): Array<AdapterDescriptionUnion> {
    return this._existingAdapters;
  }

}
