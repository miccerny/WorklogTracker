export type UserAuthType = {
  name: string;
  username: string;
  password: string;
  confirmPassword: string;
};

export type AuthTokenResponse =
  | string
  | {
      token?: string;
      accessToken?: string;
      jwt?: string;
      data?: {
        token?: string;
        accessToken?: string;
        jwt?: string;
      };
    };

export function readAuthToken(response: AuthTokenResponse): string | null {
  if (typeof response === "string") {
    return response;
  }

  return (
    response.token ??
    response.accessToken ??
    response.jwt ??
    response.data?.token ??
    response.data?.accessToken ??
    response.data?.jwt ??
    null
  );
}
