import { useCallback, useEffect, useState } from "react";
import { apiGet } from "../../utils/api";
import TimerTable from "./TimerTable";
import type { Timer } from "./Timer.types";
import { Link, useParams } from "react-router-dom";
import formatDuration from "./formatDuration";

/**
 * TimerIndex component.
 *
 * This page/component:
 * - loads timer summary data for a given WorkLog (workLogId from URL)
 * - stores result in state
 * - renders TimerTable with formatted duration
 *
 * It also shows a link to a sibling route for creating/starting a timer.
 */
const TimerIndex = () => {
  /**
   * timerState holds list of timers for the worklog summary.
   * Starts as empty array until loaded from API.
   */
  const [timerState, setTimerstate] = useState<Timer[]>([]);
  /**
   * timerState holds list of timers for the worklog summary.
   * Starts as empty array until loaded from API.
   */
  const [errorState, setErrorState] = useState<string | null>(null);

  /**
   * Read workLogId from route params (string by default).
   * Example route could be: /worklogs/:workLogId/summary
   */
  const { workLogId: workLogIdParam } = useParams<{ workLogId: string }>();

  /**
   * Read workLogId from route params (string by default).
   * Example route could be: /worklogs/:workLogId/summary
   */
  const workLogId = workLogIdParam ? Number(workLogIdParam) : null;
  const loadTimer = useCallback(async () => {
    // Debug counter to see how often this function is called
    console.count("TimerListPage render");

    /**
     * Call API and set state based on result.
     *
     * Important note:
     * - apiGet returns a Promise
     * - we use .then + .catch for success/fail
     * - setTimerstate triggers rerender with new data
     */
    const timerData = await apiGet<Timer[]>(`/worklogs/${workLogId}/summary`)
      .then((data) => {
        setTimerstate(data);
        console.log(data);
      })
      .catch((error) => {
        // Store error message (depends on what apiGet throws/returns)
        setErrorState(error);
      });
    return timerData;
  }, [workLogId]);

  /**
   * useEffect runs when component mounts and when dependencies change.
   *
   * Here it:
   * - validates workLogId
   * - triggers loading data for that workLog
   */
  useEffect(() => {
    console.count("TimerListPage effect");

    // Validation: make sure workLogId is a valid number.
    if (workLogId === null || Number.isNaN(workLogId)) {
      setErrorState("Neplatné ID worklogu");
      return;
    }

    // Load timers for current workLogId.
    loadTimer();
  }, [workLogId, loadTimer]);

  // Debug logs to see state changes during development
  console.log(workLogId, timerState);

  return (
    <>
      <div style={{ display: "flex", gap: 12, marginBottom: 12 }}>
        {/* Relativní přechod na sibling route.
            "../active-timer" means: go one level up and then to active-timer.
            This is useful when you are inside nested routes. */}
        <Link to="../active-timer">Vytvořit timer</Link>
      </div>

      {/* TimerTable is a presentational component.
          We pass:
          - timer data
          - label
          - error message
          - formatting function for duration
          - workLogId (might be used for links/actions inside table) */}
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
