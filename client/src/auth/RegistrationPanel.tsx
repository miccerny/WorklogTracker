import type { SubmitEventHandler } from "react";
import type { UserAuthType } from "./UserAuth.types";
import type { FieldError } from "./FieldError.types";

type RegistrationPage = {
    onSubmit: SubmitEventHandler,
    errorState: string,
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void,
    onBlur: (e: React.FocusEvent<HTMLInputElement>)  => void,
    valueState: UserAuthType,
    loading: boolean,
    fieldError: FieldError,
    showError: any,
    setField: any,
    touch: any,
}
const RegistrationPanel = ({onSubmit, onChange, errorState, valueState, loading, onBlur, fieldError, showError, setField, touch}: RegistrationPage ) => {

return(
    <>
    <form onSubmit={onSubmit} noValidate>
        <h1>Registrace</h1>
        {errorState && <p>{errorState}</p>}

        <label>Celé jméno</label>
        <input
        name="fullName"
        value={valueState.fullName}
        onChange={(e) => setField("fullName", e.target.value)}
        onBlur={() => touch("fullName")}
        />
        {showError("fullName") && fieldError.fullName}

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
        name="password"
        value={valueState.password}
        onChange={onChange("password")}
        onBlur={() => touch("password")}
        />

        {showError("password") && fieldError.password}

        <label>Potvrďte heslo</label>
        <input
        name="confirmPassword"
        value={valueState.confirmPassword}
        onChange={(e) => setField("confirmPassword", e.target.value)}
        onBlur={() => touch("confirmPassword")}
        />

        {showError("confirmPassword") && fieldError.confirmPassword}

        <button type="submit" disabled={loading}>
            {loading ? "Odesílám..." : "Registrovat" }
        </button>
    </form>
    </>
)

}
export default RegistrationPanel;