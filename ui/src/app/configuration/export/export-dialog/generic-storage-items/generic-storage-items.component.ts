import { Component, Input } from '@angular/core';
import { ExportItem } from '@streampipes/platform-services';

@Component({
    selector: 'sp-generic-storage-items',
    templateUrl: './generic-storage-items.component.html',
})
export class GenericStorageItemsComponent {
    @Input()
    exportItems: ExportItem[];

    @Input()
    importMode = false;

    newDocumentId = '';

    addGenericStorageDocument(): void {
        this.exportItems.push({
            resourceId: this.newDocumentId,
            label: this.newDocumentId,
            selected: true,
        });
    }

    handleDocumentRemoval(resourceId: string): void {
        const index = this.exportItems.findIndex(
            e => e.resourceId === resourceId,
        );
        if (!this.importMode) {
            this.exportItems.splice(index, 1);
        } else {
            this.exportItems[index].selected =
                !this.exportItems[index].selected;
        }
    }
}
