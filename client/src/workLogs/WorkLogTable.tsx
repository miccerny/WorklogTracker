import { Link } from "react-router-dom";
import type { WorkLog } from "./typeWorkLog";

const WorkLogsTable = ({workLogs, label}: {workLogs: WorkLog[], label: string}) => {

  return (
    <>
    <p>{label}{workLogs.length}</p>
    
    <ul>
      {workLogs.map((workLog) => (
        <li key={workLog.id}>
          <Link to={`/worklogs/${workLog.id}/summary`}>
          {workLog.workLogName}
          </Link>
          </li>
      ))}
      </ul>
    </>
  );
};
export default WorkLogsTable;