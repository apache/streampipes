import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProtocolFormComponent } from './protocol-form.component';

describe('ProtocolFormComponent', () => {
  let component: ProtocolFormComponent;
  let fixture: ComponentFixture<ProtocolFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProtocolFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProtocolFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
