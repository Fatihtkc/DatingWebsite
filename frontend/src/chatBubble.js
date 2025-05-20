// src/components/ChatBubble.js
import React from "react";
import "./css/chat.css";

const ChatBubble = ({ message, isMine }) => {
  return (
    <div className={`chat-bubble ${isMine ? "mine" : "theirs"}`}>
      <p>{message.text}</p>
    </div>
  );
};

export default ChatBubble;