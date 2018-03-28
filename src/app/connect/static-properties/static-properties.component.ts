import {Component, Input, OnInit, Output} from '@angular/core';
import {FreeTextStaticProperty} from '../model/FreeTextStaticProperty';
import {StaticProperty} from '../model/StaticProperty';

@Component({
  selector: 'app-static-properties',
  templateUrl: './static-properties.component.html',
  styleUrls: ['./static-properties.component.css']
})
export class StaticPropertiesComponent implements OnInit {

  @Input() staticProperties: StaticProperty[];

  constructor() { }

  ngOnInit() {
  }

  isFreeTextStaticProperty(val) {
    return val instanceof FreeTextStaticProperty;
  }

  asFreeTextStaticProperty(val: StaticProperty): FreeTextStaticProperty {
    return <FreeTextStaticProperty> val;
  }

}
