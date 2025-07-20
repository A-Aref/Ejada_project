import { reactRouter } from "@react-router/dev/vite";
import tailwindcss from "@tailwindcss/vite";
import { defineConfig } from "vite";
import tsconfigPaths from "vite-tsconfig-paths";

export default defineConfig({
  plugins: [tailwindcss(), reactRouter(), tsconfigPaths()],
  server: {
    // Handle Chrome DevTools and other .well-known requests
    middlewareMode: false,
    fs: {
      allow: ['..']
    }
  },
  // Handle requests that shouldn't reach React Router
  define: {
    // This can help with development builds
    'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV || 'development')
  }
});
