import { useEffect, useState } from "react";
import { apiGet } from "../../utils/api";
import { HttpRequestError } from "../../errors/HttpRequestError";
import WorkLogListPanel from "./WorkLogListPanel";
import type { WorkLogType } from "./WorkLog.types";
import { Link } from "react-router-dom";

/**
 * WorkLogs page component.
 *
 * Responsibilities:
 * - Load work logs from API on initial render
 * - Handle loading + error UI state
 * - Render WorkLogsTable with fetched data
 * - Provide navigation to create new WorkLog
 *
 * Note:
 * - Data fetching is done in useEffect with empty dependency array [],
 *   so it runs once when the component mounts.
 */
export const WorkLogListPage = () => {
  /**
   * Indicates whether the initial request is currently in progress.
   * Used to show spinner / disabled UI in WorkLogsTable.
   */
  const [loading, setLoading] = useState(false);

  /**
   * Holds loaded WorkLogs from the backend.
   * Default is empty array, so the table can render immediately without null checks.
   */
  const [workLogState, setWorkLogState] = useState<WorkLogType[]>([]);

  /**
   * Stores a user-facing error message.
   * Empty string means "no error".
   */
  const [errorState, setErrorState] = useState<string>("");

  useEffect(() => {
    /**
     * Loads WorkLogs from the API.
     * Wrapped into an async function because useEffect callback itself can't be async.
     */
    const loadData = async () => {
      setLoading(true);
      setErrorState(""); // Reset error before new request (useful if you reload later)

      try {
        /**
         * apiGet is generic, so we specify the response type explicitly:
         * api returns WorkLogType[] and TypeScript enforces it in the rest of the component.
         */
        const response = await apiGet<WorkLogType[]>("/worklogs");
        setWorkLogState(response);
      } catch (error) {
        /**
         * We handle our custom HttpRequestError separately
         * because it carries a structured message/status/code.
         */
        if (error instanceof HttpRequestError) {
          setErrorState(error.message);
        } else {
          // Fallback for unexpected errors
          setErrorState("Unknown error");
        }
      } finally {
        // Always stop loading, even if request fails
        setLoading(false);
      }
    };

    loadData();
  }, []);

  return (
    <>
      

      {/* Presentational component rendering the table */}
      <WorkLogListPanel
        workLogs={workLogState}
        label="Počet záznamů: "
        loading={loading}
        errorState={errorState}
      />
    </>
  );
};
