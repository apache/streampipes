import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AllAdaptersComponent} from './all.component';

describe('AllAdaptersComponent', () => {
  let component: AllAdaptersComponent;
  let fixture: ComponentFixture<AllAdaptersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AllAdaptersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AllAdaptersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
