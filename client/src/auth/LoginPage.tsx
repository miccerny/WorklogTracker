import { useState, type ComponentPropsWithoutRef } from "react";
import { readAuthToken, type AuthTokenResponse, type UserAuthType } from "./UserAuth.types";
import { apiGet, apiPost, setAuthToken } from "../utils/api";
import { useFlash } from "../context/flash";
import { useNavigate, type SessionData } from "react-router-dom";
import { HttpRequestError } from "../errors/HttpRequestError";
import LoginPanel from "./LoginPanel";
import { useSession } from "../context/session";

type LoginType = Pick<UserAuthType, "username" | "password">;
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
  const {setSession} = useSession();

  const handleChange: ComponentPropsWithoutRef<"input">["onChange"] = (
    e,
  ) => {
    const field = e.target.name as FieldName;
    const value = e.target.value;

    setValue((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const load: ComponentPropsWithoutRef<"form">["onSubmit"] = async (
    e,
  ) => {
    e.preventDefault();

    setLoading(true);
    setError("");
    try {
      const response = await apiPost<AuthTokenResponse, LoginType>("/auth/login", value);
      const token = readAuthToken(response);
      if(!token){
        throw new HttpRequestError("Přihlášení nevrátilo token")
      }

      setAuthToken(token);
      const me = await apiGet<SessionData>("/auth/me");
      setSession({data: me, status: "authenticated"});
      
      showFlash("success", "Přihlášení bylo úspěšné");
      navigate("/worklogs");
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
