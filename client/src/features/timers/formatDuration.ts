/**
 * Converts total duration in seconds to HH:MM:SS format.
 *
 * Example:
 * 3661 seconds -> "01:01:01"
 *
 * Used in TimerTable to display readable duration
 * instead of raw seconds from backend.
 *
 * @param totalSeconds total duration in seconds
 * @returns formatted string in HH:MM:SS format
 */
const formatDuration = (totalSeconds: number): string => {
  /**
   * Calculate full hours.
   * 3600 seconds = 1 hour.
   */
  const hours = Math.floor(totalSeconds / 3600);

  /**
   * Calculate remaining minutes.
   * First remove full hours using modulo 3600,
   * then divide by 60.
   */
  const minutes = Math.floor((totalSeconds % 3600) / 60);

  /**
   * Remaining seconds after removing hours and minutes.
   */
  const seconds = totalSeconds % 60;

  /**
   * Helper function to ensure two-digit formatting.
   *
   * Example:
   * 5  -> "05"
   * 12 -> "12"
   *
   * padStart adds leading zero if needed.
   */
  const pad = (n: number) => n.toString().padStart(2, "0");

  /**
   * Return formatted time string.
   */
  return `${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;
};
export default formatDuration;
