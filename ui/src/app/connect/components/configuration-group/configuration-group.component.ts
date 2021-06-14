import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { StaticPropertyUnion } from '../../../core-model/gen/streampipes-model';
import { ConfigurationInfo } from '../../model/ConfigurationInfo';

@Component({
  selector: 'sp-configuration-group',
  templateUrl: './configuration-group.component.html',
  styleUrls: ['./configuration-group.component.css']
})
export class ConfigurationGroupComponent implements OnInit {

  @Input() configurationGroup: FormGroup;

  @Input() adapterId: string;

  @Input() configuration: StaticPropertyUnion[];

  completedStaticProperty: ConfigurationInfo;

  constructor() { }

  ngOnInit(): void {
  }

  triggerUpdate(configurationInfo: ConfigurationInfo) {
    this.completedStaticProperty = {...configurationInfo};
  }

}
