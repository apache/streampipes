import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EventPropertyComponent } from './event-property.component';

describe('EventPropertyComponent', () => {
  let component: EventPropertyComponent;
  let fixture: ComponentFixture<EventPropertyComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EventPropertyComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EventPropertyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
