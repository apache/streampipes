import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'sp-basic-header-title-component',
  templateUrl: './header-title.component.html',
  styleUrls: ['./header-title.component.scss']
})
export class SpBasicHeaderTitleComponent implements OnInit {

  @Input()
  title: string;

  @Input()
  margin = '20px 0px';

  ngOnInit(): void {
  }

}
