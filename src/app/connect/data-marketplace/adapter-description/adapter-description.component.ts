import { Component, Input, EventEmitter, Output } from '@angular/core';
import { AdapterDescription } from '../../model/connect/AdapterDescription';
import { ConnectService } from '../../connect.service';

@Component({
  selector: 'sp-adapter-description',
  templateUrl: './adapter-description.component.html',
  styleUrls: ['./adapter-description.component.css'],
})
export class AdapterDescriptionComponent {
  constructor(private connectService: ConnectService) {}

  @Input()
  adapter: AdapterDescription;
  @Output()
  deleteAdapterEmitter: EventEmitter<AdapterDescription> = new EventEmitter<
    AdapterDescription
  >();

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
    this.deleteAdapterEmitter.emit(adapter);
  }
}
