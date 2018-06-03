export interface VersionInfo {
    backendVersion: string;
    itemVersions: [{
        itemType: string;
        itemName: string;
        itemVersion: string;
    }]
}