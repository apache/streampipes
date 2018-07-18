import { StreampipesPeContainerConifgs } from "./streampipes-pe-container-configs";
//ConsulService = StreampipesPeContainer ERLEDIGT
export interface StreampipesPeContainer {
    name: string;
    mainKey: string;
    meta: {
        status: string;
    }
    configs: [StreampipesPeContainerConifgs];
}