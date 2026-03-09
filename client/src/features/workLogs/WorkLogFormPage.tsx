import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useFlash } from "../../context/flash";
import { HttpRequestError } from "../../errors/HttpRequestError";
import { apiGet, apiPost, apiPut } from "../../utils/api";
import WorkLogFormPanel from "./WorkLogFormPanel";
import type { WorkLogType } from "./WorkLog.types";

type Props = { mode: "create" | "edit" };

const WorkLogForm = ({ mode }: Props) => {
  const navigate = useNavigate();
  const { workLogId } = useParams<{ workLogId: string }>();
  const [workLogName, setWorkLogName] = useState("");
  const [errorState, setErrorState] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const idNumber = workLogId ? Number(workLogId) : null;
  const { showFlash } = useFlash();

  useEffect(() => {
    if (mode !== "edit" || idNumber === null) return;

    const loadOne = async () => {
      try {
        setLoading(true);
        setErrorState(null);
        const all = await apiGet<WorkLogType[]>("/worklogs");
        const data = all.find((workLog) => workLog.id === idNumber);
        if (!data) {
          throw new HttpRequestError("Worklog nenalezen", -1, 404);
        }
        setWorkLogName(data.workLogName);
      } catch (error: unknown) {
        if (error instanceof HttpRequestError) {
          setErrorState(error.message);
        } else {
          setErrorState("Chyba při načítání");
        }
      } finally {
        setLoading(false);
      }
    };

    loadOne();
  }, [mode, idNumber]);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!workLogName.trim()) {
      setErrorState("Název nesmí být prázdný");
      return;
    }

    try {
      setLoading(true);
      setErrorState(null);
      if (mode === "create") {
        await apiPost("/worklogs", { workLogName });
        showFlash("success", `Byl vytvořen worklog ${workLogName}`, 2000);
      } else {
        await apiPut(`/worklogs?id=${idNumber}`, { workLogName });
        showFlash("success", "Worklog byl upraven", 2000);
      }
      navigate("/worklogs");
      showFlash("success", "Uloženo", 2000);
    } catch (error: unknown) {
      if (error instanceof HttpRequestError) {
        setErrorState(error.message);
      } else {
        setErrorState("Chyba při ukládání");
      }
      showFlash("error", "Odeslání se nezdařilo", 2000);
    } finally {
      setLoading(false);
    }
  };

  return (
    <WorkLogFormPanel
      handleSubmit={handleSubmit}
      mode={mode}
      workLogName={workLogName}
      loading={loading}
      errorState={errorState}
      navigate={navigate}
      setWorkLogName={setWorkLogName}
    />
  );
};

export default WorkLogForm;
