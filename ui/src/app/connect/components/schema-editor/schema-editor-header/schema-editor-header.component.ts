import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'sp-schema-editor-header',
  templateUrl: './schema-editor-header.component.html',
  styleUrls: ['./schema-editor-header.component.css']
})
export class SchemaEditorHeaderComponent implements OnInit {


  @Input() countSelected: number;

  @Output() addNestedPropertyEmitter = new EventEmitter();
  @Output() addStaticValuePropertyEmitter = new EventEmitter();
  @Output() addTimestampPropertyEmitter = new EventEmitter();
  @Output() guessSchemaEmitter = new EventEmitter();
  @Output() togglePreviewEmitter = new EventEmitter();
  @Output() removeSelectedPropertiesEmitter = new EventEmitter();

  constructor() { }

  ngOnInit(): void {
  }

  public addNestedProperty() {
    this.addNestedPropertyEmitter.emit();
  }

  public addStaticValueProperty() {
    this.addStaticValuePropertyEmitter.emit();
  }

  public addTimestampProperty() {
    this.addTimestampPropertyEmitter.emit();
  }

  public guessSchema() {
    this.guessSchemaEmitter.emit();
  }

  public togglePreview() {
    this.togglePreviewEmitter.emit();
  }

  public removeSelectedProperties() {
    this.removeSelectedPropertiesEmitter.emit();
  }
}
