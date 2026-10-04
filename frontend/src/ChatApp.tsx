import { useEffect, useState } from "react";
import {
    createConversation,
    getConversations,
    getMessages,
    logout,
    streamChat,
} from "./services/chatService";

type Props = {
    onLogout: () => void;
};

type Conversation = {
    id: number;
    userId: number;
    createdAt: string;
    updatedAt: string;
};

type Message = {
    id: number;
    conversationId: number;
    role: "USER" | "ASSISTANT";
    content: string;
    createdAt: string;
};

function ChatApp({ onLogout }: Props) {

    const [conversations, setConversations] =
        useState<Conversation[]>([]);

    const [conversationId, setConversationId] =
        useState<number | null>(null);

    const [messages, setMessages] =
        useState<Message[]>([]);

    const [message, setMessage] =
        useState("");

    const [streamingResponse, setStreamingResponse] =
        useState("");

    const [loading, setLoading] =
        useState(false);

    const [loadingHistory, setLoadingHistory] =
        useState(true);

    const [error, setError] =
        useState("");

    /*
     * Load conversations when the user logs in.
     */
    useEffect(() => {

        const loadConversations = async () => {

            try {

                const data =
                    await getConversations();

                setConversations(data);

                if (data.length > 0) {
                    setConversationId(data[0].id);
                }

            } catch (error) {

                console.error(error);

                setError(
                    "Unable to load conversation history."
                );

            } finally {

                setLoadingHistory(false);
            }
        };

        loadConversations();

    }, []);

    /*
     * Load messages whenever the selected
     * conversation changes.
     */
    useEffect(() => {

        if (conversationId === null) {
            setMessages([]);
            return;
        }

        const loadMessages = async () => {

            try {

                const data =
                    await getMessages(
                        conversationId
                    );

                setMessages(data);
                setStreamingResponse("");

            } catch (error) {

                console.error(error);

                setError(
                    "Unable to load messages."
                );
            }
        };

        loadMessages();

    }, [conversationId]);

    const handleNewChat = async () => {

        try {

            setError("");

            const conversation =
                await createConversation();

            setConversations(previous => [
                conversation,
                ...previous,
            ]);

            setConversationId(
                conversation.id
            );

            setMessages([]);
            setStreamingResponse("");

        } catch (error) {

            console.error(error);

            setError(
                "Unable to create conversation."
            );
        }
    };

    const handleSend = async () => {

        if (!message.trim()) {
            return;
        }

        if (conversationId === null) {
            setError(
                "Please create a conversation first."
            );
            return;
        }

        const userMessage = message;

        setMessage("");
        setError("");
        setStreamingResponse("");
        setLoading(true);

        try {

            await streamChat(
                conversationId,
                userMessage,
                (chunk) => {

                    setStreamingResponse(
                        previous =>
                            previous + chunk
                    );
                }
            );

            /*
             * Reload messages after streaming
             * so the persisted assistant response
             * appears in our message list.
             */
            const updatedMessages =
                await getMessages(
                    conversationId
                );

            setMessages(updatedMessages);
            setStreamingResponse("");

            /*
             * Refresh conversation ordering.
             */
            const updatedConversations =
                await getConversations();

            setConversations(
                updatedConversations
            );

        } catch (error) {

            console.error(error);

            setError(
                "Something went wrong while connecting to the AI service."
            );

        } finally {

            setLoading(false);
        }
    };

    const handleLogout = async () => {

        try {

            await logout();

        } catch (error) {

            console.error(error);

        } finally {

            onLogout();
        }
    };

    return (
        <div className="chat-layout">

            {/* Sidebar */}

            <aside className="sidebar">

                <div className="sidebar-header">

                    <div className="sidebar-brand">
                        <div className="sidebar-brand-icon">🌱</div>

                        <h2>AI Farming Assistant</h2>
                    </div>

                    <button
                        className="new-chat-button"
                        onClick={handleNewChat}
                    >
                        + New Chat
                    </button>

                </div>

                <div className="conversation-list">

                    {loadingHistory && (
                        <p>Loading chats...</p>
                    )}

                    {!loadingHistory &&
                        conversations.length === 0 && (
                            <div className="empty-chat">
                                <div className="empty-chat-icon">🌾</div>

                                <h2>How can I help with your farm?</h2>

                                <p>
                                    Ask about crops, irrigation, pests,
                                    fertilizer, weather, or farm planning.
                                </p>
                            </div>
                        )}

                    {conversations.map(
                        conversation => (

                            <button
                                key={conversation.id}
                                className={
                                    conversation.id ===
                                        conversationId
                                        ? "conversation active"
                                        : "conversation"
                                }
                                onClick={() =>
                                    setConversationId(
                                        conversation.id
                                    )
                                }
                            >
                                Conversation #
                                {conversation.id}
                            </button>

                        )
                    )}

                </div>

                <button
                    className="logout-button"
                    onClick={handleLogout}
                >
                    Logout
                </button>

            </aside>

            {/* Chat */}

            <main className="chat-main">

                <header className="chat-header">

                    <h1>
                        AI Farming Assistant
                    </h1>

                    {conversationId !== null && (
                        <span>
                            Conversation #{conversationId}
                        </span>
                    )}

                </header>

                <div className="messages">

                    {messages.map(msg => (

                        <div
                            key={msg.id}
                            className={
                                msg.role === "USER"
                                    ? "message user-message"
                                    : "message assistant-message"
                            }
                        >

                            <strong>
                                {msg.role === "USER"
                                    ? "You"
                                    : "AI"}
                            </strong>

                            <p>
                                {msg.content}
                            </p>

                        </div>

                    ))}

                    {streamingResponse && (

                        <div className="message assistant-message">

                            <strong>AI</strong>

                            <p>
                                {streamingResponse}
                            </p>

                        </div>

                    )}

                    {messages.length === 0 &&
                        !streamingResponse && (
                            <div className="empty-chat">
                                <h2>
                                    🌾 Ask your farming question
                                </h2>

                                <p>
                                    Get practical guidance based
                                    on your farm context.
                                </p>
                            </div>
                        )}

                </div>

                {error && (
                    <div className="error">
                        {error}
                    </div>
                )}

                <div className="input-area">

                    <input
                        type="text"
                        placeholder="Ask a farming question..."
                        value={message}
                        disabled={
                            conversationId === null ||
                            loading
                        }
                        onChange={event =>
                            setMessage(
                                event.target.value
                            )
                        }
                        onKeyDown={event => {

                            if (
                                event.key === "Enter"
                            ) {
                                handleSend();
                            }

                        }}
                    />

                    <button
                        onClick={handleSend}
                        disabled={
                            conversationId === null ||
                            loading
                        }
                    >
                        {loading
                            ? "Thinking..."
                            : "Send"}
                    </button>

                </div>

            </main>

        </div>
    );
}

export default ChatApp;