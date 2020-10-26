import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'sp-labels',
  templateUrl: './labels.component.html',
  styleUrls: ['./labels.component.css']
})
export class LabelsComponent implements OnInit {

  public editLabels = false;

  constructor() { }

  ngOnInit(): void {
  }

}
