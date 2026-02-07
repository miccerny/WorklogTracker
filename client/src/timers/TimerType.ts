export type Timer = ({
    id: number;
    workLogId: number;
    createdAt: string;
    stoppedAt: string | null;
    durationInSeconds: number;
    status: boolean;
})