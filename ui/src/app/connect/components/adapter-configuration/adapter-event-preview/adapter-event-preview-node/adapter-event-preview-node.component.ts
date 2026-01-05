import { Component, Input, OnInit, signal } from '@angular/core';

@Component({
    selector: 'sp-adapter-event-preview-node',
    standalone: false,
    templateUrl: './adapter-event-preview-node.component.html',
    styleUrl: './adapter-event-preview-node.component.scss',
})
export class AdapterEventPreviewNodeComponent implements OnInit {
    @Input() keyLabel: string | number | null = null;
    @Input() value: unknown;
    @Input() expanded = false;

    @Input() maxStringLength = 220;
    @Input() sortKeys = true;

    private _open = signal(false);
    open = this._open.asReadonly();

    ngOnInit() {
        this._open.set(this.expanded);
    }

    toggle() {
        this._open.set(!this._open());
    }

    isArray() {
        return Array.isArray(this.value);
    }

    isObject() {
        return (
            typeof this.value === 'object' &&
            this.value !== null &&
            !Array.isArray(this.value)
        );
    }

    isExpandable() {
        return this.isArray() || this.isObject();
    }

    typeLabel() {
        if (this.isArray()) return 'Array';
        if (this.isObject()) return 'Object';
        return 'Value';
    }

    arrayItems() {
        return (this.value as any[]) ?? [];
    }

    objectEntries(): [string, unknown][] {
        const obj = (this.value as Record<string, unknown>) ?? {};
        const entries = Object.entries(obj);
        if (!this.sortKeys) return entries;
        return entries.sort(([a], [b]) => a.localeCompare(b));
    }

    leafText() {
        const v: any = this.value;

        if (v === null || v === undefined) return String(v);

        if (typeof v === 'string') {
            const clipped =
                v.length > this.maxStringLength
                    ? v.slice(0, this.maxStringLength) + '…'
                    : v;
            return `"${clipped}"`;
        }

        if (typeof v === 'number' || typeof v === 'boolean') return String(v);

        // fallback for weird primitives (bigint/symbol/function)
        try {
            return String(v);
        } catch {
            return '[unprintable]';
        }
    }

    valueClass() {
        const v: any = this.value;
        if (v === null || v === undefined) return 'nullish';
        if (typeof v === 'string') return 'string';
        if (typeof v === 'number') return 'number';
        if (typeof v === 'boolean') return 'boolean';
        return '';
    }

    trackByIndex = (_: number, __: unknown) => _;
    trackByKey = (_: number, kv: [string, unknown]) => kv[0];
}
