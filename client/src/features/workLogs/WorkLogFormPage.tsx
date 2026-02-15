import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { apiGetById, apiPost, apiPut } from "../../utils/api";
import type { WorkLogType } from "./WorkLog.types";
import WorkLogFormPanel from "./WorkLogFormPanel";
import { useFlash } from "../../context/flash";

/**
 * Props for WorkLogForm component.
 *
 * mode:
 * - "create" => form creates a new worklog
 * - "edit"   => form loads existing worklog and updates it
 */
type Props = { mode: "create" | "edit" };

/**
 * Props for WorkLogForm component.
 *
 * mode:
 * - "create" => form creates a new worklog
 * - "edit"   => form loads existing worklog and updates it
 */
const WorkLogForm = ({ mode }: Props) => {
  // Programmatic navigation after submit/cancel.
  const navigate = useNavigate();

  // Read route param workLogId from URL (e.g. /worklogs/12/edit).
  // Param is string by default, so we convert it later to number.
  const { workLogId } = useParams<{ workLogId: string }>();

  // Form field state for worklog name (controlled input).
  const [workLogName, setWorkLogName] = useState("");

  // Error message shown in UI. null = no error.
  const [errorState, setErrorState] = useState<string | null>(null);

  // Loading flag to disable inputs/buttons and show loading state if needed.
  const [loading, setLoading] = useState(false);

  //Convert workLogId string to number for API calls.
  // If workLogId is missing (e.g. create mode), idNumber becomes null.
  const idNumber = workLogId ? Number(workLogId) : null;

  const {showFlash} = useFlash();

  /**
   * In edit mode, load WorkLog detail and prefill input.
   *
   * Triggered when:
   * - mode changes
   * - idNumber changes
   */
  useEffect(() => {
    // If not edit mode or no ID in URL, do nothing.
    if (mode !== "edit" || idNumber === null) return;

    /**
     * Loads one worklog by ID from API.
     *
     * Uses try/catch/finally:
     * - try: start loading, call API, set state
     * - catch: show error message
     * - finally: always turn off loading
     */
    const loadOne = async () => {
      try {
        setLoading(true);
        setErrorState(null);

        // GET /worklogs/{id}
        const data = await apiGetById<WorkLogType>("/worklogs", idNumber);

        // Prefill form input.
        setWorkLogName(data.workLogName);
      } catch (error: any) {
        // If API throws error, we show message if available.
        setErrorState(error.message ?? "Chyba při načítání");
        
      } finally {
        setLoading(false);
      }
    };
    // Call the async loader.
    loadOne();
  }, [mode, idNumber]);

  /**
   * Handles form submit for both create and edit mode.
   *
   * Important:
   * - preventDefault() stops browser from reloading page.
   * - Validation prevents sending empty name.
   */
  const handleSubmit = async (e: React.SubmitEvent) => {
    e.preventDefault();

    // Basic validation: trim removes spaces.
    if (!workLogName.trim()) {
      setErrorState("Název nesmí být prázdný");
      return;
    }

    try {
      setLoading(true);
      setErrorState(null);

      // Create or edit based on mode:
      // create => POST /worklogs
      // edit   => PUT /worklogs/{id}
      if (mode === "create") {
        await apiPost("worklogs", { workLogName });
      } else {
        await apiPut(`/worklogs/${idNumber}`, { workLogName });
      }

      // After success, navigate back to list page.
      navigate("/worklogs");
      showFlash("success", "Uloženo");
    } catch (error: any) {
      // Show error message for API failure.
      setErrorState(error.message ?? "Chyba při ukládání");
      showFlash("error", "Odeslání se nezdařilo", 2000);
    } finally {
      setLoading(false);
    }
  };

  return (
    <WorkLogFormPanel 
      handleSubmit = {handleSubmit}
      mode = {mode}
      workLogName = {workLogName}
      loading = {loading}
      errorState = {errorState}
      navigate = {navigate}
      setWorkLogName = {setWorkLogName}
    />
  );
};
export default WorkLogForm;
