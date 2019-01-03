import { Component, Input, EventEmitter, Output } from '@angular/core';
import { AdapterDescription } from '../../model/connect/AdapterDescription';
import { ConnectService } from '../../connect.service';
import {DataMarketplaceService} from "../data-marketplace.service";

@Component({
  selector: 'sp-adapter-description',
  templateUrl: './adapter-description.component.html',
  styleUrls: ['./adapter-description.component.css'],
})
export class AdapterDescriptionComponent {

  @Input()
  adapter: AdapterDescription;

  @Output()
  updateAdapterEmitter: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  createTemplateEmitter: EventEmitter<AdapterDescription> = new EventEmitter<AdapterDescription>();

  adapterToDelete: string;
  deleting: boolean = false;

  constructor(private connectService: ConnectService, private dataMarketplaceService: DataMarketplaceService) {}

  isDataStreamDescription(): boolean {
    return this.connectService.isDataStreamDescription(this.adapter);
  }

  isDataSetDescription(): boolean {
    return this.connectService.isDataSetDescription(this.adapter);
  }

  isGenericDescription(): boolean {
    return this.connectService.isGenericDescription(this.adapter);
  }

  isSpecificDescription(): boolean {
    return this.connectService.isSpecificDescription(this.adapter);
  }

  deleteAdapter(adapter: AdapterDescription): void {
  this.deleting = true;
      this.adapterToDelete = adapter.couchDbId;
      this.dataMarketplaceService.deleteAdapter(adapter).subscribe(res => {
          this.adapterToDelete = undefined;
          this.updateAdapterEmitter.emit();
          this.deleting = false;
      });
  }

  deleteAdapterTemplate(adapter: AdapterDescription): void {
      this.adapterToDelete = adapter.couchDbId;
      this.dataMarketplaceService.deleteAdapterTemplate(adapter).subscribe(res => {
          this.adapterToDelete = undefined;
          this.updateAdapterEmitter.emit();
          this.deleting = false;
      });
  }

  createTemplate(adapter: AdapterDescription): void {
      this.createTemplateEmitter.emit(adapter);
  }

  getClassName() {
    let className = this.isRunningAdapter(this.adapter) ? "adapter-box" : "adapter-description-box";

    if (this.isDataSetDescription()) {
      className += " adapter-box-set";
    } else {
      className +=" adapter-box-stream";
    }

    return className;
  }

  isRunningAdapter(adapter: AdapterDescription) {
    return (adapter.couchDbId != undefined && !adapter.isTemplate);
  }

  deleteInProgress(adapterCouchDbId) {
    return this.deleting && (adapterCouchDbId === this.adapterToDelete);
  }
}
