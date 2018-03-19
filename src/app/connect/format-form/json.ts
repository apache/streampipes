import {Format} from './format';

export class Json extends Format {

  public properties: {key: String} = {'key': ''};
  constructor(
    public key: string,
    public name: string,
  ) {
    super('org.streampipes.streamconnect.model.adapter.JsonFormat');
    this.properties.key = key;
  }
}
