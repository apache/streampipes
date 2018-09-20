import {
  AfterContentInit,
  Directive,
  ElementRef,
  Renderer,
} from '@angular/core';

@Directive({
  selector: '[spButtonGray]',
})
export class SpButtonGrayDirective implements AfterContentInit {
  constructor(public el: ElementRef, public renderer: Renderer) {}

  ngAfterContentInit() {
    this.renderer.setElementClass(
      this.el.nativeElement.querySelector('button'),
      'sp-button-gray',
      true
    );
  }
}
