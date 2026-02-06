import type { Timer } from "./TimerType";

const TimerTable = ({label, timerData, errorState }: {timerData: Timer[], label: string, errorState: string | null}) => {

return(
    <>
    {errorState && <p>{errorState}</p>}
    <p>{label}</p>
    {timerData.map((timer) => (
        <li key={timer.id}>
            {timer.startedAt}
        </li>
    ))}
    </>
)
}
export default TimerTable;