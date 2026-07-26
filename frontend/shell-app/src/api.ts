import axios from "axios";
export const api = axios.create({ baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8081/api/v1", timeout: 10_000 });
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("insuredesk-token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
api.interceptors.response.use((r) => r, async (error) => {
  if (error.response?.status === 401) localStorage.removeItem("insuredesk-token");
  return Promise.reject(error);
});
