import type { ChangeEvent, SubmitEventHandler } from "react";

/**
 * Props for login presentational form.
 */
type LoginPanelProps = {
  /** Called when user submits the form. */
  onSubmit: SubmitEventHandler<HTMLFormElement>;
  /** Called when input values change. */
  onChange: (e: ChangeEvent<HTMLInputElement>) => void;
  /** Current username/email input value. */
  username: string;
  /** Current password input value. */
  password: string;
  /** True while login request is running. */
  loading: boolean;
  /** Global login error text. */
  errorState: string;
};

/**
 * Login form UI component.
 * It only renders fields and delegates logic to parent.
 *
 * @param props Form handlers and input values.
 * @returns Login form JSX.
 */
const LoginPanel = ({
  onSubmit,
  onChange,
  username,
  password,
  loading,
  errorState,
}: LoginPanelProps) => {
  return (
    <form onSubmit={onSubmit} noValidate className="card">
      <h1>Přihlášení</h1>
      {/* Show API error text above fields if login fails. */}
      {errorState && <p>{errorState}</p>}

      <label htmlFor="username">Email</label>
      <input
        id="username"
        name="username"
        type="email"
        value={username}
        onChange={onChange}
        autoComplete="username"
      />

      <label htmlFor="password">Heslo</label>
      <input
        id="password"
        name="password"
        type="password"
        value={password}
        onChange={onChange}
        autoComplete="current-password"
      />

      <button type="submit" disabled={loading}>
        {/* Prevent duplicate submit while request is in progress. */}
        {loading ? "Přihlašuji..." : "Přihlásit"}
      </button>
    </form>
  );
};

export default LoginPanel;
