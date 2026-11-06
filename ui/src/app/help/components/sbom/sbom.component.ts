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
    ChangeDetectionStrategy,
    Component,
    DestroyRef,
    OnInit,
    inject,
} from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    SpAlertBannerComponent,
    SpLabelComponent,
    SpTableComponent,
    SpTableNameSearchConfig,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { SbomService } from './sbom.service';
import { CycloneDxBom, SbomIndexEntry, SbomRow } from './sbom.model';
import { LoadedSbom, mergeSbomComponents } from './sbom-merge';

@Component({
    selector: 'sp-sbom-tab',
    templateUrl: './sbom.component.html',
    styleUrls: ['./sbom.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        DatePipe,
        MatButtonModule,
        MatCheckboxModule,
        MatIconModule,
        MatProgressBarModule,
        MatTableModule,
        MatTooltipModule,
        LayoutDirective,
        LayoutGapDirective,
        LayoutAlignDirective,
        TranslatePipe,
        SpAlertBannerComponent,
        SpLabelComponent,
        SpTableComponent,
        SplitSectionComponent,
    ],
})
export class SbomTabComponent implements OnInit {
    private sbomService = inject(SbomService);
    private translate = inject(TranslateService);
    private destroyRef = inject(DestroyRef);

    readonly columns = ['name', 'version', 'type', 'licenses', 'sources'];
    readonly searchConfig: SpTableNameSearchConfig<SbomRow> = {
        enabled: true,
        placeholder: this.translate.instant('Search components'),
        searchKey: ['name', 'group', 'version', 'purl', 'licenses'],
    };

    dataSource = new MatTableDataSource<SbomRow>([]);
    entries: SbomIndexEntry[] = [];
    generatedAt?: string;
    selected = new Set<string>();
    loading = false;
    /** The build did not publish an index or it lists no SBOM. */
    notIncluded = false;
    /** The index or a document could not be loaded for another reason. */
    loadFailed = false;
    failedEntries: string[] = [];

    private boms = new Map<string, CycloneDxBom>();
    private labels = new Map<string, string>();

    ngOnInit(): void {
        this.load();
    }

    load(): void {
        this.loading = true;
        this.notIncluded = false;
        this.loadFailed = false;
        this.failedEntries = [];
        this.sbomService
            .loadIndex()
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
                next: index => {
                    this.entries = index.entries ?? [];
                    this.generatedAt = index.generatedAt;
                    this.labels = new Map(
                        this.entries.map(entry => [entry.id, entry.label]),
                    );
                    this.selected = new Set(this.entries.map(e => e.id));
                    if (this.entries.length === 0) {
                        this.notIncluded = true;
                        this.loading = false;
                        this.rebuildRows();
                        return;
                    }
                    this.loadDocuments();
                },
                error: (error: HttpErrorResponse) => {
                    this.loading = false;
                    this.entries = [];
                    this.rebuildRows();
                    if (error.status === 404) {
                        this.notIncluded = true;
                    } else {
                        this.loadFailed = true;
                    }
                },
            });
    }

    toggleSource(id: string, checked: boolean): void {
        if (checked) {
            this.selected.add(id);
        } else {
            this.selected.delete(id);
        }
        this.rebuildRows();
    }

    isSelected(id: string): boolean {
        return this.selected.has(id);
    }

    labelFor(id: string): string {
        return this.labels.get(id) ?? id;
    }

    downloadUrl(entry: SbomIndexEntry): string {
        return this.sbomService.downloadUrl(entry);
    }

    downloadName(entry: SbomIndexEntry): string {
        return `${entry.id}-bom.json`;
    }

    get componentCount(): number {
        return this.dataSource.data.length;
    }

    private loadDocuments(): void {
        const requests = this.entries.map(entry =>
            this.sbomService.loadBom(entry).pipe(
                map(bom => ({ entry, bom }) as LoadedSbom),
                catchError(() => {
                    this.failedEntries.push(entry.label);
                    return of(null);
                }),
            ),
        );
        forkJoin(requests)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe(loaded => {
                this.boms = new Map(
                    loaded
                        .filter((item): item is LoadedSbom => item !== null)
                        .map(item => [item.entry.id, item.bom]),
                );
                this.loading = false;
                this.rebuildRows();
            });
    }

    private rebuildRows(): void {
        const loaded: LoadedSbom[] = this.entries
            .filter(entry => this.selected.has(entry.id))
            .filter(entry => this.boms.has(entry.id))
            .map(entry => ({ entry, bom: this.boms.get(entry.id) }));
        this.dataSource.data = mergeSbomComponents(loaded);
    }
}
