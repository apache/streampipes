import { Component, Input, EventEmitter, OnInit, Output, ViewChild, ChangeDetectorRef } from '@angular/core';
import { RestService } from '../../rest.service';
import { EventSchema } from '../model/EventSchema';
import { AdapterDescription } from '../../model/connect/AdapterDescription';
import { GuessSchema } from '../model/GuessSchema';
import { NotificationLd } from '../../model/message/NotificationLd';
import { FlatTreeControl, CdkTree } from '@angular/cdk/tree';
import { ExampleNode } from '../model/ExampleNode';
import { ArrayDataSource } from '@angular/cdk/collections';
import { EventProperty } from '../model/EventProperty';
import { EventPropertyNested } from '../model/EventPropertyNested';
import { EventPropertyPrimitive } from '../model/EventPropertyPrimitive';
import { EventPropertyList } from '../model/EventPropertyList';
import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { resolve } from 'dns';

@Component({
  selector: 'app-event-schema',
  templateUrl: './event-schema.component.html',
  styleUrls: ['./event-schema.component.css'],
})
export class EventSchemaComponent implements OnInit {

  treeControl = new FlatTreeControl<ExampleNode>(
    node => node.level, node => node.expandable);
  datasource: ArrayDataSource<ExampleNode>;

  @ViewChild('treeSelector') tree: CdkTree<any>;

  constructor(private restService: RestService , private changeDetectorRefs: ChangeDetectorRef) {}
  @Input() adapterDescription: AdapterDescription;
  @Input() isEditable: boolean;
  @Output() isEditableChange = new EventEmitter<boolean>();

  @Output() adapterChange = new EventEmitter<AdapterDescription>();

  @Input() eventSchema: EventSchema;
  @Output() eventSchemaChange = new EventEmitter<EventSchema>();

  @Input() oldEventSchema: EventSchema;
  @Output() oldEventSchemaChange = new EventEmitter<EventSchema>();

  schemaGuess: GuessSchema = new GuessSchema();

  isLoading = false;
  isError = false;
  showErrorMessage = false;
  errorMessages: NotificationLd[];

  public guessSchema(): void {
    this.isLoading = true;
    this.isError = false;
    this.restService.getGuessSchema(this.adapterDescription).subscribe(guessSchema => {
        this.isLoading = false;
        this.eventSchema = guessSchema.eventSchema;
        this.eventSchemaChange.emit(this.eventSchema);
        this.schemaGuess = guessSchema;

        this.oldEventSchema = this.eventSchema.copy();
        this.oldEventSchemaChange.emit(this.oldEventSchema);

        this.datasource = new ArrayDataSource(this.buildFileTree(this.eventSchema));

        this.isEditable = true;
        this.isEditableChange.emit(true);
    },
    error => {
        this.errorMessages = error.notifications;
        this.isError = true;
        this.isLoading = false;
        this.eventSchema = new EventSchema();
    });

  }

  buildFileTree(obj: EventProperty | EventSchema, level: number = -1, parentId: string = '0'): ExampleNode[] {
    let nodeArray: ExampleNode[] = new Array<ExampleNode>();
    if (obj instanceof EventSchema) {
      for (let eventProperty of obj.eventProperties) {
        nodeArray = nodeArray.concat(this.buildFileTree(eventProperty, level+1, parentId));
      }
    } else if (obj instanceof EventProperty) {
      if (obj instanceof EventPropertyPrimitive) {
        let node = obj as unknown as ExampleNode;
        node.level = level;
        nodeArray.push(node);
      } else if (obj instanceof EventPropertyList) {
        let node = obj as unknown as ExampleNode;
        node.level = level;
        nodeArray.push(node);
      } else if (obj instanceof EventPropertyNested) {
        let node = obj as unknown as ExampleNode;
        node.level = level;
        nodeArray.push(node);
        for (let eventProperty of obj.eventProperties) {
          nodeArray = nodeArray.concat(this.buildFileTree(eventProperty, level+1, parentId));
        }
      }
    }
    return nodeArray;
  }

  buildEventSchema(nodes: ExampleNode[]): EventSchema {
    const schema = this.eventSchema;
    schema.eventProperties = new Array<EventProperty>();
    let lastLevel = 0;
    const lastNested = new Array<EventPropertyNested>();
    lastNested[-1] = schema as unknown as EventPropertyNested;
    nodes.forEach((node, index) => {
      if (node.level == lastLevel) {
        lastNested[lastLevel-1].eventProperties.push(node);
      } else if (node.level > lastLevel) {
        lastNested[lastLevel] = nodes[index-1] as unknown as EventPropertyNested;
        lastNested[lastLevel].eventProperties = new Array<EventProperty>();
        lastLevel = node.level;
        lastNested[lastLevel-1].eventProperties.push(node);
      } else if (node.level < lastLevel) {
        lastLevel = node.level;
        lastNested[lastLevel-1].eventProperties.push(node);
      }
    });
    return schema;
  }

  ngOnInit() {
    if (!this.eventSchema) {
      this.eventSchema = new EventSchema();
    }

  }

  drop(event: CdkDragDrop<EventProperty>) {
    let changedData: ExampleNode[];
    this.datasource.connect().subscribe(res => {
      changedData = res as ExampleNode[];
      let oldData = changedData.splice(event.previousIndex, 1)[0];
      oldData.level = event.currentIndex == 0?0:changedData[event.currentIndex-1].level;
      changedData.splice(event.currentIndex, 0, oldData);
      let schema = this.buildEventSchema(changedData);
      console.log(schema)
      this.datasource = null
      this.datasource = new ArrayDataSource(this.buildFileTree(schema));
      this.changeDetectorRefs.detectChanges();
    })
}
}
