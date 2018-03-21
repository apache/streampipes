import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EventPropertyPrimitiveComponent } from './event-property-primitive.component';

describe('EventPropertyPrimitiveComponent', () => {
  let component: EventPropertyPrimitiveComponent;
  let fixture: ComponentFixture<EventPropertyPrimitiveComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EventPropertyPrimitiveComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EventPropertyPrimitiveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
