import type { Timer } from "./TimerType";

const TimerTable = ({format, label, timerData, errorState }: {timerData: Timer[], label: string, errorState: string | null, format: any}) => {

return(
    <>
    {errorState && <p>{errorState}</p>}
    <p>{label}</p>
    <ul>
    {timerData.map((timer) => (
        <li key={timer.id}>
            {format(timer.duration)}
        </li>
    ))}
    </ul>
    <button type="button">Vytvořit nový timer</button>
    </>
)
}
export default TimerTable;