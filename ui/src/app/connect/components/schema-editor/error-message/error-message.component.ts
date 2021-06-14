import { Component, Input, OnInit } from '@angular/core';
import { Notification } from '../../../../core-model/gen/streampipes-model';

@Component({
  selector: 'sp-error-message',
  templateUrl: './error-message.component.html',
  styleUrls: ['./error-message.component.css']
})
export class ErrorMessageComponent implements OnInit {

  @Input() errorMessages: Notification[];

  showErrorMessage = false;

  constructor() { }

  ngOnInit(): void {
  }

}
