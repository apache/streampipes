import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EventPropoertyListComponent } from './event-propoerty-list.component';

describe('EventPropoertyListComponent', () => {
  let component: EventPropoertyListComponent;
  let fixture: ComponentFixture<EventPropoertyListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EventPropoertyListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EventPropoertyListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
