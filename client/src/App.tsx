import { Route, Link, Routes, useMatch } from "react-router-dom";
import "./App.css";
import { WorkLogs } from "./workLogs/WorkLogsIndex";
import TimerIndex from "./timers/TimerIndex";
import TimerActiveIndex from "./timers/TimerActiveIndex";
import WorkLogLayout from "./workLogs/WorkLogLayout";

function App() {
  const match = useMatch("/worklogs/:workLogId/*");
  const workLogId = match?.params.workLogId;

  return (
    <>
      <header className="header">
        <ul>
          <li className="nav-center">
            <Link className="nav-item" to={"/worklogs"}>
              Worklogs
            </Link>
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
          <li>
            <span className="nav-item">Registrace</span>
          </li>
          <li>
            <span className="nav-item">Přihlášení</span>
          </li>
        </ul>
      </header>
      <div className="page">
        <Routes>
          <Route path="/worklogs/*" element={<WorkLogs />} />
              <Route path=":workLogId" element={<WorkLogLayout />}>
                <Route path="summary" element={<TimerIndex />} />
                <Route path="active-timer" element={<TimerActiveIndex />} />
              </Route>
        </Routes>
      </div>
    </>
  );
}

export default App;
