import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FileUploadComponent } from './file-upload.component';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';
import { MatSnackBarModule} from '@angular/material';
import { FileRestService } from './service/filerest.service';

@NgModule({
  imports: [
    CommonModule,
    FlexLayoutModule,
    CustomMaterialModule,
    MatSnackBarModule
  ],
  declarations: [
      FileUploadComponent
  ],
  entryComponents: [
      FileUploadComponent
  ],
  providers: [
      FileRestService
  ]
})
export class FileUploadModule { }
