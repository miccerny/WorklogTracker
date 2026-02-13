import { Link } from "react-router-dom";
import type { WorkLogType } from "./WorkLog.types";

/**
 * Props definition for WorkLogsTable component.
 *
 * workLogs  - array of WorkLog objects (data from API)
 * label     - custom label text displayed above the list
 * loading   - indicates whether data is currently loading
 * errorState - error message (empty string if no error)
 */
type WorkLog = {
  workLogs: WorkLogType[];
  label: string;
  loading: boolean;
  errorState: string;
};

/**
 * WorkLogsTable component.
 *
 * This component is responsible only for rendering UI.
 * It receives data via props and does not handle fetching logic.
 *
 * Responsibilities:
 * - Show loading state
 * - Show error message
 * - Render list of WorkLogs
 * - Provide navigation links (summary & edit)
 */
const WorkLogsTable = ({ workLogs, label, loading, errorState }: WorkLog) => {
  return (
    <>
      {/* Static heading for section */}
      <h4>WorkLogs</h4>

      {/* Conditional rendering:
          If loading === true, show loading text */}
      {loading && <p>Načítám...</p>}

      {/* If errorState is not empty string, display error in red */}
      {errorState && <p style={{ color: "red" }}>{errorState}</p>}

      {/* Display label + number of worklogs */}
      <p>
        {label}
        {workLogs.length}
      </p>
      
      {/* Render list of worklogs */}
      <ul>
        {workLogs.map((workLog) => (
          // key is required by React to efficiently track list items
          <li key={workLog.id}>
            {/* Link to summary page of specific WorkLog */}
            <Link to={`/worklogs/${workLog.id}/summary`}>
              {workLog.workLogName}
            </Link>

            {/* Link to edit page of specific WorkLog */}
            <Link to={`/worklogs/${workLog.id}/edit`}>Upravit</Link>
          </li>
        ))}
      </ul>
    </>
  );
};
export default WorkLogsTable;
