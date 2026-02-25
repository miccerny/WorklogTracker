import type { UserAuthType } from "./UserAuth.types";
import type { FieldError } from "./FieldError.types";
import "./RegistrationPanel.css";

type FieldName = keyof UserAuthType;

type RegistrationPage = {
  onSubmit: React.ComponentPropsWithoutRef<"form">["onSubmit"];
  errorState: string;
  onChange: React.ComponentPropsWithoutRef<"input">["onChange"];
  onBlur: React.ComponentPropsWithoutRef<"input">["onBlur"];
  valueState: UserAuthType;
  loading: boolean;
  fieldError: FieldError;
  showError: (field: FieldName) => boolean;
};
const RegistrationPanel = ({
  onSubmit,
  onChange,
  errorState,
  valueState,
  loading,
  onBlur,
  fieldError,
  showError,
}: RegistrationPage) => {
  return (
    <>
      <form className="reg" onSubmit={onSubmit} noValidate>
        <h1 className="reg__title">Registrace</h1>
        {errorState && (
          <p className=" reg__state reg__state--error">{errorState}</p>
        )}

        <div className="reg__field">
          <label>Celé jméno</label>
          <input
            className="reg input "
            name="fullName"
            value={valueState.fullName}
            onChange={onChange}
            onBlur={onBlur}
            placeholder="Jan Novák"
          />
          {showError("fullName") && (
            <div className="reg__error">{fieldError.fullName}</div>
          )}
        </div>
        <div className="reg__field">
          <label>Email</label>
          <input
            className="reg input"
            name="username"
            value={valueState.username}
            onChange={onChange}
            onBlur={onBlur}
            placeholder="jan.novak@seznam.cz"
          />
          {showError("username") && (
            <div className="reg__error">{fieldError.username}</div>
          )}
        </div>
        <div className="reg__field">
          <label>Heslo</label>
          <input
            className="reg input"
            name="password"
            value={valueState.password}
            onChange={onChange}
            onBlur={onBlur}
          />

          {showError("password") && (
            <div className="reg__error">{fieldError.password}</div>
          )}
        </div>
        <div className="reg__field">
          <label>Potvrďte heslo</label>
          <input
            className="reg input"
            name="confirmPassword"
            value={valueState.confirmPassword}
            onChange={onChange}
            onBlur={onBlur}
          />

          {showError("confirmPassword") && (
            <div className="reg__error">{fieldError.confirmPassword}</div>
          )}
        </div>
        <div className="reg_actions">
          <button
            className="btn btn-primary reg__submit"
            type="submit"
            disabled={loading}
          >
            {loading ? "Odesílám..." : "Registrovat"}
          </button>
        </div>
      </form>
    </>
  );
};
export default RegistrationPanel;
