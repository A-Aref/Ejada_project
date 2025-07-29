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
    },
    proxy: {
      // Proxy OAuth2 requests to WSO2
      '/oauth2': {
        target: 'https://localhost:9443',
        changeOrigin: true,
        secure: false,
        configure: (proxy) => {
          proxy.on('error', (err) => {
            console.log('OAuth2 proxy error', err);
          });
          proxy.on('proxyReq', (proxyReq, req) => {
            console.log('Sending OAuth2 Request to the Target:', req.method, req.url);
          });
          proxy.on('proxyRes', (proxyRes, req) => {
            console.log('Received OAuth2 Response from the Target:', proxyRes.statusCode, req.url);
          });
        },
      },
      // Proxy API requests to WSO2 API Gateway
      '/api': {
        target: 'http://localhost:8280',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '/Vbank/1.0.0'),
        configure: (proxy) => {
          proxy.on('error', (err) => {
            console.log('API proxy error', err);
          });
          proxy.on('proxyReq', (proxyReq, req) => {
            console.log('Sending API Request to the Target:', req.method, req.url);
          });
          proxy.on('proxyRes', (proxyRes, req) => {
            console.log('Received API Response from the Target:', proxyRes.statusCode, req.url);
          });
        },
      }
    }
  },
  // Handle requests that shouldn't reach React Router
  define: {
    // This can help with development builds
    'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV || 'development')
  }
});
