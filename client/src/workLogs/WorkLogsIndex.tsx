import { useEffect, useState } from "react"
import { apiGet } from "../utils/api";
import { HttpRequestError } from "../errors/HttpRequestError";
import WorkLogsTable from "./WorkLogTable";
import type  { WorkLog } from "./typeWorkLog";
import { Outlet } from "react-router-dom";



export const WorkLogs = () => {
  const [loading, setLoading] = useState(false);
  const [WorkLogState, setWorkLogState] = useState<WorkLog[]>([]);
  const [erorState, setErrorState] = useState<string>("");

  useEffect(() => {
    const loadData = async() => {
      setLoading(true);

      try {
        const response = await apiGet<WorkLog[]>("/worklogs");
          setWorkLogState(response);
      } catch (error) {
        if (error instanceof HttpRequestError) {
          setErrorState(error.message);
        } else {
          setErrorState("Unknown error");
        }
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  return (
    <>
      <h4>Work Logs</h4>
      <hr/>
       <WorkLogsTable
        workLogs={WorkLogState}
        label = "Počet záznamů: "
       />
       <Outlet />
    </>
  )
}
