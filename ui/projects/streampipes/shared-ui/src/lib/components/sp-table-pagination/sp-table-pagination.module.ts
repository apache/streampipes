import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SpTablePaginationComponent } from './sp-table-pagination.component';
import {
    MatPaginatorIntl,
    MatPaginatorModule,
} from '@angular/material/paginator';
import { getCustomPaginatorIntl } from './custom-paginator-intl';

// Angular Material modules
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatTabsModule } from '@angular/material/tabs';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatTreeModule } from '@angular/material/tree';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatStepperModule } from '@angular/material/stepper';
import { MatRadioModule } from '@angular/material/radio';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { FormsModule } from '@angular/forms';
import { PortalModule } from '@angular/cdk/portal';
import { OverlayModule } from '@angular/cdk/overlay';
import { DragDropModule } from '@angular/cdk/drag-drop';

import { TranslateModule } from '@ngx-translate/core';
import { MarkdownModule } from 'ngx-markdown';

@NgModule({
    declarations: [SpTablePaginationComponent],
    imports: [
        CommonModule,
        FormsModule,
        FlexLayoutModule,

        // Angular Material modules
        MatTableModule,
        MatPaginatorModule,
        MatSortModule,
        MatDividerModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatCheckboxModule,
        MatButtonModule,
        MatTabsModule,
        MatMenuModule,
        MatSelectModule,
        MatDatepickerModule,
        MatTooltipModule,
        MatTreeModule,
        MatExpansionModule,
        MatStepperModule,
        MatRadioModule,
        MatProgressSpinnerModule,

        // CDK and other modules
        PortalModule,
        OverlayModule,
        DragDropModule,

        // i18n and markdown
        TranslateModule.forChild({}),
        MarkdownModule.forRoot(),
    ],
    providers: [
        { provide: MatPaginatorIntl, useFactory: getCustomPaginatorIntl },
    ],
    exports: [SpTablePaginationComponent],
})
export class SpTablePaginationModule {}
