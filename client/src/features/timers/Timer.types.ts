/**
 * Timer type represents a single timer record
 * received from backend API.
 *
 * This structure mirrors TimerEntity (backend)
 * but uses frontend-friendly types (e.g. string for dates).
 */
export type Timer = ({
   /**
     * Unique identifier of timer.
     */
    id: number;

     /**
     * ID of parent WorkLog.
     * Used for routing and grouping timers.
     */
    workLogId: number;

    /**
     * Creation/start timestamp of timer.
     *
     * Stored as string because JSON transfers date-time as ISO string.
     * Usually formatted on frontend before displaying.
     */
    createdAt: string;

    /**
     * Stop timestamp.
     *
     * Can be null if timer is still running.
     */
    stoppedAt: string | null;

    /**
     * Duration of timer in seconds.
     *
     * Converted to readable format using formatting function
     * (e.g. formatDuration).
     */
    durationInSeconds: number;
    /**
     * Current status of timer.
     * Limited to predefined string union type.
     */
    status: TimerStatus;
})

/**
 * TimerStatus defines allowed states of timer.
 *
 * Using string union instead of string improves:
 * - type safety
 * - autocomplete
 * - prevents invalid values
 */
type TimerStatus = "RUNNING" | "STOPPED";