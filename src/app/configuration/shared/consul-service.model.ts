import { ConsulServiceConfigs } from "./consul-service-configs";
//ConsulService = StreampipesPeContainer
export interface ConsulService {
    name: string;
    mainKey: string;
    meta: {
        status: string;
    }
    configs: [ConsulServiceConfigs];
}