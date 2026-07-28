import axios from "axios";
import { getAccessToken, refreshAccessToken, setAccessToken } from "./auth";
export const api = axios.create({ baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8023/api/v1", timeout: 10_000, withCredentials: true });
api.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
api.interceptors.response.use((r) => r, async (error) => {
  const original = error.config;
  const isAuthBootstrapRequest =
    original?.url?.includes("/auth/refresh") ||
    original?.url?.includes("/auth/login");
  if (error.response?.status === 401 && original && !original._refreshAttempt && !isAuthBootstrapRequest) {
    original._refreshAttempt = true;
    const token = await refreshAccessToken();
    if (token) {
      original.headers.Authorization = `Bearer ${token}`;
      return api(original);
    }
    setAccessToken(null);
  }
  return Promise.reject(error);
});
