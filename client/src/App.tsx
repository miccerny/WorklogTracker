<<<<<<< Updated upstream
import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'

function App() {
  const [count, setCount] = useState(0)
=======
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
import WorkLogForm from "./features/workLogs/WorkLogFormPage";

// Timer pages
import TimerIndex from "./features/timers/TimerListPage";
import TimerActiveIndex from "./features/timers/ActiveTimerPage";
import LoginPage from "./auth/LoginPage";
import RegistrationPage from "./auth/ReqistrationPage";
import { useSession } from "./context/session";
import { setAuthToken } from "./utils/api";
import { useFlash } from "./context/flash";

function App() {
  const { session, setSession } = useSession();
  const { showFlash } = useFlash();

   /**
   * We want to show "Časovače" link only when user is inside a specific WorkLog.
   *
   * useMatch checks current URL against a pattern and returns params if it matches.
   * Pattern "/worklogs/:workLogId/*" means:
   * - workLogId is available for any nested route under /worklogs/:workLogId/...
   */
  const match = useMatch("/worklogs/:workLogId/*");
  const workLogId = match?.params.workLogId;
>>>>>>> Stashed changes

  const handleLogout = () => {
    setAuthToken(null);
    setSession({
      data: null,
      status: "unauthenticated",
    });
    showFlash("info", "Byl jste odhlášen");
  };

  return (
    <>
<<<<<<< Updated upstream
      <div>
        <a href="https://vite.dev" target="_blank">
          <img src={viteLogo} className="logo" alt="Vite logo" />
        </a>
        <a href="https://react.dev" target="_blank">
          <img src={reactLogo} className="logo react" alt="React logo" />
        </a>
=======
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

        <ul className="nav-auth">
          {session.status === "authenticated" ? (
            <>
              <li>
                <span className="nav-item">Přihlášen</span>
              </li>
              <li>
                <button
                  className="nav-item nav-button"
                  type="button"
                  onClick={handleLogout}
                >
                  Odhlásit
                </button>
              </li>
            </>
          ) : (
            <>
              <li>
                <Link className="nav-item" to="/register">
                  Registrace
                </Link>
              </li>
              <li>
                <Link className="nav-item" to="/login">
                  Přihlášení
                </Link>
              </li>
            </>
          )}
        </ul>
      </header>

      {/* Main content container */}
      <div className="page">
        <Routes>

          {/* WorkLog list */}
          <Route path="/worklogs" element={<WorkLogListPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegistrationPage />} />

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
>>>>>>> Stashed changes
      </div>
      <h1>Vite + React</h1>
      <div className="card">
        <button onClick={() => setCount((count) => count + 1)}>
          count is {count}
        </button>
        <p>
          Edit <code>src/App.tsx</code> and save to test HMR
        </p>
      </div>
      <p className="read-the-docs">
        Click on the Vite and React logos to learn more
      </p>
    </>
  )
}

export default App
