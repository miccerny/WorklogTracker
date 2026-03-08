/**
 * Field-level validation errors for auth forms.
 * Each property is optional because not every field fails validation.
 */
export type FieldError = {
  /** Error message for full name field. */
  name?: string;
  /** Error message for username/email field. */
  username?: string;
  /** Error message for password field. */
  password?: string;
  /** Error message for confirm password field. */
  confirmPassword?: string;
};
