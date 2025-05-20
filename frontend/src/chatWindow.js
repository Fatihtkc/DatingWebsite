import React, { useState, useEffect } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import "./css/chat.css";
import api from "./api/axiosInstance";

function ChatWindow({ selectedChat, setLastMessages, currentUser }) {
  const [messages, setMessages] = useState([]);
  const [client, setClient] = useState(null);
  const [newMessage, setNewMessage] = useState("");

  // 1️⃣ Geçmiş mesajları fetch et
  useEffect(() => {
    if (!selectedChat) return;
    const fetchMessages = async () => {
      try {
        const res = await api.get(
          `/api/messages/${currentUser.id}/${selectedChat.id}`
        );
        setMessages(res.data || []);
      } catch (err) {
        console.error("Error fetching messages:", err);
      }
    };
    fetchMessages();
  }, [selectedChat, currentUser.id]);

  // 2️⃣ WebSocket bağlantısı ve abonelik
  useEffect(() => {
    if (!currentUser) return;

    const stompClient = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/chat"),
      onConnect: () => {
        stompClient.subscribe(
          `/topic/messages/${currentUser.id}`,
          (msg) => {
            const body = JSON.parse(msg.body);
            setMessages((prev) => [...prev, body]);
            setLastMessages && setLastMessages(body);
          }
        );
      },
    });

    stompClient.activate();
    setClient(stompClient);

    return () => stompClient.deactivate();
  }, [currentUser, setLastMessages]);

  const sendMessage = () => {
    if (client && newMessage.trim()) {
      const msg = {
        senderId: currentUser.id,
        receiverId: selectedChat.id,
        content: newMessage,
        isImage: false,
      };
      client.publish({
        destination: "/app/sendMessage",
        body: JSON.stringify(msg),
      });
      setNewMessage("");
    }
  };

  if (!selectedChat)
    return <div className="chat-window empty">Select a chat</div>;

  return (
    <div className="chat-window">
      {/* Header */}
      <div className="chat-header2">
        <img
          src={selectedChat.image || "https://via.placeholder.com/50"}
          alt={selectedChat.name}
          className="profile-pic-large"
        />
        <h4>{selectedChat.name}</h4>
      </div>

      {/* Mesajlar */}
      <div className="messages">
        {messages.map((msg, idx) => {
          const isMine = msg.senderId === currentUser.id;
          return (
            <div key={idx} className={`message ${isMine ? "mine" : "theirs"}`}>
              <div className={`bubble ${isMine ? "mine" : "theirs"}`}>
                {msg.isImage ? (
                  <img
                    src={msg.content}
                    alt="Message content"
                    className="message-image"
                  />
                ) : (
                  <p>{msg.content}</p>
                )}
                <span className="timestamp">
                  {new Date(msg.sentAt).toLocaleTimeString([], {
                    hour: "2-digit",
                    minute: "2-digit",
                  })}
                </span>
              </div>
            </div>
          );
        })}
      </div>

      {/* Input */}
      <div className="message-input">
        <input
          type="text"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          placeholder="Type a message..."
        />
        <button onClick={sendMessage} disabled={!newMessage.trim()}>
          Send
        </button>
      </div>
    </div>
  );
}

export default ChatWindow;
