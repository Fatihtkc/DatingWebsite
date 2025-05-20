import React, { useState , useEffect} from "react";
import "./css/chat.css";
import api from "./api/axiosInstance";

function ChatList({ selectedChat, setSelectedChat, lastMessages, goToProfileDetail }) {
  const [menuOpen, setMenuOpen] = useState(null); // Açık olan menüyü takip et
  const [users, setUsers] = useState([]);

  useEffect(() => {
    fetchMatches();
  }, []);

  const userId = sessionStorage.getItem("id");
  const fetchMatches = async () => {
    try {
      const response = await api.get(`/matches/${userId}`);
      console.log('API Response:', response.data);
  
      const backendMatches = response.data;
  
      setUsers(backendMatches);
    } catch (error) {
      console.error("Error fetching matches:", error);
    }
  };

  const [isReportModalOpen, setIsReportModalOpen] = useState(false);
  const [reportMessage, setReportMessage] = useState("");
  const [reportReason, setReportReason] = useState("");

  const reportUser = (user) => {
    alert(`User ${user.fullName} has been reported.`);
    setMenuOpen(null);
  };

  const removeUser = (userId) => {
    setUsers((prevUsers) => prevUsers.filter(user => user.id !== userId));
    setMenuOpen(null);
  };

  const openReportModal = () => {
    setIsReportModalOpen(true);
  };

  const submitReport = () => {
    if (!reportReason) return;
    setReportMessage(`Your report about "${reportReason}" has been successfully submitted. We will review it and get back to you soon.`);

    setIsReportModalOpen(false);
    setTimeout(() => {
      setReportMessage(""); 
    }, 4000); 
  };

  const handleChatClick = (user) => {
    const imageUrl = user.user2.images?.[0]?.imageUrl || "https://via.placeholder.com/50";
    setSelectedChat({
      id: user.user2.id, // match ID olabilir
      name: user.user2.fullName,
      image: imageUrl,
      user: user.user2, // tüm kullanıcı bilgilerini geçmek istersen
    });
  };

  return (
    <div className="chat-list">
      {users.map(user => (
        <div 
          key={user.user2.id} 
          className={`chat-user ${selectedChat?.id === user.id ? "selected" : ""}`}
          onClick={() => handleChatClick(user)}
        >
          <img src={user.user2.images?.[0]?.imageUrl} alt={user.user2.fullName} className="profile-pic" onClick={(e) => {
              e.stopPropagation(); // Prevent triggering parent onClick
              goToProfileDetail(user);
            }}/>
          <div>
            <p><strong>{user.user2.fullName}</strong></p>
            <p className="last-message">
          {lastMessages[user.user2.id] 
            ? lastMessages[user.user2.id].length > 20 
              ? lastMessages[user.user2.id].substring(0, 20) + "..." 
              : lastMessages[user.user2.id] 
            : "No messages yet."
          }
        </p>
          </div>

          {/* Üç Nokta Butonu */}
          <div className="menu-container">
            <button 
              className="menu-button" 
              onClick={(e) => { e.stopPropagation(); setMenuOpen(user.id); }}
            >
              ⋮
            </button>
          </div>
        </div>
      ))}

      {/* Sidebar'daki Üç Nokta Menü (Ortada Açılacak) */}
      {menuOpen !== null && (
        <div className="sidebar-menu">
          <button onClick={openReportModal}>🚨 Report</button>
          <button onClick={() => removeUser(menuOpen)}>🗑️ Delete</button>
          <button onClick={() => setMenuOpen(null)}>❌ Close</button>
        </div>
      )}
      {isReportModalOpen && (
        <div className="report-modal">
          <h3>Report User</h3>
          <p>Select a reason for reporting:</p>
          <select value={reportReason} onChange={(e) => setReportReason(e.target.value)}>
            <option value="">-- Select Reason --</option>
            <option value="Fake Photo">Fake Photo</option>
            <option value="Inappropriate Behavior">Inappropriate Behavior</option>
            <option value="Scam / Fraud">Scam / Fraud</option>
            <option value="Harassment">Harassment</option>
            <option value="Other">Other</option>
          </select>
          <button onClick={submitReport} className="report-submit-btn">Submit Report</button>
          <button onClick={() => setIsReportModalOpen(false)} className="close-modal-btn">Cancel</button>
        </div>
      )}

      {reportMessage && (
        <div className="report-notification2">
          {reportMessage}
        </div>
      )}
    </div>
  );
}

export default ChatList;