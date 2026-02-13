import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { apiGetById, apiPost, apiPut } from "../../utils/api";
import type { WorkLogType } from "./WorkLog.types";

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
    } catch (error: any) {
      // Show error message for API failure.
      setErrorState(error.message ?? "Chyba při ukládání");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form
      // Form submit handler
      onSubmit={handleSubmit}
      // Simple inline layout: grid with spacing, limited width
      style={{ display: "grid", gap: 12, maxWidth: 400 }}
    >
      {/* Title changes depending on mode */}
      <h2>{mode === "create" ? "Vytvořit worklog" : "Upravit worklog"}</h2>

      {/* Controlled input: value comes from state, onChange updates state */}
      <input
        value={workLogName}
        onChange={(e) => setWorkLogName(e.target.value)}
        // Disable input while loading to prevent double actions
        disabled={loading}
      />

      {/* Show error message if errorState is not null */}
      {errorState && <div style={{ color: "crimson" }}>{errorState}</div>}

      {/* Buttons row */}
      <div style={{ display: "flex", gap: 10 }}>
        {/* Submit button triggers onSubmit */}
        <button type="submit" disabled={loading}>
          {mode === "create" ? "Vytvořit" : "Uložit"}
        </button>

        {/* Cancel button navigates back without submit */}
        <button
          type="button"
          onClick={() => navigate("/worklogs")}
          disabled={loading}
        >
          Zrušit
        </button>
      </div>
    </form>
  );
};
export default WorkLogForm;
