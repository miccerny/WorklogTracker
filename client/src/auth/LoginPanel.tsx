import type { ComponentPropsWithoutRef } from "react";

type LoginPanelProps = {
  onChange: ComponentPropsWithoutRef<"input">["onChange"];
  onSubmit: ComponentPropsWithoutRef<"form">["onSubmit"];
  value: {
    username: string;
    password: string;
  };
  loading: boolean;
  errorState: string;
};

const LoginPanel = ({
  onChange,
  onSubmit,
  value,
  loading,
  errorState,
}: LoginPanelProps) => {
  return (
    <form className="reg" onSubmit={onSubmit} noValidate>
      <h1 className="reg__title">Přihlášení</h1>

      {errorState && <p className="reg__state reg__state--error">{errorState}</p>}

      <div className="reg__field">
        <label>Email</label>
        <input
          className="reg input"
          name="username"
          value={value.username}
          onChange={onChange}
          placeholder="jan.novak@seznam.cz"
        />
      </div>

      <div className="reg__field">
        <label>Heslo</label>
        <input
          className="reg input"
          name="password"
          type="password"
          value={value.password}
          onChange={onChange}
        />
      </div>

      <div className="reg_actions">
        <button className="btn btn-primary reg__submit" type="submit" disabled={loading}>
          {loading ? "Přihlašuji..." : "Přihlásit"}
        </button>
      </div>
    </form>
  );
};

export default LoginPanel;
