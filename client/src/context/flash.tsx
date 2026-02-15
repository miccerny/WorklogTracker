import {
  createContext,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from "react";

/**
 * Supported flash message types.
 * Extend if needed (e.g. "warning", "info", etc.)
 */
export type FlashType = "success" | "error" | "info";

/**
 * Flash message structure.
 */
export interface FlashMessage {
  type: FlashType;
  message: string;
}

/**
 * Context value definition.
 */
interface FlashContextType {
  flash: FlashMessage | null;
  showFlash: (type: FlashType, message: string, timeoutMs?: number) => void;
  clear: () => void;
}

/**
 * Create context with undefined as default
 * (forces proper usage inside provider)
 */
const FlashContext = createContext<FlashContextType | undefined>(undefined);

/**
 * Provider component that wraps the app.
 */
export function FlashProvider({ children }: { children: ReactNode }) {
  const [flash, setFlash] = useState<FlashMessage | null>(null);

  const value = useMemo<FlashContextType>(
    () => ({
      flash,

      /**
       * Shows flash message and optionally auto-clears it.
       */
      showFlash: (type, message, timeoutMs = 2500) => {
        setFlash({ type, message });

        if (timeoutMs) {
          window.setTimeout(() => setFlash(null), timeoutMs);
        }
      },

      /**
       * Manually clears flash message.
       */
      clear: () => setFlash(null),
    }),
    [flash]
  );

  return (
    <FlashContext.Provider value={value}>
      {children}
    </FlashContext.Provider>
  );
}

/**
 * Custom hook for consuming FlashContext.
 * Throws error if used outside provider.
 */
export function useFlash(): FlashContextType {
  const ctx = useContext(FlashContext);

  if (!ctx) {
    throw new Error("useFlash must be used within a FlashProvider");
  }

  return ctx;
}
