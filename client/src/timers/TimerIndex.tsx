import { useEffect, useState } from "react"
import { apiGet } from "../utils/api";
import TimerTable from "./TimerTable";
import type { Timer } from "./TimerType";
import { useParams } from "react-router-dom";

const TimerIndex = () => {

    const [timerState, setTimerstate] = useState<Timer[]>([]);
    const [errorState, setErrorState] = useState<string | null>(null);
    const {workLogId: workLogIdParam} = useParams<{ workLogId: string }>();
    const workLogId = workLogIdParam ? Number(workLogIdParam) : null;
    const loadTimer = async() => {
        const timerData= await apiGet<Timer[]>(`/worklogs/${workLogId}/summary`)
        .then((data) => {
            setTimerstate(data);

        })
        .catch((error) => {
            setErrorState(error);
        })
        return timerData;
    }

    useEffect(() => {
        if(workLogId === null || Number.isNaN(workLogId)) {
            setErrorState("Neplatné ID worklogu");
            return;
        } 
        loadTimer()

    },[workLogIdParam]);

    const formatDuration = (totalSeconds: number): string =>{
        const hours = Math.floor(totalSeconds/3600);
        const minutes = Math.floor((totalSeconds % 3600 /60));
        const seconds = totalSeconds /60;

        const pad = (n: number) => n.toString().padStart(2, "0");

        if(hours > 0 && minutes > 0){
            return `${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;
        } else if (minutes > 0){
        return `${pad(minutes)}:${pad(seconds)}`;
        }
        return `${pad(seconds)}`;
    }


    return (
        <>
        <TimerTable
            timerData = {timerState}
            label = "Časovač"
            errorState={errorState}
            format = {formatDuration}
        />
        </>
    )

}
export default TimerIndex;