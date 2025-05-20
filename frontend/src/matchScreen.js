import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { FaUser, FaBars, FaArrowLeft } from "react-icons/fa";
import "./css/match.css";
import api from "./api/axiosInstance";

function MatchScreen(likedProfiles) {
  const navigate = useNavigate();
  const [matches, setMatches] = useState([]);
  const [isReportModalOpen, setIsReportModalOpen] = useState(false);
  const [reportMessage, setReportMessage] = useState("");
  const [reportReason, setReportReason] = useState("");
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const userId = sessionStorage.getItem('id');
  const [currentProfile, setCurrentProfile] = useState(null);
  const [imageError, setImageError] = useState(false);
  const [notification, setNotification] = useState("");


  useEffect(() => {
    fetchMatches();
  }, [likedProfiles]);

  const fetchMatches = async () => {
    try {
      const response = await api.get(`/matches/${userId}`);
      console.log('API Response:', response.data);
  
      const backendMatches = response.data;
  
      setMatches(backendMatches);
    } catch (error) {
      console.error("Error fetching matches:", error);
      setMatches(likedProfiles || []);
    }
  };

  const removeMatch = async (id) => {
    try {
      await api.delete(`/matches/${id}`);
      
      setMatches(matches.filter(match => match.id !== id));
      console.log("Match .");
    } catch (error) {
      console.error("Match silinirken hata oluştu:", error);
      alert("Failed to remove match. Please try again later.");
    }
  };

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  const openChat = (user) => {
    navigate(`/chatScreen?user=${user.fullName}`);
  };

  const handleLogout = () => {
    console.log('Çıkış..');
    sessionStorage.clear();
    navigate('/login'); 
  };

  const handleViewProfile = (profile) => {
    console.log('Navigating to profile with ID:', profile.id);
    if (!profile.images || profile.images.length === 0) {
      setImageError(true);
      setNotification("This profile does not have a valid image. Cannot view profile."); 
      setTimeout(() => setNotification(""), 4000);
    } else {
      navigate(`/profile/${profile.id}`, { state: { profile } });
    }
  };

  const handleEditProfile = () => {
    navigate('/ProfilePage'); 
  };

  const goToLikesPage = () => {
    navigate('/likes', { state: { likedProfiles } }); 
  };

  const goToMatchScreen = () => {
    navigate('/matchScreen'); 
  };

  const goToMessagesScreen = () => {
    navigate('/chatScreen'); 
  };

  const handleLogoClick = () => {
    navigate("/index");
  };

  const handleBack = () => {
    navigate("/giriş"); 
  };

  const openReportModal = (profile) => {
    setCurrentProfile(profile);
    setIsReportModalOpen(true);
  };
  

  const submitReport = () => {
    if (!reportReason) return;
    api.post('/complaints', {
      "complainant": { "id": userId },
      "complained": { "id": currentProfile.id },
      "reason": reportReason,
    },{
      headers: {
        Authorization: `Bearer ${sessionStorage.getItem('token')}`
      }
    }).catch(err => console.error('Error reporting user:', err));

    setReportMessage(`Your report for ${currentProfile.name} about "${reportReason}" has been received. We will review it and get back to you soon.`);
    setIsReportModalOpen(false);
    setTimeout(() => {
      setReportMessage("");
    }, 4000);
  };

  return (
    <>
      {/* Header kısmı */}
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

      {/* İçerik kısmı */}
      <div className="page-container3">
        <button className="back-button3" onClick={handleBack}>
          <FaArrowLeft style={{ marginRight: "5px" }} />
        </button>
        <h1>Matched Profiles</h1>
        <div className="match2-list">
        {matches.length === 0 ? (
          <p>No matches yet.</p>
        ) : (
          matches.map((match) => {
            const otherUser = match.user1 ? match.user2 : match.user1;
            return (
              <div key={match.id} className="match2-card">
                <img 
                  src={(otherUser?.images?.[0]?.imageUrl) || "https://via.placeholder.com/150"} 
                  alt={otherUser?.fullName || "Unknown"} 
                  className="match2-image"  
                  onClick={() => handleViewProfile(otherUser)}
                  style={{ cursor: "pointer" }}
                />
                <p className="match-name">{otherUser?.fullName || "Unknown"}</p>
                <div className="button2-group">
                    <button onClick={() => openChat(otherUser)}>💬 Message</button>
                    <button onClick={() => removeMatch(match.id)}>❌ Remove</button>
                    <button onClick={() => openReportModal(otherUser)}>🚨 Report</button>
                  </div>
              </div>
            );
          })
        )}
        </div>
      </div>

        {/* Report modal */}
        {isReportModalOpen && (
        <div className="report-modal">
          <h3>Report {currentProfile?.fullName || "User"}</h3>
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
        

      {notification && (
        <div className="notification">
          {notification}
        </div>
      )}

    <div className="footer-summary2">
    &copy; SoulM.com | Designed by Group 19
 </div>
</>
  );
}

export default MatchScreen;