import {Format} from './format';

export class Csv extends Format {

  public properties: {offset: number, delimiter: String} = {'offset': 0, 'delimiter': ','};
  constructor(
    public delimiter: string,
    public offset: number,
    public name: string,
  ) {
    super('org.streampipes.streamconnect.model.adapter.CsvFormat');
    this.properties.delimiter = delimiter;
    this.properties.offset = offset;
  }

}
