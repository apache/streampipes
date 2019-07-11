export function RdfId(target: any, key: string) {
   Reflect.defineMetadata('RdfsId', 'rdfsId', target, key);
  Reflect.defineMetadata('TsId', key, target,'@id');
}
