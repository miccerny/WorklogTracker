import { Link } from "react-router-dom";
import type { Timer } from "./TimerType";
import { formatLocalDateTime } from "./formatDateTime";

type TimerTableProps = {
  timerData: Timer[];
  label: string;
  errorState: string | null;
  format: (seconds: number) => string;
  workLogId: number | null;
};

const TimerTable = ({
  format,
  label,
  timerData,
  errorState,
  workLogId,
}: TimerTableProps) => {
  return (
    <>
      {errorState && <p>{errorState}</p>}
      <div className="card">
      <h2 className="section-title">{label}</h2>
      <hr className="divider"/>

      <ul>
        {timerData.map((timer) => {
          console.log("startedAt:", timer.createdAt);
          console.log("stoppedAt:", timer.stoppedAt);
        console.log("TIMER OBJ:", timer);
          return (
            <li key={timer.id}>
              <div>{format(timer.durationInSeconds)}</div>

              <div>Start: {formatLocalDateTime(timer.createdAt)}</div>

              <div>Status: {timer.status}</div>
            </li>
          );
        })}
      </ul>

      {workLogId && (
        <Link to={`/worklogs/${workLogId}/start-timer`}>
          <button className="btn btn-primary">Spustit nový timer</button>
        </Link>
      )}
      </div>
    </>
  );
};
export default TimerTable;
