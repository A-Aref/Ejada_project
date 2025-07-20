import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
  route("/", "routes/login.tsx"),
  route("/register", "routes/register.tsx"),
  route("/account", "routes/account.tsx"),
] satisfies RouteConfig;
