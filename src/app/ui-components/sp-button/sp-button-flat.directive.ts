import {
  AfterContentInit,
  Directive,
  ElementRef,
  Renderer,
} from '@angular/core';

@Directive({
  selector: '[spButtonFlat]',
})
export class SpButtonFlatDirective implements AfterContentInit {
  constructor(public el: ElementRef, public renderer: Renderer) {}

  ngAfterContentInit() {
    this.renderer.setElementClass(
      this.el.nativeElement.querySelector('button'),
      'sp-button-flat',
      true
    );
  }
}
