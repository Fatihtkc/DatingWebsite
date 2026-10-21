import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import ChatList from "./chatList";
import ChatWindow from "./chatWindow";
import { FaBars, FaArrowLeft, FaUser } from "react-icons/fa";
import api from "./api/axiosInstance";
import "./css/chat.css";

function useQuery() {
  return new URLSearchParams(useLocation().search);
}

function ChatScreen() {
  const query = useQuery();
  const userFromMatch = query.get("user");
  const navigate = useNavigate();

  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [selectedChat, setSelectedChat] = useState(null);
  const [lastMessages, setLastMessages] = useState({});
  const [users, setUsers] = useState([]);

  const userId = sessionStorage.getItem("id");

  const currentUser = {
    id: Number(sessionStorage.getItem("id")),
    fullName: sessionStorage.getItem("name") || "Me",
  };

  useEffect(() => {
    const fetchMatches = async () => {
      try {
        const response = await api.get(`/matches/${userId}`);
        setUsers(response.data || []);
      } catch (error) {
        console.error("Error fetching matches:", error);
      }
    };

    fetchMatches();
  }, [userId]);

  useEffect(() => {
    if (userFromMatch && users.length > 0) {
      const matchedUser = users.find(user => user.user2.fullName === userFromMatch);
      if (matchedUser) {
        const newChatId = Date.now();
        const userImage = matchedUser.images?.[0]?.imageUrl || "https://via.placeholder.com/50";
        setSelectedChat({ id: newChatId, name: userFromMatch, image: userImage });
        setLastMessages(prev => ({ ...prev, [newChatId]: "No messages yet" }));
      }
    }
  }, [userFromMatch, users]);

  const handleLogoClick = () => navigate("/index");
  const handleBack = () => navigate("/giriş");
  const toggleMenu = () => setIsMenuOpen(!isMenuOpen);
  const goToProfileDetail = () => navigate("/ProfilePage");
  const handleLogout = () => { sessionStorage.clear(); navigate("/login"); };

    const handleEditProfile = () => {
    navigate('/ProfilePage'); 
  };

  const goToLikesPage = () => {
    navigate('/likes');
  };

  const goToMatchScreen = () => {
    navigate('/matchScreen'); 
  };

  const goToMessagesScreen = () => {
    navigate('/chatScreen'); 
  };

  return (
    <div>
      <header>
        <div className="logo">
          <h1 className="logo-text" onClick={handleLogoClick} style={{ cursor: "pointer" }}>SoulM</h1>
        </div>
        <div className="header-divider"></div>
        <div className="menu-icon" onClick={toggleMenu}>
          <FaBars />
        </div>
        <ul className={`menu ${isMenuOpen ? 'open' : ''}`}>
          <li><a href="#" onClick={goToMatchScreen}>Matches</a></li>
          <li><a href="#" onClick={goToMessagesScreen}>Messages</a></li>
          <li><a href="#" onClick={goToLikesPage}>Likes</a></li>
        </ul>
        <div className="profile-container">
          <ul className="profile">
            <li className="profile-item">
              <a href="#"> <FaUser /> Profile</a>
              <ul className="dropdown">
                <li><a href="#" onClick={handleLogout}>Logout</a></li>
                <li><a href="#" onClick={handleEditProfile}>Edit Profile</a></li>
              </ul>
            </li>
          </ul>
        </div>
      </header>

      <div className="chat-header3">
        <button className="back-button3" onClick={handleBack}>
          <FaArrowLeft style={{ marginRight: "5px" }} />
        </button>
        <span>📩 Messages</span>
      </div>

      <div className="chat-content">
        <ChatList
          selectedChat={selectedChat}
          setSelectedChat={setSelectedChat}
          lastMessages={lastMessages}
          users={users}
        />
        <ChatWindow
          selectedChat={selectedChat}
          setLastMessages={setLastMessages}
          currentUser={currentUser}
        />
      </div>
    </div>
  );
}

export default ChatScreen;
