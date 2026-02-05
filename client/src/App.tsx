import { Route, Link, Routes } from "react-router-dom";
import "./App.css";
import { WorkLogs } from "./workLogs/WorkLogsIndex";

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
      </Routes>
    </>
  );
}

export default App;
