import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";
import { apiGet } from "../utils/api";
import { HttpRequestError } from "../errors/HttpRequestError";

/**
 * Possible session statuses.
 */
export type SessionStatus =
  | "loading"
  | "authenticated"
  | "unauthenticated";

/**
 * User session data.
 * Replace `any` with your real User DTO when you have it.
 */
export interface SessionData {
  // Example:
  // id: number;
  // email: string;
  // role: string;
  [key: string]: unknown;
}

/**
 * Internal session state structure.
 */
export interface SessionState {
  data: SessionData | null;
  status: SessionStatus;
}

/**
 * Context value definition.
 */
interface SessionContextType {
  session: SessionState;
  setSession: React.Dispatch<React.SetStateAction<SessionState>>;
}

/**
 * Create context with undefined default
 * (forces proper usage inside provider)
 */
const SessionContext = createContext<SessionContextType | undefined>(
  undefined
);

/**
 * Custom hook for accessing session.
 */
export function useSession(): SessionContextType {
  const ctx = useContext(SessionContext);

  if (!ctx) {
    throw new Error("useSession must be used within a SessionProvider");
  }

  return ctx;
}

/**
 * Session provider component.
 */
export function SessionProvider({ children }: { children: ReactNode }) {
  const [sessionState, setSessionState] = useState<SessionState>({
    data: null,
    status: "loading",
  });

  /**
   * Load current user session on mount.
   */
  useEffect(() => {
    apiGet<SessionData>("/me")
      .then((data) =>
        setSessionState({ data, status: "authenticated" })
      )
      .catch((e: unknown) => {
        if (
          e instanceof HttpRequestError &&
          e.status === 401
        ) {
          setSessionState({
            data: null,
            status: "unauthenticated",
          });
          return;
        }

        console.error("Session load failed:", e);

        setSessionState({
          data: null,
          status: "unauthenticated",
        });
      });
  }, []);

  return (
    <SessionContext.Provider
      value={{
        session: sessionState,
        setSession: setSessionState,
      }}
    >
      {children}
    </SessionContext.Provider>
  );
}