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

import { ClipboardModule } from '@angular/cdk/clipboard';

import { Component, inject } from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import {
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { DialogRef } from '@streampipes/shared-ui';
import { TranslatePipe } from '@ngx-translate/core';

interface CodeSnippet {
    label: string;
    examples: string[];
    description: string;
}

@Component({
    selector: 'sp-transformation-script-documentation-dialog',
    templateUrl: './transformation-script-documentation-dialog.component.html',
    styleUrl: './transformation-script-documentation-dialog.component.scss',
    imports: [
        ClipboardModule,
        LayoutDirective,
        LayoutGapDirective,
        LayoutAlignDirective,
        MatButton,
        MatDivider,
        MatIcon,
        MatTooltip,
        TranslatePipe,
        MatIconButton,
    ],
})
export class TransformationScriptDocumentationDialogComponent {
    private dialogRef = inject(
        DialogRef<TransformationScriptDocumentationDialogComponent>,
    );

    readonly apiSnippets: CodeSnippet[] = [
        {
            label: 'Read event fields',
            examples: [
                'const temperature = event.temperature;\nconst sensorId = event.sensorId;',
            ],
            description:
                'Use event fields from the current sample event. ' +
                'The editor autocompletes available runtime names after event.',
        },
        {
            label: 'Emit a transformed event',
            examples: [
                'out.collect({\n  sensor: event.sensor,\n  temperatureF: (event.temperatureC * 9 / 5) + 32,\n});',
            ],
            description:
                'Send the transformed payload to the output collector. ' +
                'Call out.collect with the object you want to emit.',
        },
        {
            label: 'Use the StreamPipes client',
            examples: [
                'const adapters = ctx.client().adapters().all();\n' +
                    'out.collect({\n  adapterId: adapters[0].elementId,\n  adapterName: adapters[0].name,\n});',
            ],
            description:
                'ctx.client() exposes the StreamPipes client inside the script. ' +
                'You can read from APIs and use their results in your output.',
        },
        {
            label: 'Client entry points',
            examples: [
                'ctx.client().pipelines();\nctx.client().pipelineElementTemplates();\n' +
                    'ctx.client().adapters();\nctx.client().sinks();',
            ],
            description:
                'These client APIs are available through editor autocomplete after ctx.client().',
        },
    ];

    readonly utilitySnippets: CodeSnippet[] = [
        {
            label: 'Add timestamp',
            examples: [
                'utils.addTimestamp(event);',
                'utils.addTimestamp(event, "processedAt");',
            ],
            description:
                'Adds the current time in epoch milliseconds to the given field and returns the updated event.',
        },
        {
            label: 'Rename field',
            examples: ['utils.rename(event, "oldName", "newName");'],
            description:
                'Copies a field to a new name, removes the old field, and returns the updated event.',
        },
        {
            label: 'Remove field',
            examples: ['utils.remove(event, "fieldName");'],
            description:
                'Deletes a field from the event and returns the updated event.',
        },
        {
            label: 'Parse timestamp',
            examples: [
                'utils.parseTimestamp(event, "createdAt");',
                'utils.parseTimestamp(event, "createdAt", "eventTime");',
            ],
            description:
                'Parses a date field into epoch milliseconds and returns the updated event. ' +
                'By default the value is stored in timestamp. Invalid dates throw an error.',
        },
    ];

    close(): void {
        this.dialogRef.close();
    }
}
