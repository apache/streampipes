export interface ConsulService {
    name: string;
    mainKey: string;
    meta: {
        status: string;
    }
    configs: [{
        valueType: string;
        isPassword: boolean;
        description: string;
        key: string;
        value: string | number | boolean;
    }]
}