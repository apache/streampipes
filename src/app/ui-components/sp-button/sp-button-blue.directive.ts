import {
  AfterContentInit,
  Directive,
  ElementRef,
  Renderer,
} from '@angular/core';

@Directive({
  selector: '[spButtonBlue]',
})
export class SpButtonBlueDirective implements AfterContentInit {
  constructor(public el: ElementRef, public renderer: Renderer) {}

  ngAfterContentInit() {
    this.renderer.setElementClass(
      this.el.nativeElement.querySelector('button'),
      'sp-button-blue',
      true
    );
  }
}
