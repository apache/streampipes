import {UserInput} from '../../support/model/UserInput';
import {AdapterUtils} from '../../support/utils/AdapterUtils';
import {SpecificAdapterInput} from '../../support/model/SpecificAdapterInput';
import {SpecificAdapterBuilder} from '../../support/builder/SpecificAdapterBuilder';

describe('Test Random Data Simulator Stream Adapter', function () {

    const adapterInput = SpecificAdapterBuilder
        .create('Random_Data_Simulator_\\(Stream\\)')
        .setName('Random Data Simulator Adapter Test')
        .addInput('input', 'wait-time-ms','1000')
        .build();

    AdapterUtils.testSpecificStreamAdapter(adapterInput);
});
