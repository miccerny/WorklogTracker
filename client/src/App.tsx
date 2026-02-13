/**
 * App-level routing + global layout (header + page container).
 *
 * Responsibilities:
 * - Show main navigation (WorkLogs + Timers link depending on selected WorkLog)
 * - Define application routes using React Router v6
 * - Keep router-dependent UI (like active WorkLog context) at the top level
 */
import { Route, Link, Routes, useMatch } from "react-router-dom";
import "./App.css";

// Pages / layouts
import { WorkLogListPage} from "./features/workLogs/WorkLogListPage";
import WorkLogLayout from "./features/workLogs/WorkLogLayout";
import WorkLogForm from "./features/workLogs/WorkLogForm";

// Timer pages
import TimerIndex from "./features/timers/TimerListPage";
import TimerActiveIndex from "./features/timers/ActiveTimerPage";

function App() {

   /**
   * We want to show "Časovače" link only when user is inside a specific WorkLog.
   *
   * useMatch checks current URL against a pattern and returns params if it matches.
   * Pattern "/worklogs/:workLogId/*" means:
   * - workLogId is available for any nested route under /worklogs/:workLogId/...
   */
  const match = useMatch("/worklogs/:workLogId/*");
  const workLogId = match?.params.workLogId;

  return (
    <>
      <header className="header">
        {/* Main navigation area */}
        <ul>
          <li className="nav-center">
            {/* Always available: list of WorkLogs */}
            <Link className="nav-item" to={"/worklogs"}>
              Worklogs
            </Link>

            {/* 
              Timers link depends on whether we currently have a WorkLog context.
              If we don't have workLogId (we're not inside a specific WorkLog),
              we render a disabled-looking label instead of a clickable link.
            */}
            {workLogId ? (
              <Link className="nav-item" to={`/worklogs/${workLogId}/summary`}>
                Časovače
              </Link>
            ) : (
              <span
                className="nav-item"
                style={{ opacity: 0.5, cursor: "not-allowed" }}
              >
                Časovače
              </span>
            )}
          </li>
        </ul>

        {/* Right-side auth placeholder (not wired yet) */}
        <ul className="nav-auth">
          <li>
            <span className="nav-item">Registrace</span>
          </li>
          <li>
            <span className="nav-item">Přihlášení</span>
          </li>
        </ul>
      </header>

      {/* Main content container */}
      <div className="page">
        <Routes>

          {/* WorkLog list */}
          <Route path="/worklogs" element={<WorkLogListPage />} />

          {/* WorkLog create */}
          <Route path="/worklogs/new" element={<WorkLogForm mode="create" />} />

           {/* WorkLog edit */}
          <Route
            path="/worklogs/:workLogId/edit"
            element={<WorkLogForm mode="edit" />}
          />

          {/* 
            WorkLog "detail" route acts as a layout for nested timer pages.
            WorkLogLayout typically renders <Outlet /> for children.
          */}
          <Route path="/worklogs/:workLogId" element={<WorkLogLayout />}>

            {/* Timers overview for selected WorkLog */}
            <Route path="summary" element={<TimerIndex />} />

            {/* Active timer page for selected WorkLog */}
            <Route path="active-timer" element={<TimerActiveIndex />} />
          </Route>
        </Routes>
      </div>
    </>
  );
}

export default App;
