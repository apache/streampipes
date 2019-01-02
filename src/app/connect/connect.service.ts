import { Injectable } from '@angular/core';
import { AdapterDescription } from './model/connect/AdapterDescription';
import {GenericAdapterSetDescription} from './model/connect/GenericAdapterSetDescription';
import {GenericAdapterStreamDescription} from './model/connect/GenericAdapterStreamDescription';
import {SpecificAdapterStreamDescription} from './model/connect/SpecificAdapterStreamDescription';
import {SpecificAdapterSetDescription} from './model/connect/SpecificAdapterSetDescription';

@Injectable()
export class ConnectService {
  isDataStreamDescription(adapter: AdapterDescription): boolean {
      return adapter instanceof SpecificAdapterStreamDescription || adapter instanceof GenericAdapterStreamDescription;
  }

  isDataSetDescription(adapter: AdapterDescription): boolean {
      return adapter instanceof SpecificAdapterSetDescription || adapter instanceof GenericAdapterSetDescription;
  }

  isGenericDescription(adapter: AdapterDescription): boolean {
      return adapter instanceof GenericAdapterSetDescription || adapter instanceof GenericAdapterStreamDescription;
  }

  isSpecificDescription(adapter: AdapterDescription): boolean {
      return adapter instanceof SpecificAdapterSetDescription || adapter instanceof SpecificAdapterStreamDescription;
  }
}
