import { Component, Input } from '@angular/core';

@Component({
  selector: 'sp-button-new',
  templateUrl: './sp-button.component.html',
  styleUrls: ['./sp-button.component.css'],
})
export class SpButtonComponent {
  @Input()
  spButtonBlue = false;

  constructor() {
    console.log(this.spButtonBlue);
  }
}
