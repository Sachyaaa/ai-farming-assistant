import { useState } from "react";
import Login from "./Login";
import ChatApp from "./ChatApp";
import "./App.css";

function App() {

  const [authenticated, setAuthenticated] =
    useState(false);

  if (!authenticated) {
    return (
      <Login
        onLogin={() => setAuthenticated(true)}
      />
    );
  }

  return (
    <ChatApp
      onLogout={() => setAuthenticated(false)}
    />
  );
}

export default App;