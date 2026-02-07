const CZ_DATE_TIME = new Intl.DateTimeFormat("cs-CZ", {
  year: "numeric",
  month: "2-digit",
  day: "2-digit",
  hour: "2-digit",
  minute: "2-digit",
  second: "2-digit",
});

function parseLocalDateTime(value: string): Date {
  const [datePart, timePart = "00:00:00"] = value.split("T");
  const [year, month, day] = datePart.split("-").map(Number);
  const [hour, minute, second = 0] = timePart.split(":").map(Number);

  return new Date(year, month - 1, day, hour, minute, second);
}

export function formatLocalDateTime(
  value: string | null | undefined
): string {
  if (!value) return "—";

  const date = parseLocalDateTime(value);
  if (Number.isNaN(date.getTime())) return "Neplatné datum";

  return CZ_DATE_TIME.format(date);
}