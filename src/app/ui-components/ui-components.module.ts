import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material';
import { BrowserModule } from '@angular/platform-browser';
import { SpButtonBlueDirective } from './sp-button/sp-button-blue.directive';
import { SpButtonFlatDirective } from './sp-button/sp-button-flat.directive';
import { SpButtonGrayDirective } from './sp-button/sp-button-gray.directive';
import { SpButtonComponent } from './sp-button/sp-button.component';

@NgModule({
  imports: [BrowserModule, CommonModule, MatButtonModule],
  declarations: [
    SpButtonComponent,
    SpButtonBlueDirective,
    SpButtonGrayDirective,
    SpButtonFlatDirective,
  ],
  exports: [
    SpButtonComponent,
    SpButtonBlueDirective,
    SpButtonGrayDirective,
    SpButtonFlatDirective,
  ],
})
export class UiComponentsModule {}
