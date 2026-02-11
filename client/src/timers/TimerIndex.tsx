import { useEffect, useState } from "react";
import { apiGet } from "../utils/api";
import TimerTable from "./TimerTable";
import type { Timer } from "./TimerType";
import { Link, useParams } from "react-router-dom";
import formatDuration from "./formatDuration";


const TimerIndex = () => {
  const [timerState, setTimerstate] = useState<Timer[]>([]);
  const [errorState, setErrorState] = useState<string | null>(null);
  const { workLogId: workLogIdParam } = useParams<{ workLogId: string }>();
  const workLogId = workLogIdParam ? Number(workLogIdParam) : null;
  const loadTimer = async () => {
    const timerData = await apiGet<Timer[]>(`/worklogs/${workLogId}/summary`)
      .then((data) => {
        setTimerstate(data);
      })
      .catch((error) => {
        setErrorState(error);
      });
    return timerData;
  };

  useEffect(() => {
    if (workLogId === null || Number.isNaN(workLogId)) {
      setErrorState("Neplatné ID worklogu");
      return;
    }
    loadTimer();
  }, [workLogId]);


  return (
    <>
      <div style={{ display: "flex", gap: 12, marginBottom: 12 }}>
        {/* relativní přechod na sibling route */}
        <Link to="../active-timer">Vytvořit timer</Link>
      </div>

      <TimerTable
        timerData={timerState}
        label="Souhrn stopek"
        errorState={errorState}
        format={formatDuration}
        workLogId={workLogId}
      />
    </>
  );
};
export default TimerIndex;
