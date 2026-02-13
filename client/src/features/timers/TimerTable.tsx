import { Link } from "react-router-dom";
import type { Timer } from "./Timer.types";
import { formatLocalDateTime } from "./formatDateTime";

/**
 * Props definition for TimerTable component.
 *
 * timerData  - list of timers (already loaded from API)
 * label      - section title displayed above the list
 * errorState - error message (null means no error)
 * format     - function to format duration (seconds -> readable string)
 * workLogId  - current WorkLog ID (used for navigation link)
 */
type TimerTableProps = {
  timerData: Timer[];
  label: string;
  errorState: string | null;
  format: (seconds: number) => string;
  workLogId: number | null;
};

/**
 * TimerTable component.
 *
 * Responsibility:
 * - Render list of timers
 * - Show error state if exists
 * - Show "empty state" if no timers
 * - Provide button to start new timer
 *
 * This component is purely presentational.
 * It does not fetch data itself.
 */
const TimerTable = ({
  format,
  label,
  timerData,
  errorState,
  workLogId,
}: TimerTableProps) => {
  /**
   * If no timers exist, show simple empty state.
   * Note: this check runs before error check.
   */
  if (timerData.length === 0) {
    return <div>Nic nezměřeno</div>;
  }

  /**
   * If error exists, show it.
   * This returns early and prevents table rendering.
   */
  if (errorState) {
    return <div style={{ color: "crimson" }}>{errorState}</div>;
  }
  return (
    <>
      {/* Redundant error rendering (will normally not execute
          because of early return above). Kept as-is per original logic. */}
      {errorState && <p>{errorState}</p>}
      <div className="card">
        {/* Section title */}
        <h2 className="section-title">{label}</h2>

        <hr className="divider" />

        {/* List of timers */}
        <ul>
          {timerData.map((timer) => {
            // Debug logs for development purposes
            console.log("startedAt:", timer.createdAt);
            console.log("stoppedAt:", timer.stoppedAt);
            console.log("TIMER OBJ:", timer);
            return (
              // key is required for React list rendering optimization
              <li key={timer.id}>
                {/* Duration formatted using injected function */}
                <div>{format(timer.durationInSeconds)}</div>

                {/* Formatted start date/time */}
                <div>Start: {formatLocalDateTime(timer.createdAt)}</div>

                {/* Timer status (e.g. RUNNING, STOPPED) */}
                <div>Status: {timer.status}</div>
              </li>
            );
          })}
        </ul>

        {/* If workLogId exists, show navigation to active-timer route */}
        {workLogId && (
          <Link to={`/worklogs/${workLogId}/active-timer`}>
            <button className="btn btn-primary">Spustit nový timer</button>
          </Link>
        )}
      </div>
    </>
  );
};
export default TimerTable;
