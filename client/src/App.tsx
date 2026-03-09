import { Link, Route, Routes, useMatch } from "react-router-dom";
import LoginPage from "./auth/LoginPage";
import RegistrationPage from "./auth/registration/ReqistrationPage";
import { useSession } from "./context/session";
import TimerActiveIndex from "./features/timers/ActiveTimerPage";
import TimerIndex from "./features/timers/TimerListPage";
import WorkLogForm from "./features/workLogs/WorkLogFormPage";
import WorkLogLayout from "./features/workLogs/WorkLogLayout";
import { WorkLogListPage } from "./features/workLogs/WorkLogListPage";
import { setAuthToken } from "./utils/api";
import "./App.css";

function App() {
  const { session, setSession } = useSession();
  const match = useMatch("/worklogs/:workLogId/*");
  const workLogId = match?.params.workLogId;
  const user = session.data;

  const handleLogout = () => {
    setAuthToken(null);
    setSession({ data: null, status: "unauthenticated" });
    window.location.assign("/auth/login");
  };

  return (
    <>
      <header className="header">
        <ul>
          <li className="nav-center">
            <Link className="nav-item" to="/worklogs">
              Worklogs
            </Link>
            {workLogId ? (
              <Link className="nav-item" to={`/worklogs/${workLogId}/summary`}>
                Časovače
              </Link>
            ) : (
              <span className="nav-item" style={{ opacity: 0.5, cursor: "not-allowed" }}>
                Časovače
              </span>
            )}
          </li>
        </ul>

        <ul className="nav-auth">
          {session.status === "authenticated" && user ? (
            <>
              <li className="nav-user">
                <strong>{user.name}</strong>
                <span>{user.username}</span>
              </li>
              <li>
                <button className="nav-item" type="button" onClick={handleLogout}>
                  Odhlásit
                </button>
              </li>
            </>
          ) : (
            <>
              <li>
                <Link className="nav-item" to="/auth/register">
                  Registrace
                </Link>
              </li>
              <li>
                <Link className="nav-item" to="/auth/login">
                  Přihlásit
                </Link>
              </li>
            </>
          )}
        </ul>
      </header>

      <div className="page">
        <Routes>
          <Route path="/auth">
            <Route path="register" element={<RegistrationPage />} />
            <Route path="login" element={<LoginPage />} />
          </Route>
          <Route path="/worklogs" element={<WorkLogListPage />} />
          <Route path="/worklogs/new" element={<WorkLogForm mode="create" />} />
          <Route path="/worklogs/:workLogId/edit" element={<WorkLogForm mode="edit" />} />
          <Route path="/worklogs/:workLogId" element={<WorkLogLayout />}>
            <Route path="summary" element={<TimerIndex />} />
            <Route path="active-timer" element={<TimerActiveIndex />} />
          </Route>
        </Routes>
      </div>
    </>
  );
}

export default App;
