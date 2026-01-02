import { Component, inject, Input } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import { ConnectScriptTemplatesService } from '@streampipes/platform-services';

@Component({
    selector: 'sp-create-adapter-transformation-template-dialog',
    templateUrl:
        './create-adapter-transformation-template-dialog.component.html',
    styleUrl: './create-adapter-transformation-template-dialog.component.scss',
    standalone: false,
})
export class CreateAdapterTransformationTemplateDialogComponent {
    @Input()
    script: string;

    @Input()
    language: string;

    templateName = 'Name';
    templateDescription = 'Description';

    private templateService = inject(ConnectScriptTemplatesService);
    private dialogRef = inject(
        DialogRef<CreateAdapterTransformationTemplateDialogComponent>,
    );

    save(): void {
        this.templateService
            .create({
                appDocType: 'transformation-script-template',
                elementId: undefined,
                rev: undefined,
                language: this.language,
                code: this.script,
                name: this.templateName,
                description: this.templateDescription,
            })
            .subscribe(() => this.close(true));
    }

    close(reloadTemplate = false): void {
        this.dialogRef.close(reloadTemplate);
    }
}
