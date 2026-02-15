import { Link } from "react-router-dom";
import type { WorkLogType } from "./WorkLog.types";
import "./WorkLogList.css"
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
const WorkLogListPanel = ({
  workLogs,
  label,
  loading,
  errorState,
}: WorkLog) => {
  return (
    <>
    {/* Navigates to WorkLog create form */}
      <Link className="wl__action btn" to="/worklogs/new">+ Vytvořit worklog</Link>
      <section className="wl card">
        <div className="wl__header">
          {/* Static heading for section */}
          <h4 className="wl__title">WorkLogs</h4>

          {/* Little badge with count of worklogs*/}
          <span className="wl__count" title="Počet Worklogů">
            {workLogs.length}
          </span>
        </div>

        {/* Display label + number of worklogs */}
        <p className="wl__meta muted">
          {label}
          {workLogs.length}
        </p>

        {/* Conditional rendering:
          If loading === true, show loading text */}
        {loading && <p className="wl__state muted">Načítám...</p>}

        {/* If errorState is not empty string, display error in red */}
        {errorState && (
          <p className="wl__state wl_state--error">{errorState}</p>
        )}

        {/* Render list of worklogs */}
        <ul className="wl__list">
          {workLogs.map((workLog) => (
            // key is required by React to efficiently track list items
            <li className="wl__item" key={workLog.id}>
              {/* Link to summary page of specific WorkLog */}
              <Link className="wl__name" to={`/worklogs/${workLog.id}/summary`}>
                {workLog.workLogName}
              </Link>

              {/* Link to edit page of specific WorkLog */}
              <div className="wl__actions">
                <Link className="wl__action btn" to={`/worklogs/${workLog.id}/edit`}>Upravit</Link>
              </div>
            </li>
          ))}
        </ul>
      </section>
    </>
  );
};
export default WorkLogListPanel;
