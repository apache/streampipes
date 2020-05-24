import { Component, Input, OnInit } from '@angular/core';
import { DateRange } from '../../../../../core-model/datalake/DateRange';

@Component({
  selector: 'sp-no-data-in-date-range',
  templateUrl: './no-data-in-date-range.component.html',
  styleUrls: ['./no-data-in-date-range.component.css']
})
export class NoDataInDateRangeComponent implements OnInit {

  @Input()
  public viewDateRange: DateRange;

  constructor() { }

  ngOnInit(): void {
  }

}
