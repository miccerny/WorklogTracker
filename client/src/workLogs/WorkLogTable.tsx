import type { WorkLog } from "./typeWorkLog";

const WorkLogsTable = ({workLogs, label}: {workLogs: WorkLog[], label: string}) => {
  return (
    <>
    <p>{label}</p>
      {workLogs.map((workLog) => (
        <li key={workLog.id}>{workLog.workLogName}</li>
      ))}
      
    </>
  );
};
export default WorkLogsTable;