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
import { RestService } from '../../../services/rest.service';
import { AdapterDescription } from '@streampipes/platform-services';

@Component({
    selector: 'sp-event-preview',
    standalone: false,
    templateUrl: './event-preview.component.html',
    styleUrl: './event-preview.component.scss',
})
export class EventPreviewComponent {
    restService = inject(RestService);

    @Input()
    adapterDescription: AdapterDescription;

    @Output()
    goBackEmitter: EventEmitter<MatStepper> = new EventEmitter();

    @Output()
    cancelEmitter: EventEmitter<void> = new EventEmitter();

    @Output()
    nextEmitter: EventEmitter<MatStepper> = new EventEmitter();

    runError: string | null = null;

    sampleScripts: any[] = [
        {
            key: 'identity',
            title: 'Identity (return event)',
            value: `// returns the same event
return event;`,
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
            this.runError = null;
            // optional: reset output when selecting a new sample
            this.output = null;
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

    input = {};
    output = {};

    script = `return event;`;

    getSampleEvent(): void {
        this.restService
            .getSampleEvents(this.adapterDescription)
            .subscribe(sampleData => {
                this.input = sampleData.samples[0];
            });
    }

    runScript(): void {
        this.runError = null;
        const inputClone = this.input
            ? JSON.parse(JSON.stringify(this.input))
            : {};
        try {
            // First try: treat the editor content as a function body that returns the transformed event
            try {
                const fn = new Function('event', this.script);
                const result = fn(inputClone);
                this.output = result === undefined ? null : result;
                this.adapterDescription.sampleData = {
                    samples: [this.output],
                };
                return;
            } catch (e) {
                // fallback: try to parse the editor contents as a function expression, e.g. `(event) => {...}` or `function(event){...}`
                const maybeFn = eval(`(${this.script})`);
                if (typeof maybeFn === 'function') {
                    const result = maybeFn(inputClone);
                    this.output = result === undefined ? null : result;
                    return;
                }
                // If not a function, throw original error
                throw e;
            }
        } catch (err: any) {
            this.runError = err && err.message ? err.message : String(err);
            this.output = null;
        }
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
