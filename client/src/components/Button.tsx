import type { ButtonHTMLAttributes, ReactNode } from "react";
import "./Button.css";

type ButtonVariant = "primary" | "secondary" | "danger";

type ButtonProps = {
  children: ReactNode;
  onClick?: () => void;
  type?: "button"  | "submit" | "reset";
  variant?: ButtonVariant;
  loading?: boolean;
  disabled?: boolean;
  fullWidth?: boolean;
} & ButtonHTMLAttributes<HTMLButtonElement>;

const Button = ({
  children,
  onClick,
  type = "button",
  variant = "primary",
  loading = false,
  disabled = false,
  fullWidth = false,
} : ButtonProps) => {
const isDisabled = disabled || loading;

  return(
      <button
      type={type}
      onClick={onClick}
      disabled={isDisabled}
      className={`btn btn-${variant}${fullWidth ? "btn-full" : ""}`}
      >
       {loading ? <span className="btn-spinner" /> : children}
      </button>
  );
};
export default Button;