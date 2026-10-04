import { useState } from "react";
import { login, register } from "./services/chatService";

type Props = {
  onLogin: () => void;
};

function Login({ onLogin }: Props) {

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [registerMode, setRegisterMode] = useState(false);

  const [error, setError] = useState("");

  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {

    if (!email.trim() || !password.trim()) {
      setError("Email and password are required.");
      return;
    }

    setLoading(true);
    setError("");

    try {

      if (registerMode) {
        await register(email, password);
      }

      await login(email, password);

      onLogin();

    } catch (error) {

      console.error(error);

      setError(
        registerMode
          ? "Registration failed."
          : "Invalid email or password."
      );

    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">

      <div className="login-card">

        <h1>🌱 AI Farming Assistant</h1>

        <h2>
          {registerMode ? "Create account" : "Welcome back"}
        </h2>

        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(event) =>
            setEmail(event.target.value)
          }
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(event) =>
            setPassword(event.target.value)
          }
        />

        {error && (
          <p className="error">
            {error}
          </p>
        )}

        <button
          onClick={handleSubmit}
          disabled={loading}
        >
          {loading
            ? "Please wait..."
            : registerMode
              ? "Create account"
              : "Login"}
        </button>

        <button
          className="secondary-button"
          onClick={() => {
            setRegisterMode(!registerMode);
            setError("");
          }}
        >
          {registerMode
            ? "Already have an account? Login"
            : "Create new account"}
        </button>

      </div>

    </div>
  );
}

export default Login;