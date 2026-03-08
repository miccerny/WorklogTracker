/**
 * Registration/auth form model used on auth pages.
 */
export type UserAuthType = {
  /** Full name shown in user profile. */
  name: string;
  /** Login identifier (email in this app). */
  username: string;
  /** User password from input field. */
  password: string;
  /** Repeated password used for client-side confirmation. */
  confirmPassword: string;
};
