export class StreampipesPeContainerConifgs{
    valueType: string;
    isPassword: boolean;
    description: string;
    key: string;
    value: string | number | boolean;
    modifiedKey: string;
    modifyKey(){
        var str1 = this.key;
        str1 = str1.replace(/SP/g,"");
        var str = str1.replace(/_/g," ");  
        return str
    }
}