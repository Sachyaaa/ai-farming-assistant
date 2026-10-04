# AI Farming Assistant 🌱

An AI-powered farming decision-support assistant built as a practical AI Full-Stack project.

The goal is to build a production-oriented assistant that can understand farmer questions, maintain conversation context, retrieve trusted farming knowledge, use external tools, and provide personalized farming guidance.

## 🚧 Current Progress

### Day 1 — LLM Fundamentals
- LLM fundamentals
- Tokens and context windows
- System and user prompts
- Temperature and hallucinations
- Gemini API setup
- First Gemini API call
- Model/API troubleshooting

### Day 2 — Spring Boot + Gemini
- Spring Boot backend
- Gemini API integration
- REST chat endpoint
- Request/response DTOs
- Environment-based API key configuration
- Global exception handling

### Day 3 — Prompt Engineering
- System instructions
- Farmer context
- Prompt structure
- Practical farming response guidelines
- Handling missing information
- Reducing unsupported assumptions

### Day 4 — Structured AI Output
- Structured output concepts
- Farming query classification
- Query categories
- Urgency classification
- JSON parsing and validation
- Handling invalid AI responses

### Day 5 — Streaming AI Responses
- Gemini streaming API
- Server-Sent Events (SSE)
- Streaming responses from Spring Boot
- React streaming client
- Incremental response rendering
- End-to-end frontend → backend → Gemini streaming

### Day 6 — PostgreSQL + Conversation History
- PostgreSQL setup
- Spring Data JPA
- Conversation entity
- Message entity
- Conversation/message repositories
- Conversation history persistence
- Loading previous messages into Gemini context

### Day 7 — Authentication + User-Owned Conversations
- Spring Security integration
- User registration
- BCrypt password hashing
- Login authentication
- Session-based authentication
- Protected API endpoints
- CORS configuration
- User-owned conversations
- Conversation authorization
- Conversation history API
- React login/register UI
- React chat UI
- New conversation functionality
- Logout
- Frontend/backend authentication integration

---

## 🏗️ Current Architecture

```text
┌──────────────────────┐
│     React Frontend   │
│                      │
│ Login / Register     │
│ Chat UI              │
│ Conversations        │
│ Streaming Responses  │
└──────────┬───────────┘
           │
           │ HTTP / SSE
           ▼
┌────────────────────────────┐
│      Spring Boot API       │
│                            │
│ Spring Security            │
│ Authentication             │
│ Conversation APIs          │
│ Chat APIs                  │
│ Gemini Integration         │
│ JPA / Persistence          │
└──────────┬─────────────────┘
           │
     ┌─────┴───────────┐
     ▼                 ▼
┌──────────────┐  ┌──────────────┐
│ PostgreSQL   │  │ Gemini API   │
│              │  │              │
│ Users        │  │ LLM          │
│ Conversations│  │ Streaming    │
│ Messages     │  │ Responses    │
└──────────────┘  └──────────────┘
```

## 🛠️ Tech Stack

### Backend
- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- REST APIs
- Server-Sent Events (SSE)

### AI
- Google Gemini API
- Prompt Engineering
- Structured AI Output
- Streaming LLM Responses

### Frontend
- React
- TypeScript
- Vite
- Fetch API
- SSE streaming

### Database
- PostgreSQL
- JPA / Hibernate

### Planned AI Stack
- Embeddings
- pgvector
- RAG
- Tool Calling
- AI Agents
- MCP
- AI Evaluation
- AI Security
- Observability

### Deployment / Infrastructure
- Docker
- AWS
- CI/CD

---

## 🌱 Planned AI Features

The current application is the foundation for a more capable AI farming assistant.

### RAG — Farming Knowledge Base

```text
Farming Documents
       ↓
Document Processing
       ↓
Chunking
       ↓
Embeddings
       ↓
PostgreSQL + pgvector
       ↓
Similarity Search
       ↓
Relevant Context
       ↓
Gemini
       ↓
Grounded Answer
```

The assistant will eventually use trusted agricultural documents and provide answers based on retrieved information rather than relying only on the LLM's internal knowledge.

### Tool Calling

Planned tools include:

- `getFarmDetails()`
- `getWeather()`
- `getCropInformation()`
- `getMarketPrices()`
- `createFarmTask()`
- `saveFarmJournal()`

### Agent Workflows

The assistant will eventually be able to combine:

```text
Farmer Profile
      +
Crop Information
      +
Weather
      +
Farming Knowledge
      ↓
AI Reasoning
      ↓
Personalized Crop Plan
```

### MCP

A future MCP server will expose farming-related tools and resources to the AI assistant.

### AI Security

Planned security topics:

- Prompt injection
- Indirect prompt injection
- Malicious documents
- Tool abuse
- Input validation
- Authentication and authorization
- Safe tool execution
- Human confirmation for sensitive actions

### AI Evaluation

Planned evaluation capabilities:

- Retrieval quality
- Answer groundedness
- Hallucination detection
- Test datasets
- Regression testing
- LLM response evaluation

---

## 📂 Project Structure

```text
ai-farming-assistant/
│
├── backend/
│   └── Spring Boot application
│       ├── config/
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── repository/
│       └── service/
│
├── frontend/
│   └── React + TypeScript application
│       ├── components/
│       ├── services/
│       └── pages/
│
├── .gitignore
└── README.md
```

> The exact package and folder structure may evolve as the project grows.

---

## 🔐 Environment Variables

The application uses environment variables for sensitive configuration.

### Gemini API Key

```bash
export GEMINI_API_KEY="your_api_key"
```

### Database Password

```bash
export DB_PASSWORD="your_database_password"
```

Do **not** commit real API keys, passwords, or other secrets to Git.

---

## 🚀 Running Locally

### Backend

Set the required environment variables:

```bash
export GEMINI_API_KEY="your_api_key"
export DB_PASSWORD="your_database_password"
```

Start the Spring Boot application.

The backend runs on:

```text
http://localhost:8080
```

### Frontend

Start the React/Vite application.

The frontend runs on:

```text
http://localhost:5173
```

---

## 🔑 Authentication Flow

```text
React
  │
  ├── Register
  │      ↓
  │   Spring Boot
  │      ↓
  │   BCrypt password hash
  │      ↓
  │   PostgreSQL
  │
  └── Login
         ↓
    Spring Security
         ↓
    Authenticated Session
         ↓
    Protected APIs
```

Each conversation belongs to the authenticated user.

The backend validates conversation ownership before allowing access to conversation data.

---

## 💬 Chat Flow

```text
User
 ↓
React Chat UI
 ↓
POST /api/chat/stream
 ↓
Spring Security
 ↓
Conversation ownership validation
 ↓
Load conversation history
 ↓
Gemini API
 ↓
Streaming response
 ↓
SSE
 ↓
React
 ↓
Save completed response
```

---

## 📈 Learning Roadmap

This project is being developed incrementally while learning AI Full-Stack development.

```text
Days 1–7   ✅ LLM + Backend + Auth + Chat
Days 8–14  🔜 Embeddings + RAG
Days 15–21 🔜 Tool Calling + Agents
Days 22–28 🔜 MCP + Security + Evaluation
Days 29–35 🔜 AI Developer Certification Preparation
Days 36–42 🔜 Mock Exams + Final Revision
```

---

## 🎯 Project Goal

Build a portfolio-quality AI application that demonstrates practical understanding of:

- LLM APIs
- Prompt Engineering
- Structured Outputs
- Streaming
- Authentication
- Conversation Memory
- Embeddings
- RAG
- Vector Databases
- Tool Calling
- AI Agents
- MCP
- AI Security
- AI Evaluation
- Observability
- Docker
- AWS
- Production-oriented AI application architecture

The objective is not just to build a chatbot, but to understand and implement the core components used in modern AI-powered applications.