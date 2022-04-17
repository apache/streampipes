import { NgModule } from '@angular/core';
import { ConfirmDialogComponent } from './dialog/confirm-dialog/confirm-dialog.component';
import { PanelDialogComponent } from './dialog/panel-dialog/panel-dialog.component';
import { StandardDialogComponent } from './dialog/standard-dialog/standard-dialog.component';
import { CommonModule } from "@angular/common";
import { PortalModule } from "@angular/cdk/portal";
import { MatButtonModule } from "@angular/material/button";
import { OverlayModule } from "@angular/cdk/overlay";

@NgModule({
  declarations: [
    ConfirmDialogComponent,
    PanelDialogComponent,
    StandardDialogComponent
  ],
  imports: [
    CommonModule,
    PortalModule,
    MatButtonModule,
    OverlayModule
  ],
  exports: [
    ConfirmDialogComponent, PanelDialogComponent, StandardDialogComponent
  ]
})
export class SharedUiModule { }
