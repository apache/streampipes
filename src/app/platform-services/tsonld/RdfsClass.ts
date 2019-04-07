export function RdfsClass(propertyName: String) {
  return function(target: Function) {
    Reflect.defineMetadata('RdfsClass', propertyName, target);
  };
}
