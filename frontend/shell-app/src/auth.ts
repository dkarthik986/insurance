import { api } from "./api";

let accessToken: string | null = null;
let refreshPromise: Promise<string | null> | null = null;

export const setAccessToken = (token: string | null) => { accessToken = token; };
export const getAccessToken = () => accessToken;

export async function refreshAccessToken() {
  if (refreshPromise) return refreshPromise;
  refreshPromise = api.post("/auth/refresh", undefined, { withCredentials: true })
    .then((response) => {
      const token = response.data?.data?.accessToken ?? null;
      setAccessToken(token);
      return token;
    })
    .catch(() => {
      setAccessToken(null);
      return null;
    })
    .finally(() => { refreshPromise = null; });
  return refreshPromise;
}

export async function initializeAuth() {
  const token = await refreshAccessToken();
  if (!token) return null;
  try {
    const response = await api.get("/auth/me", { withCredentials: true });
    return response.data?.data ?? null;
  } catch {
    setAccessToken(null);
    return null;
  }
}
