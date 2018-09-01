import { Injectable } from '@angular/core';
import { AdapterDescription } from './model/connect/AdapterDescription';

@Injectable()
export class ConnectService {
  isDataStreamDescription(adapter: AdapterDescription): boolean {
    return adapter.constructor.name.includes('AdapterStreamDescription');
  }

  isDataSetDescription(adapter: AdapterDescription): boolean {
    return adapter.constructor.name.includes('AdapterSetDescription');
  }

  isGenericDescription(adapter: AdapterDescription): boolean {
    return adapter.id.includes('generic');
  }

  isSpecificDescription(adapter: AdapterDescription): boolean {
    return adapter.id.includes('specific');
  }
}
