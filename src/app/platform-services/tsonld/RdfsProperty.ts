export function RdfProperty(propertyName: string) {
  return (target: any, key: string) => {
    Reflect.defineMetadata('RdfProperty', propertyName, target, key);
    Reflect.defineMetadata('TsProperty', key, target, propertyName);
  };
}
