/**
 * Pre-configured formatter for Czech locale date and time.
 *
 * Uses built-in Intl.DateTimeFormat API.
 * This ensures consistent formatting across the application.
 *
 * Example output (cs-CZ):
 * 13.02.2026 14:05:09
 */
const CZ_DATE_TIME = new Intl.DateTimeFormat("cs-CZ", {
  year: "numeric",
  month: "2-digit",
  day: "2-digit",
  hour: "2-digit",
  minute: "2-digit",
  second: "2-digit",
});

/**
 * Parses ISO-like LocalDateTime string (without timezone)
 * into JavaScript Date object.
 *
 * Example input:
 * "2026-02-13T14:05:09"
 *
 * Important:
 * Backend likely sends LocalDateTime (without timezone),
 * so we manually split date and time parts instead of using
 * new Date(value) directly (which may interpret timezone).
 *
 * @param value date-time string in format YYYY-MM-DDTHH:mm:ss
 * @returns JavaScript Date object
 */
function parseLocalDateTime(value: string): Date {

  // Split into date and time parts.
  const [datePart, timePart = "00:00:00"] = value.split("T");

  // Extract year, month, day as numbers.
  const [year, month, day] = datePart.split("-").map(Number);

  // Extract hour, minute, second as numbers.
  const [hour, minute, second = 0] = timePart.split(":").map(Number);


   /**
   * Important:
   * Month in JS Date constructor is 0-based (0 = January).
   * Therefore we subtract 1 from month.
   */
  return new Date(year, month - 1, day, hour, minute, second);
}

/**
 * Formats LocalDateTime string into readable Czech date-time.
 *
 * Handles:
 * - null
 * - undefined
 * - invalid date values
 *
 * @param value date-time string or null
 * @returns formatted date string or fallback message
 */
export function formatLocalDateTime(
  value: string | null | undefined
): string {

  // If value is null/undefined/empty, return dash symbol.
  if (!value) return "—";

  // Parse string into Date object.
  const date = parseLocalDateTime(value);

  // Validate date (invalid date returns NaN time).
  if (Number.isNaN(date.getTime())) return "Neplatné datum";

  // Format using predefined Czech formatter.
  return CZ_DATE_TIME.format(date);
}