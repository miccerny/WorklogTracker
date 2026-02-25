import { useState } from "react";
import type { UserAuthType } from "./UserAuth.types";
import { apiPost } from "../utils/api";
import { useFlash } from "../context/flash";
import { useNavigate } from "react-router-dom";
import { HttpRequestError } from "../errors/HttpRequestError";
import LoginPanel from "./LoginPanel";

type LoginType = Pick<UserAuthType, "fullName" & "confirmPasswrod">;
type FieldName = keyof LoginType;

const LoginPage = () => {
  const [value, setValue] = useState<LoginType>({
    username: "",
    password: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string>("");
  const { showFlash } = useFlash();
  const navigate = useNavigate();

  const handleChange: React.ComponentPropsWithoutRef<"input">["onChange"] = (
    e,
  ) => {
    const field = e.target.name as FieldName;
    const value = e.target.value;

    setValue((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const load: React.ComponentPropsWithoutRef<"form">["onSubmit"] = async (
    e,
  ) => {
    e.preventDefault();

    setLoading(true);
    setError("");
    try {
      await apiPost("/login", value);
      showFlash("success", "Přihlášení bylo úspěšné");
      navigate("/worklog");
    } catch (e) {
      if (e instanceof HttpRequestError) {
        setError(e.message || "Přihlášení se nezdařilo");
      } else {
        setError("Neočekáváná chyba");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <LoginPanel
      onChange={handleChange}
      value={value}
      onSubmit={load}
      loading={loading}
      errorState={error}
    />
  );
};
export default LoginPage;
