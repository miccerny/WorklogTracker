import { useCallback, useEffect, useMemo, useState } from "react";
import type { Timer } from "./Timer.types";
import { apiGet, apiPost } from "../../utils/api";
import { Link, useParams } from "react-router-dom";
import TimerActiveTable from "./ActiveTimerPanel";
import formatDuration from "./formatDuration";
import { useFlash } from "../../context/flash";

/**
 * TimerActiveIndex component.
 *
 * Purpose:
 * - Display "active timer" panel for a specific WorkLog
 * - Allow starting and stopping timer via API calls
 * - Show live elapsed time (ticks every second while timer is RUNNING)
 *
 * Data flow:
 * - Loads timers summary for workLogId
 * - Finds RUNNING timer (if exists)
 * - Computes elapsed time from its createdAt
 */
const TimerActiveIndex = () => {
  /**
   * Holds timers list loaded from API.
   * We use it to find active (RUNNING) timer.
   */
  const [timerActiveState, setTimerActiveState] = useState<Timer[]>([]);

  /**
   * Holds error message to show in UI (null = no error).
   */
  const [errorState, setErrorState] = useState<string | null>(null);

  /**
   * tick is used as a simple "time signal" to recompute elapsed time each second.
   * When isRunning is true, setInterval increments tick every 1000ms.
   */
  const [tick, setTick] = useState(0);

  /**
   * Read workLogId from route params (string).
   * Example route: /worklogs/:workLogId/active-timer
   */
  const { workLogId: workLogIdParam } = useParams<{ workLogId: string }>();

  /**
   * Convert param to number (or null if missing).
   */
  const workLogId = workLogIdParam ? Number(workLogIdParam) : null;

  const {showFlash} = useFlash();

  /**
   * Loads timers data from backend for current workLogId.
   *
   * Wrapped in useCallback so the function reference is stable,
   * which helps when used inside useEffect.
   */
  const loadTimer = useCallback(async () => {
    try {
      setErrorState(null);

      // Fetch timers summary for this worklog
      const data = await apiGet<Timer[]>(`/worklogs/${workLogId}/summary`);

      // Store timers into state so UI can render and compute activeTimer
      setTimerActiveState(data);
    } catch (e: any) {
      // Convert error to readable message
      setErrorState(e?.message ?? String(e));
    }
  }, [workLogId]);

  /**
   * On mount / when workLogId changes:
   * - validate ID
   * - load current timers
   */
  useEffect(() => {
    if (workLogId === null || Number.isNaN(workLogId)) {
      setErrorState("Neplatné ID worklogu");
      return;
    }
    loadTimer();
  }, [workLogId, loadTimer]);

  /**
   * Compute current active timer.
   *
   * useMemo prevents re-filtering on every render unless timerActiveState changes.
   */
  const activeTimer = useMemo(() => {
    return timerActiveState.find((timer) => timer.status === "RUNNING") ?? null;
  }, [timerActiveState]);

  /**
   * Boolean flag: is there a RUNNING timer?
   */
  const isRunning = Boolean(activeTimer);

  /**
   * Interval effect for live ticking.
   *
   * When timer is running, start interval that increases tick each second.
   * When timer stops or component unmounts, clear interval (cleanup).
   */
  useEffect(() => {
    if (!isRunning) return;

    // Create interval
    const id = window.setInterval(() => setTick((t) => t + 1), 1000);

    // Cleanup: stop interval
    return () => window.clearInterval(id);
  }, [isRunning]);

  /**
   * Extract startedAt string from activeTimer (or null if no active timer).
   *
   * Note: your Timer type uses "createdAt" as the start timestamp.
   */
  const startedAt = activeTimer?.createdAt ?? null;

  /**
   * Compute elapsed seconds based on startedAt.
   *
   * Dependencies:
   * - startedAt (when active timer changes)
   * - tick (forces recomputation every second while running)
   */
  const elapsedSeconds = useMemo(() => {
    if (!startedAt) return 0;

    // Convert start time to milliseconds
    const start = new Date(startedAt).getTime();
    // Current time in milliseconds
    const now = Date.now();
    // Convert difference to seconds
    return Math.floor((now - start) / 1000);
  }, [startedAt, tick]);

  /**
   * Formatted text shown in UI.
   * If not started, show default "00:00:00".
   */
  const timeText = startedAt ? formatDuration(elapsedSeconds) : "00:00:00";

  /**
   * Start timer action.
   *
   * Calls backend endpoint and reloads timers afterwards.
   */
  const onStart = async () => {
    if (!workLogId) return;
    try {
      setErrorState(null);

      // Start timer endpoint (request body is empty object)
      await apiPost<Timer, Record<string, never>>(
        `/worklogs/${workLogId}/startTimer`,
        {},
      );

      // Reload timers so UI gets RUNNING timer and starts ticking
      await loadTimer();
    } catch (e: any) {
      setErrorState(e?.message ?? String(e));
    }
  };

  /**
   * Stop timer action.
   *
   * Calls backend endpoint and reloads timers afterwards.
   */
  const onStop = async () => {
    if (!workLogId) return;

    try {
      setErrorState(null);
      // Stop timer endpoint (request body is empty object)
      await apiPost<Timer, Record<string, never>>(
        `/worklogs/${workLogId}/stopTimer`,
        {},
      );

      // Reload timers so UI shows STOPPED result and stops ticking
      await loadTimer();
      showFlash("success", "Časovač ukončen a uložen", 2000);
    } catch (e: any) {
      setErrorState(e?.message ?? String(e));
    }
  };

  return (
    <>
      {/* Simple navigation back to summary route */}
      <div style={{ display: "flex", gap: 12, marginBottom: 12 }}>
        <Link to="../summary">Zpět na souhrn</Link>
      </div>

      {/* Presentational component that displays timer UI and buttons */}
      <TimerActiveTable
        timeText={timeText}
        isRunning={isRunning}
        errorState={errorState}
        onStart={onStart}
        onStop={onStop}
      />
    </>
  );
};

export default TimerActiveIndex;
