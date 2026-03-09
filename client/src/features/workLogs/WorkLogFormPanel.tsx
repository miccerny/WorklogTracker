type FormPanel = {
  handleSubmit: any;
  mode: string;
  workLogName: string;
  loading: any;
  errorState: string | null;
  navigate: any;
  setWorkLogName: any;
};

const WorkLogFormPanel = ({
  handleSubmit,
  mode,
  workLogName,
  loading,
  errorState,
  navigate,
  setWorkLogName,
}: FormPanel) => {
  return (
    <>
      <form
        // Form submit handler
        onSubmit={handleSubmit}
        // Simple inline layout: grid with spacing, limited width
        style={{ display: "grid", gap: 12, maxWidth: 400 }}
      >
        {/* Title changes depending on mode */}
        <h4 className="wl__title">{mode === "create" ? "Vytvořit worklog" : "Upravit worklog"}</h4>

        {/* Controlled input: value comes from state, onChange updates state */}
        <label className="wl__label">Název </label>
        <input
          value={workLogName}
          onChange={(e) => setWorkLogName(e.target.value)}
          // Disable input while loading to prevent double actions
          disabled={loading}
          className="wl__input"
        />

        {/* Show error message if errorState is not null */}
        {errorState && <div style={{ color: "crimson" }}>{errorState}</div>}

        {/* Buttons row */}
        <div style={{ display: "flex", gap: 10 }}>
          {/* Submit button triggers onSubmit */}
          <button 
            type="submit" 
            disabled={loading}
            className="wl__action btn"
          >
            {mode === "create" ? "Vytvořit" : "Uložit"}
          </button>

          {/* Cancel button navigates back without submit */}
          <button
            type="button"
            onClick={() => navigate("/worklogs")}
            disabled={loading}
            className="wl__action btn"
          >
            Zrušit
          </button>
        </div>
      </form>
    </>
  );
};
export default WorkLogFormPanel;
