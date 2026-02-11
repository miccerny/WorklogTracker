import { useEffect, useMemo, useState } from "react";
import type { Timer } from "./TimerType";
import { apiGet, apiPost } from "../utils/api";
import { Link, useParams } from "react-router-dom";
import TimerActiveTable from "./TimerActiveTable";
import formatDuration from "./formatDuration";

const TimerActiveIndex = () => {
  const [timerActiveState, setTimerActiveState] = useState<Timer[]>([]);
  const [erorState, setErrorState] = useState<string | null>(null);

  const [tick, setTick] = useState(0);

  const { workLogId: workLogIdParam } = useParams<{ workLogId: string }>();
  const workLogId = workLogIdParam ? Number(workLogIdParam) : null;

  const loadTimerActiveStart = async () => {
    try {
      setErrorState(null);
      const data = await apiGet<Timer[]>(`/worklogs/${workLogId}/active-timer`);
      setTimerActiveState(data);
    } catch (e: any) {
      setErrorState(e?.message ?? String(e));
    }
  };

  useEffect(() => {
    if (workLogId === null || Number.isNaN(workLogId)) {
      setErrorState("Neplatné ID worklogu");
      return;
    }
    loadTimerActiveStart();
  }, [workLogId]);

  const activeTimer = useMemo(() => {
    return timerActiveState.find((timer) => timer.status === "RUNNING") ?? null;
  }, [timerActiveState]);

  const isRunning = Boolean(activeTimer);

  useEffect(() => {
    if (!isRunning) return;
    const id = window.setInterval(() => setTick((t) => t + 1), 1000);
    return () => window.clearInterval(id);
  }, [isRunning]);

  const startedAt = activeTimer?.createdAt ?? null;

  const elapsedSeconds = useMemo(() => {
    if (!startedAt) return 0;
    const start = new Date(startedAt).getTime();
    const now = Date.now();
    return Math.floor((now - start) / 1000);
  }, [startedAt, tick]);

  const timeText = startedAt ? formatDuration(elapsedSeconds) : "00:00:00";

  const onStart = async () => {
    if (!workLogId) return;
    try {
      setErrorState(null);

      await apiPost<Timer, Record<string, never>>(
        `/worklogs/${workLogId}/startTimer`,
        {},
      );
      await loadTimerActiveStart();
    } catch (e: any) {
      setErrorState(e?.message ?? String(e));
    }
  };

  const onStop = async () => {
    if (!workLogId) return;

    try {
      setErrorState(null);

      await apiPost<Timer, Record<string, never>>(
        `/worklogs/${workLogId}/stopTimer`,
        {},
      );
      await loadTimerActiveStart();
    } catch (e: any) {
      setErrorState(e?.message ?? String(e));
    }
  };

  return (
    <>
      <div style={{ display: "flex", gap: 12, marginBottom: 12 }}>
        <Link to="../summary">Zpět na souhrn</Link>
      </div>

      <TimerActiveTable
        timeText={timeText}
        isRunning={isRunning}
        erroState={erorState}
        onStart={onStart}
        onStop={onStop}
      />
    </>
  );
};

export default TimerActiveIndex;
