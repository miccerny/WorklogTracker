import type { ComponentProps } from "react";
import type { UserAuthType } from "./UserAuth.types";
import type { FieldError } from "./FieldError.types";

/**
 * Props for registration presentational form.
 */
type RegistrationPage = {
  /** Called when user submits the registration form. */
  onSubmit: NonNullable<ComponentProps<"form">["onSubmit"]>;
  /** Generic submit/API error text. */
  errorState: string;
  /** Current form values. */
  valueState: UserAuthType;
  /** True while registration request is running. */
  loading: boolean;
  /** Field-level validation messages. */
  fieldError: FieldError;
  /** Returns visible error for field (usually only after touch). */
  showError: any;
  /** Updates one field value. */
  setField: any;
  /** Marks field as touched. */
  touch: any;
};

/**
 * Registration form UI component.
 * Keeps only markup and delegates business logic to page container.
 *
 * @param props Form values and handlers.
 * @returns Registration form JSX.
 */
const RegistrationPanel = ({
  onSubmit,
  errorState,
  valueState,
  loading,
  fieldError,
  showError,
  setField,
  touch,
}: RegistrationPage) => {
  return (
    <>
      <form onSubmit={onSubmit} noValidate>
        <h1>Registrace</h1>
        {/* Show global error above form when API request fails. */}
        {errorState && <p>{errorState}</p>}

        <label>Celé jméno</label>
        <input
          name="name"
          value={valueState.name}
          onChange={(e) => setField("name", e.target.value)}
          onBlur={() => touch("name")}
        />
        {/* Field-level error shown only when field should display error. */}
        {showError("name") && fieldError.name}

        <label>Email</label>
        <input
          name="username"
          value={valueState.username}
          onChange={(e) => setField("username", e.target.value)}
          onBlur={() => touch("username")}
        />
        {showError("username") && fieldError.username}

        <label>Heslo</label>
        <input
        type="password"
          name="password"
          value={valueState.password}
          onChange={(e) => setField("password", e.target.value)}
          onBlur={() => touch("password")}
        />

        {showError("password") && fieldError.password}

        <label>Potvrďte heslo</label>
        <input
        type="password"
          name="confirmPassword"
          value={valueState.confirmPassword}
          onChange={(e) => setField("confirmPassword", e.target.value)}
          onBlur={() => touch("confirmPassword")}
        />

        {showError("confirmPassword") && fieldError.confirmPassword}

        <button type="submit" disabled={loading}>
          {/* Disable submit to prevent duplicate requests. */}
          {loading ? "Odesílám..." : "Registrovat"}
        </button>
      </form>
    </>
  );
};
export default RegistrationPanel;
