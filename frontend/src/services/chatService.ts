const API_URL = "http://localhost:8080";

export async function register(
  email: string,
  password: string
) {
  const response = await fetch(
    `${API_URL}/api/auth/register`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify({
        email,
        password,
      }),
    }
  );

  if (!response.ok) {
    throw new Error("Registration failed");
  }

  return response.json();
}

export async function login(
  email: string,
  password: string
) {
  const response = await fetch(
    `${API_URL}/api/auth/login`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify({
        email,
        password,
      }),
    }
  );

  if (!response.ok) {
    throw new Error("Invalid email or password");
  }

  return response.json();
}

export async function logout() {
  const response = await fetch(
    `${API_URL}/api/auth/logout`,
    {
      method: "POST",
      credentials: "include",
    }
  );

  if (!response.ok) {
    throw new Error("Logout failed");
  }
}

export async function getConversations() {
  const response = await fetch(
    `${API_URL}/api/conversations`,
    {
      credentials: "include",
    }
  );

  if (!response.ok) {
    throw new Error("Unable to load conversations");
  }

  return response.json();
}

export async function createConversation() {
  const response = await fetch(
    `${API_URL}/api/conversations`,
    {
      method: "POST",
      credentials: "include",
    }
  );

  if (!response.ok) {
    throw new Error("Unable to create conversation");
  }

  return response.json();
}

export async function getMessages(
  conversationId: number
) {
  const response = await fetch(
    `${API_URL}/api/conversations/${conversationId}/messages`,
    {
      credentials: "include",
    }
  );

  if (!response.ok) {
    throw new Error("Unable to load messages");
  }

  return response.json();
}

export async function streamChat(
  conversationId: number,
  message: string,
  onChunk: (chunk: string) => void
) {
  const response = await fetch(
    `${API_URL}/api/chat/stream`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify({
        conversationId,
        message,
      }),
    }
  );

  if (!response.ok || !response.body) {
    throw new Error(
      "Unable to connect to AI service"
    );
  }

  const reader = response.body.getReader();
  const decoder = new TextDecoder();

  let buffer = "";

  while (true) {

    const { value, done } =
      await reader.read();

    if (done) break;

    buffer += decoder.decode(
      value,
      { stream: true }
    );

    const events =
      buffer.split("\n\n");

    buffer =
      events.pop() ?? "";

    for (const event of events) {

      const dataLine =
        event
          .split("\n")
          .find(line =>
            line.startsWith("data:")
          );

      if (!dataLine) continue;

      const data =
        dataLine.replace(
          /^data:\s?/,
          ""
        );

      if (data === "[DONE]") {
        return;
      }

      onChunk(data);
    }
  }
}