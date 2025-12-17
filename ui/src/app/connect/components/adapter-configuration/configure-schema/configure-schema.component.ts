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

import {
    Component,
    EventEmitter,
    inject,
    Input,
    OnInit,
    Output,
} from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import { AdapterDescription } from '@streampipes/platform-services';
import { filter, map } from 'rxjs/operators';
import { shareReplay } from 'rxjs';
import { AdapterConfigurationStateService } from '../adapter-configuration-state-service/adapter-configuration-state.service';

@Component({
    selector: 'sp-configure-schema',
    standalone: false,
    templateUrl: './configure-schema.component.html',
    styleUrl: './configure-schema.component.scss',
})
export class ConfigureSchemaComponent implements OnInit {
    private stateService = inject(AdapterConfigurationStateService);

    @Input()
    adapterDescription: AdapterDescription;

    @Output()
    goBackEmitter: EventEmitter<MatStepper> = new EventEmitter();

    @Output()
    cancelEmitter: EventEmitter<void> = new EventEmitter();

    @Output()
    nextEmitter: EventEmitter<MatStepper> = new EventEmitter();

    private adapterDescription$ = this.stateService.state$.pipe(
        map(state => state.adapterDescription),
        filter(adapter => !!adapter),
        shareReplay(1), // Prevents multiple extractions for different async pipes
    );

    // variables related to get sample
    isSampleLoading$ = this.stateService.state$.pipe(
        map(s => s.isGettingSample),
    );
    sampleErrorMessage$ = this.stateService.state$.pipe(
        map(s => s.sampleError),
    );
    input$ = this.adapterDescription$.pipe(
        map(a => a.schemaTransformationConfig?.inputs?.[0] || {}),
    );

    // variables related to run script
    isRunningScript$ = this.stateService.state$.pipe(
        map(s => s.isRunningScript),
    );
    scriptError$ = this.stateService.state$.pipe(map(s => s.scriptError));
    output$ = this.adapterDescription$.pipe(
        map(a => a.schemaTransformationConfig?.outputs?.[0] || {}),
    );
    script = `// returns the same event
function transform(event) {
  return event;
}`;

    sampleScripts: any[] = [
        {
            key: 'identity',
            title: 'Identity (return event)',
            value: `// returns the same event
function transform(event) {
  return event;
}`,
        },
        {
            key: 'complex',
            title: 'Simulator (Complex event)',
            value: ` var flattened = {};

    // 1. Define the keys we want to extract (hardcoded list of top-level primitives)
    var KEYS_TO_EXTRACT = [
        "phase",
        "sensorType",
        "active",
        "timestamp",
        "sensorId"
    ];

    // Handles null or non-object inputs gracefully
    if (typeof event !== 'object' || event === null) {
        return flattened;
    }

    // 2. Iterate only over the hardcoded list of keys using a traditional for loop
    for (var i = 0; i < KEYS_TO_EXTRACT.length; i++) {
        var key = KEYS_TO_EXTRACT[i];
        // Use hasOwnProperty to ensure the property exists directly on the object
        if (Object.prototype.hasOwnProperty.call(event, key)) {
            flattened[key] = event[key];
        }
    }

    return flattened;`,
        },
    ];

    selectSample(key: string) {
        const s = this.sampleScripts.find(x => x.key === key);
        if (s) {
            this.script = s.value;
            // optional: reset output when selecting a new sample
        }
    }

    editorOptions = {
        mode: 'javascript',
        autoRefresh: true,
        theme: 'dracula',
        autoCloseBrackets: true,
        lineNumbers: true,
        lineWrapping: true,
        gutters: ['CodeMirror-lint-markers'],
        lint: true,
        extraKeys: {
            'Ctrl-Space': 'autocomplete',
        },
    };

    ngOnInit(): void {
        this.initializeScriptVariable();
    }

    private initializeScriptVariable(): void {
        if (this.adapterDescription.schemaTransformationConfig.script != '') {
            this.script =
                this.adapterDescription.schemaTransformationConfig.script;
        } else {
            this.adapterDescription.schemaTransformationConfig.script =
                this.script;
        }
    }

    getSampleEvent(): void {
        this.stateService.getSampleEvent(this.adapterDescription);
    }

    runScript(): void {
        this.stateService.runScript(this.adapterDescription, this.script);
    }

    public cancel() {
        this.cancelEmitter.emit();
    }

    public next() {
        this.nextEmitter.emit();
    }

    public goBack() {
        this.goBackEmitter.emit();
    }
}
