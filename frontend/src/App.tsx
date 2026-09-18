import { useEffect, useState } from "react";
import "./App.css";
import LoginPage from "./auth/LoginForm";
import TaskPageIndex from "./Tasks/TaskPageIndex";
import type User from "./auth/model/User";
import { getUser } from "./lib/api";
import {
  Navigate,
  Route,
  Routes,
  useLocation,
  useNavigate,
} from "react-router-dom";

function App() {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const location = useLocation();

  async function loadUser(token: string | null) {
    if (token == null) {
      setLoading(false);
      navigate("/auth", { replace: true });
      return;
    }

    const loggedUser = await getUser(token);

    if (loggedUser != null) {
      setUser(loggedUser);
      setLoading(false);
      if (location.pathname === "/auth" || location.pathname === "/") {
        navigate("/tasks", { replace: true });
      }
      return;
    }

    localStorage.removeItem("TM_DATA");
    setUser(null);
    setLoading(false);
    navigate("/auth", { replace: true });
  }

  useEffect(() => {
    void loadUser(localStorage.getItem("TM_DATA"));
  }, []);

  if (loading) return null;

  return (
    <Routes>
      <Route
        path="/auth"
        element={
          user ? (
            <Navigate to="/tasks" replace />
          ) : (
            <LoginPage loadUser={loadUser} />
          )
        }
      />
      <Route
        path="/tasks"
        element={
          user ? (
            <TaskPageIndex user={user} setUser={setUser} />
          ) : (
            <Navigate to="/auth" replace />
          )
        }
      />
      <Route
        path="/"
        element={<Navigate to={user ? "/tasks" : "/auth"} replace />}
      />
      <Route
        path="*"
        element={<Navigate to={user ? "/tasks" : "/auth"} replace />}
      />
    </Routes>
  );
}

export default App;
