import { StaticProperty } from '../../connect/model/StaticProperty';

export interface KviConfiguration {
    kviName: string;
    dataSetId: string;
    name: string;
    config: StaticProperty[];
}