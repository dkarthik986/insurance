import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";
import federation from "@originjs/vite-plugin-federation";

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, "..", "");
  const remote = (key: string) => `${env[key]}/assets/remoteEntry.js`;
  return {
    plugins: [
      react(),
      federation({
        name: "shell",
        remotes: {
          dashboard: remote("VITE_MFE_DASHBOARD"),
          customers: remote("VITE_MFE_CUSTOMERS"),
          policies: remote("VITE_MFE_POLICIES"),
          vehicles: remote("VITE_MFE_VEHICLES"),
          notifications: remote("VITE_MFE_NOTIFICATIONS"),
          reports: remote("VITE_MFE_REPORTS"),
          portal: remote("VITE_MFE_PORTAL")
        },
        shared: ["react", "react-dom", "react-router-dom", "zustand", "antd", "axios", "@tanstack/react-query"]
      })
    ],
    envDir: "..",
    server: { port: Number(env.VITE_SHELL_PORT || 3000), host: "0.0.0.0" },
    preview: { port: Number(env.VITE_SHELL_PORT || 3000) },
    build: { target: "esnext" }
  };
});

