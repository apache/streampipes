import { Component, Input } from '@angular/core';

@Component({
    selector: 'sp-static-tree-input-node-details',
    templateUrl: './static-tree-input-node-details.component.html',
    styleUrl: '../static-runtime-resolvable-tree-input.component.scss',
})
export class StaticTreeInputNodeDetailsComponent {
    @Input()
    nodeMetadata: { [index: string]: any };
}
