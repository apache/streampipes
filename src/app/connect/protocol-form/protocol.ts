export class Protocol {

  protocols = [
    'HTTP',
    'File',
  ];

  public properties: {url: String, selected: String} = {url: '', selected: ''};
  constructor(
    public url: string,
    public selected: string,
    public type: string = 'org.streampipes.streamconnect.model.adapter.Protocol'
  ) {
    this.properties.url = url;
    this.properties.selected = url;
  }

}
