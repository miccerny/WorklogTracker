
type Props = {
  label?: string;
  timeText: string;          // už naformátovaný čas "00:12:34"
  isRunning: boolean;
  errorState: string | null;
  onStart: () => void;
  onStop: () => void;
};

const TimerActiveTable = ({
  label = "Aktivní timer",
  timeText,
  isRunning,
  errorState,
  onStart,
  onStop,
}: Props) => {

  return(
    <div style={{ display: "grid", gap: 12 }}>
      <h2 style={{ margin: 0 }}>{label}</h2>

      {errorState && (
        <div style={{ color: "crimson" }}>{errorState}</div>
      )}

      <div style={{ fontSize: 28, fontWeight: 700 }}>
        {timeText}
      </div>

      <div style={{ display: "flex", gap: 10 }}>
        <button onClick={onStart} disabled={isRunning}>
          Start
        </button>
        <button onClick={onStop} disabled={!isRunning}>
          Stop
        </button>
      </div>
    </div>
  )
}
export default TimerActiveTable;