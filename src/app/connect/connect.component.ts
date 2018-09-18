import { Component, OnInit, ViewChild } from '@angular/core';
import { AdapterDescription } from './model/connect/AdapterDescription';
@Component({
  selector: 'sp-connect',
  templateUrl: './connect.component.html',
  styleUrls: ['./connect.component.css'],
})
export class ConnectComponent {
  newAdapterFromDescription: AdapterDescription;

  selectAdapter(adapterDescription: AdapterDescription) {
    this.newAdapterFromDescription = adapterDescription;
  }

  removeSelection() {
    this.newAdapterFromDescription = undefined;
  }
}
