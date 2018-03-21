import { TestBed, inject } from '@angular/core/testing';

import { DataTypesService } from './data-type.service';

describe('DataTypesService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DataTypesService]
    });
  });

  it('three data types', inject([DataTypesService], (service: DataTypesService) => {
    expect(service.getDataTypes().length).toBe(3);
  }));

  it('get correct label', inject([DataTypesService], (service: DataTypesService) => {
    expect(service.getLabel('http://www.w3.org/2001/XMLSchema#string'))
      .toBe('String - A textual datatype, e.g., \'machine1\'');
    expect(service.getLabel('http://www.w3.org/2001/XMLSchema#boolean'))
      .toBe('Boolean - A true/false value');
    expect(service.getLabel('http://schema.org/Number'))
      .toBe('Number - A number, e.g., \'1.25\'');
  }));

  it('get url', inject([DataTypesService], (service: DataTypesService) => {
    expect(service.getUrl(0))
      .toBe('http://www.w3.org/2001/XMLSchema#string');
    expect(service.getUrl(1))
      .toBe('http://www.w3.org/2001/XMLSchema#boolean');
    expect(service.getUrl(2))
      .toBe('http://schema.org/Number');
  }));
});
