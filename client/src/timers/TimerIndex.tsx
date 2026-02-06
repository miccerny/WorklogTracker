import { useEffect, useState } from "react"
import { apiGet } from "../utils/api";
import TimerTable from "./TimerTable";
import type { Timer } from "./TimerType";
import { useParams } from "react-router-dom";

export const TimerIndex = () => {

    const [timerState, setTimerstate] = useState<Timer[]>([]);
    const [errorState, setErrorState] = useState<string | null>(null);
    const {workLogId} = useParams<{ workLogId: string }>();
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
        if(!workLogId) return;
        loadTimer()

    },[workLogId]);


    return (
        <>
        <TimerTable
            timerData = {timerState}
            label = "Časovač"
        />
        </>
    )

}
export default TimerIndex;