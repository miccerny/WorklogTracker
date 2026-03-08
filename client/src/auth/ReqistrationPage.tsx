import { useState, type ComponentProps } from "react";
import type { UserAuthType } from "./UserAuth.types";
import { apiPost } from "../utils/api";
import { useFlash } from "../context/flash";
import { HttpRequestError } from "../errors/HttpRequestError";
import { useNavigate } from "react-router-dom";
import RegistrationPanel from "./RegistrationPanel";
import type { FieldError } from "./FieldError.types";

/**
 * Allowed field names from registration form data.
 */
type FieldName = keyof UserAuthType;

/**
 * Tracks which fields were touched by the user.
 * We use this to show errors only after interaction.
 */
type Touched = {
  name?: boolean;
  username?: boolean;
  password?: boolean;
  confirmPassword?: boolean;
};

/**
 * Basic email format validation.
 */
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

/**
 * Form submit event type from native form props.
 */
type FormSubmitEvent = Parameters<
  NonNullable<ComponentProps<"form">["onSubmit"]>
>[0];

/**
 * Registration page container.
 * Handles form state, validation, submit, and error feedback.
 *
 * @returns Registration form page component.
 */
export const RegistrationPage = () => {
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const [valueState, setValueState] = useState<UserAuthType>({
    name: "",
    username: "",
    password: "",
    confirmPassword: "",
  });
  const [errorState, setErrorState] = useState<string>("");
  const [touched, setTuched] = useState<Touched>({});
  const [fieldError, setFieldsError] = useState<FieldError>({});
  const { showFlash } = useFlash();

  /**
   * Updates one field in form state.
   *
   * @param name Field name to update.
   * @param value New field value.
   * @returns void
   */
  const setField = (name: FieldName, value: string) => {
    setValueState((prev) => ({ ...prev, [name]: value }));
  };

  /**
   * Marks one field as touched.
   *
   * @param name Field name that was interacted with.
   * @returns void
   */
  const touchField = (name: FieldName) => {
    setTuched((prev) => ({ ...prev, [name]: true }));
  };

  /**
   * Validates registration form values.
   *
   * @param v Current form values.
   * @returns Map of field errors (empty when valid).
   */
  const validate = (v: UserAuthType): FieldError => {
    const nextField: FieldError = {};

    if (!v.name.trim()) nextField.name = "Zadej celé jméno";
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

  /**
   * Runs validation and stores errors into state.
   *
   * @param v Current form values.
   * @returns Calculated field error map.
   */
  const validateAndSet = (v: UserAuthType) => {
    const next = validate(v);
    setFieldsError(next);
    return next;
  };

  /**
   * Handles registration form submit.
   *
   * @param e Form submit event.
   * @returns Promise that resolves after API call and state updates.
   */
  const handleSubmit = async (e: FormSubmitEvent) => {
    e.preventDefault();
    setErrorState("");
    // Mark all fields as touched so user can immediately see all validation errors.
    setTuched({
      name: true,
      username: true,
      password: true,
      confirmPassword: true,
    });

    // Stop submit early when any validation error exists.
    const nextErrors = validateAndSet(valueState);
    if (Object.keys(nextErrors).length > 0) return;

    // confirmPassword is needed only on frontend, backend expects registrationData without it.
    const { confirmPassword: _confirmPassword, ...registrationData } =
      valueState;

    try {
      setLoading(true);
      await apiPost("/auth/register", registrationData);
      showFlash("success", "Registrace proběhla úspěšně");
      navigate("/auth/login");
    } catch (e) {
      if (e instanceof HttpRequestError) {
        setErrorState(e.message || "Registrace se nepovedla");
        showFlash("error", e.message || "Registrace se nepovedla");

        const d = e.details as any;
        const fieldMap = d?.errors || d?.fieldErrors;
        if (fieldMap && typeof fieldMap === "object") {
          // If backend returns field-level errors, map them directly to form fields.
          setFieldsError(fieldMap);
          setTuched((prev) => ({ ...prev, ...Object.keys(fieldMap) }));
        }
      } else {
        setErrorState("Registrace se nepovedla");
        showFlash("error", "Registrace se nepovedla");
      }
    } finally {
      setLoading(false);
    }
  };

  /**
   * Returns visible error text for one field.
   * Error is shown only when the field was touched.
   *
   * @param field Field name.
   * @returns Error text or falsy value when hidden.
   */
  const showError = (field: FieldName) => touched[field] && fieldError[field];

  return (
    <>
      <RegistrationPanel
        onSubmit={handleSubmit}
        errorState={errorState}
        valueState={valueState}
        loading={loading}
        fieldError={fieldError}
        showError={showError}
        setField={setField}
        touch={touchField}
      />
    </>
  );
};
export default RegistrationPage;
