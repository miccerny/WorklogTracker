import { useState, type ChangeEvent} from "react";
import { useNavigate } from "react-router-dom";
import { useFlash } from "../context/flash";
import { useSession, type SessionData } from "../context/session";
import { HttpRequestError } from "../errors/HttpRequestError";
import { apiGet, apiPost, setAuthToken } from "../utils/api";
import LoginPanel from "./LoginPanel";
import type { UserAuthType } from "./UserAuth.types";

/**
 * Login request payload.
 */
type LoginType = Pick<UserAuthType, "username" | "password">;

/**
 * Supported login API response shapes.
 * Backend can return token directly or nested in data.
 */
type LoginResponse =
  | string
  | {
      token?: string;
      accessToken?: string;
      jwt?: string;
      data?: {
        token?: string;
        accessToken?: string;
        jwt?: string;
      };
    };

/**
 * Reads auth token from known response variants.
 *
 * @param response Login API response.
 * @returns Token string or null when token is missing.
 */
function readToken(response: LoginResponse): string | null {
  if (typeof response === "string") {
    return response;
  }

  return (
    response.token ??
    response.accessToken ??
    response.jwt ??
    response.data?.token ??
    response.data?.accessToken ??
    response.data?.jwt ??
    null
  );
}

/**
 * Login page container.
 * Handles login submit, token persistence, and session refresh.
 *
 * @returns Login page JSX.
 */
const LoginPage = () => {
  const [value, setValue] = useState<LoginType>({
    username: "",
    password: "",
  });
  const [errorState, setErrorState] = useState("");
  const [loading, setLoading] = useState(false);
  const { showFlash } = useFlash();
  const { setSession } = useSession();
  const navigate = useNavigate();

  /**
   * Updates login form values while user types.
   *
   * @param e Input change event.
   * @returns void
   */
  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setValue((prev) => ({ ...prev, [name]: value }));
  };

  /**
   * Submits login credentials to API.
   *
   * @param e Form submit event.
   * @returns Promise resolved when submit flow finishes.
   */
  const handleSubmit = async (e: FormDataEvent) => {
    e.preventDefault();
    setErrorState("");

    try {
      setLoading(true);
      const loginResponse = await apiPost<LoginResponse, LoginType>(
        "/auth/login",
        value
      );
      // Parse token from backend response (supports multiple token keys).
      const token = readToken(loginResponse);

      if (!token) {
        throw new HttpRequestError("Přihlášení nevrátilo token");
      }

      // Save token first, then fetch /auth/me to initialize session data.
      setAuthToken(token);
      const me = await apiGet<SessionData>("/auth/me");
      setSession({ data: me, status: "authenticated" });

      showFlash("success", "Přihlášení bylo úspěšné");
      // Redirect user to the main page after successful login.
      navigate("/worklogs");
    } catch (e) {
      if (e instanceof HttpRequestError) {
        setErrorState(e.message || "Přihlášení se nepovedlo");
        showFlash("error", e.message || "Přihlášení se nepovedlo");
      } else {
        setErrorState("Přihlášení se nepovedlo");
        showFlash("error", "Přihlášení se nepovedlo");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <LoginPanel
      onSubmit={handleSubmit}
      onChange={handleChange}
      username={value.username}
      password={value.password}
      loading={loading}
      errorState={errorState}
    />
  );
};

export default LoginPage;
