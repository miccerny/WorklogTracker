import type { Timer } from "./TimerType";

const TimerTable = ({label, timerData}: {timerData: Timer[], label: string}) => {

return(
    <>
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