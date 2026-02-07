import { Route, Link, Routes } from "react-router-dom";
import "./App.css";
import { WorkLogs } from "./workLogs/WorkLogsIndex";
import TimerIndex from "./timers/TimerIndex";

function App() {
  return (
    <>
    <header className="header">
      <ul>
        <li className="nav-center">
          <Link className="nav-item" to={"/worklogs"}>Worklogs</Link>
        </li>
      </ul>
      <ul className="nav-auth">
        <li><span className="nav-item">Registrace</span></li>
        <li><span className="nav-item">Přihlášení</span></li>
      </ul>
      </header>
      <div className="page">
      <Routes>
        <Route path="/worklogs" element={<WorkLogs />}/>
        <Route  path="/worklogs/:workLogId/summary" element={<TimerIndex />}/>
      </Routes>
      </div>
    </>
  );
}

export default App;
