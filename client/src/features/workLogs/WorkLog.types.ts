/**
 * Represents a single WorkLog entity received from the backend API.
 *
 * This type describes the data structure used across the frontend:
 * - listing worklogs
 * - displaying detail pages
 * - editing worklogs
 *
 * It should stay aligned with the backend DTO contract.
 */
export type WorkLogType = {
  /**
   * Unique identifier of the work log.
   * Used for routing, editing, deleting and linking timers.
   */
  id: number;

  /**
   * Human-readable name of the work log.
   * Example: "Client Project A", "Internal Development".
   */
  workLogName: string;

  /**
   * Optional hourly rate assigned to this work log.
   * Can be undefined if no billing rate is defined.
   */
  hourlyRate?: number;

  /**
   * Indicates whether this work log is currently active.
   * Optional because backend might omit it in some responses.
   */
  isActivated?: boolean;
};
