import React, { useState } from "react";
import type { UserAuthType } from "./UserAuth.types";
import { apiPost } from "../utils/api";
import { useFlash } from "../context/flash";
import { HttpRequestError } from "../errors/HttpRequestError";
import { useNavigate } from "react-router-dom";
import RegistrationPanel from "./RegistrationPanel";
import type { FieldError } from "./FieldError.types";

type FieldName = keyof UserAuthType;

type Touched = {
  fullName?: boolean;
  username?: boolean;
  password?: boolean;
  confirmPassword?: boolean;
};

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export const RegistrationPage = () => {
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const [valueState, setValueState] = useState<UserAuthType>({
    fullName: "",
    username: "",
    password: "",
    confirmPassword: "",
  });
  const [errorState, setErrorState] = useState<string>("");
  const [touched, setTouched] = useState<Touched>({});
  const [fieldError, setFieldsError] = useState<FieldError>({});
  const { showFlash } = useFlash();

  const validate = (v: UserAuthType): FieldError => {
    const nextField: FieldError = {};

    if (!v.fullName.trim()) nextField.fullName = "Zadej celé jméno";
    if (!v.username.trim()) nextField.username = "Zadej email";
    else if (!emailRegex.test(v.username.trim()))
      nextField.username = "Email nemá správný tvar";

    if (!v.password) nextField.password = "Zadej heslo";
    else if (v.password.length < 6)
      nextField.password = "Heslo musí mít alespoň 6 znaků";

    if (!v.confirmPassword) nextField.confirmPassword = "Znovu zadej heslo";
    else if (v.password !== v.confirmPassword)
      nextField.confirmPassword = "Hesla nesjou stejná";

    return nextField;
  };

  const validateAndSet = (v: UserAuthType) => {
    const next = validate(v);
    setFieldsError(next);
    return next;
  };

  const touchField = (name: FieldName) => {
    setTouched((prev) => ({ ...prev, [name]: true }));
  };

  const handleChange: React.ComponentPropsWithoutRef<"input">["onChange"] = (e) => {
    const field = e.target.name as FieldName;
    const value = e.target.value;

    setValueState((prev) => {
      const updated = { ...prev, [field]: value };

      if (touched[field]) {
        setFieldsError(validate(updated));
      }

      return updated;
    });
  };

  const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    const field = e.target.name as FieldName;

    touchField(field);
    validateAndSet(valueState);
  };

  const handleSubmit: React.ComponentPropsWithoutRef<"form">["onSubmit"] = async (e) => {
    e.preventDefault();
    setErrorState("");
    setTouched({
      fullName: true,
      username: true,
      password: true,
      confirmPassword: true,
    });

    const nextErrors = validateAndSet(valueState);
    if (Object.keys(nextErrors).length > 0) return;

    const { confirmPassword, ...registrationData } = valueState;

    try {
      setLoading(true);
      await apiPost("/registration", registrationData);
      showFlash("success", "Registrace proběhla úspěšně");
      navigate("/login");
    } catch (e) {
      if (e instanceof HttpRequestError) {
        setErrorState(e.message || "Registrace se nepovedla");
        showFlash("error", e.message || "Registrace se nepovedla");

        const d = e.details as any;
        const fieldMap = d?.errors || d?.fieldErrors;
        if (fieldMap && typeof fieldMap === "object") {
          setFieldsError(fieldMap);


          setTouched((prev) => ({ ...prev, ...Object.keys(fieldMap) }));
        }
      } else {
        setErrorState("Registrace se nepovedla");
        showFlash("error", "Registrace se nepovedla");
      }
    } finally {
      setLoading(false);
    }
  };

  const showError = (field: FieldName) => Boolean(touched[field] && fieldError[field]);

  return (
    <>
      <RegistrationPanel
        onSubmit={handleSubmit}
        errorState={errorState}
        onChange={handleChange}
        onBlur={handleBlur}
        valueState={valueState}
        loading={loading}
        fieldError={fieldError}
        showError={showError}
      />
    </>
  );
};
export default RegistrationPage;
