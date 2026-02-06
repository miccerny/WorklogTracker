import { Route, Link, Routes } from "react-router-dom";
import "./App.css";
import { WorkLogs } from "./workLogs/WorkLogsIndex";
import TimerIndex from "./timers/TimerIndex";

function App() {
  return (
    <>
      <ul>
        <li className="navigation">
          <Link to={"/worklogs"}>Worklogs</Link>
        </li>
      </ul>
      <ul className="navigation">
        <li>Registrace</li>
        <li>Přihlášení</li>
      </ul>
      <Routes>
        <Route path="/worklogs" element={<WorkLogs />}/>
        <Route  path="/worklogs/:workLogId/summary" element={<TimerIndex />}/>
      </Routes>
    </>
  );
}

export default App;
