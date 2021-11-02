/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { GeneralConfigService } from '../../platform-services/apis/general-config.service';
import { GeneralConfigModel } from '../../platform-services/model/general-config.model';

@Component({
  selector: 'sp-general-configuration',
  templateUrl: './general-configuration.component.html',
  styleUrls: ['./general-configuration.component.scss']
})
export class GeneralConfigurationComponent implements OnInit {

  parentForm: FormGroup;
  formReady = false;

  generalConfig: GeneralConfigModel;

  constructor(private fb: FormBuilder,
              private generalConfigService: GeneralConfigService) {}

  ngOnInit(): void {
    this.generalConfigService.getGeneralConfig().subscribe(config => {
      if (config.configured) {
        this.generalConfig = config;
      } else {
        this.generalConfig = {
          configured: false,
          hostname: window.location.hostname,
          port: window.location.port as unknown as number,
          protocol: window.location.protocol.replace(':', '') as unknown as 'http' | 'https'
        };
      }
      this.parentForm = this.fb.group({});
      this.parentForm.addControl('protocol', new FormControl(this.generalConfig.protocol, Validators.required));
      this.parentForm.addControl('port', new FormControl(this.generalConfig.port, Validators.required));
      this.parentForm.addControl('hostname', new FormControl(this.generalConfig.hostname, Validators.required));

      this.parentForm.valueChanges.subscribe(v => {
        this.generalConfig.protocol = v.protocol;
        this.generalConfig.port = v.port;
        this.generalConfig.hostname = v.hostname;
      });

      this.formReady = true;
    });

  }

  loadConfig() {
    this.generalConfigService.getGeneralConfig().subscribe(config => {
      this.generalConfig = config;
    });
  }

  updateConfig() {
    this.generalConfigService.updateGeneralConfig(this.generalConfig).subscribe(result => {
      this.loadConfig();
    });
  }

}
