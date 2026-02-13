/**
 * Props for TimerActiveTable component.
 *
 * label       - optional section title (default provided)
 * timeText    - already formatted time string (e.g. "00:12:34")
 * isRunning   - indicates whether timer is currently running
 * errorState  - error message to display (null = no error)
 * onStart     - callback function for starting timer
 * onStop      - callback function for stopping timer
 */
type Props = {
  label?: string;
  timeText: string; // už naformátovaný čas "00:12:34"
  isRunning: boolean;
  errorState: string | null;
  onStart: () => void;
  onStop: () => void;
};

/**
 * TimerActiveTable component.
 *
 * This is a presentational component.
 * It:
 * - displays current timer value
 * - shows error message if exists
 * - renders Start / Stop buttons
 *
 * It does NOT:
 * - handle API calls
 * - calculate time
 * - manage state
 *
 * All logic is passed in via props.
 */
const TimerActiveTable = ({
  // Default value for label if not provided
  label = "Aktivní timer",
  timeText,
  isRunning,
  errorState,
  onStart,
  onStop,
}: Props) => {
  return (
    // Simple grid layout with spacing
    <div style={{ display: "grid", gap: 12 }}>
      {/* Section title */}
      <h2 style={{ margin: 0 }}>{label}</h2>

      {/* Conditional rendering of error message */}
      {errorState && <div style={{ color: "crimson" }}>{errorState}</div>}

      {/* Display formatted time text */}
      <div style={{ fontSize: 28, fontWeight: 700 }}>{timeText}</div>

      {/* Buttons container */}
      <div style={{ display: "flex", gap: 10 }}>
        {/* Start button:
            Disabled when timer is already running */}
        <button onClick={onStart} disabled={isRunning}>
          Start
        </button>
        {/* Stop button:
            Disabled when timer is NOT running */}
        <button onClick={onStop} disabled={!isRunning}>
          Stop
        </button>
      </div>
    </div>
  );
};
export default TimerActiveTable;
