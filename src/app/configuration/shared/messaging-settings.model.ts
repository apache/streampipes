
export interface MessagingSettings {
    batchSize: number;
    messageMaxBytes: number;
    lingerMs: number;
    acks: number;

   prioritizedFormats: [string];
}