import {AdapterUtils} from '../../support/utils/AdapterUtils';
import {SpecificAdapterBuilder} from '../../support/builder/SpecificAdapterBuilder';

describe('Test Random Data Simulator Stream Adapter', function () {

    const adapterInput = SpecificAdapterBuilder
        .create('Machine_Data_Simulator')
        .setName('Machine Data Simulator Test')
        .addInput('input', 'wait-time-ms','1000')
        .build();

    AdapterUtils.testSpecificStreamAdapter(adapterInput);

});