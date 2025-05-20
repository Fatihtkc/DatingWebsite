import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { FaUser, FaBars } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import api from "./api/axiosInstance";
import { FaArrowLeft } from "react-icons/fa";

const LikesPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [likedProfiles, setLikedProfiles] = useState([]);
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const likerId = sessionStorage.getItem('id');

  useEffect(() => {
    const fetchLikedProfiles = async () => {
      try {
        const response = await api.get(`/likes/${likerId}`);  // Backend URL'i kullanın
        setLikedProfiles(response.data);
      } catch (error) {
        console.error('Error fetching liked profiles:', error);
      }
    };

    fetchLikedProfiles();
  }, []);

  const removeProfile = async (id) => {
    try {
      await api.delete(`likes/${id}`);
      
      // API'den başarılı bir yanıt aldıktan sonra, lokal likedProfiles dizisini güncelliyoruz.
      const updatedProfiles = likedProfiles.filter((profile) => profile.id !== id);
      setLikedProfiles(updatedProfiles);
      
      console.log("Profile removed successfully!");
    } catch (error) {
      console.error("Error removing profile:", error);
    }
  };
  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  const handleLogout = () => {
    console.log('Çıkış..');
    sessionStorage.clear();
    navigate('/login'); 
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

  const calculateAge = (birthDateString) => {
    const today = new Date();
    const birthDate = new Date(birthDateString);
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
  
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--; // Doğum günü henüz kutlanmadıysa yaşı 1 azalt
    }
  
    return age;
  };

  return (
    <>
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
        <li><a href="#" onClick={goToLikesPage} >Likes</a></li>
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
      <div className="likes-page">
        <button className="back-button3" onClick={handleBack}>
                <FaArrowLeft style={{ marginRight: "5px" }} />
              </button>
        <h1>Liked Profiles</h1>
        {likedProfiles.length > 0 ? (
          <ul className="profile-list">
            {likedProfiles.map((profile) => (
              <li className="profile-item2" key={profile.id}>
                <img src={profile.liked.images[0].imageUrl} alt="Profile" />
                <div>
                  <h2>{profile.liked.fullName}, {calculateAge(profile.liked.birthDate)} </h2>
                  <p>{profile.shorterbio}</p>
                  <button className="remove-button" onClick={() => removeProfile(profile.id)}>
                    <span>Remove Profile</span> 
                  </button>
                </div>
              </li>
            ))}
          </ul>
        ) : (
          <p className="no-liked-profiles">No liked profiles yet.</p>
        )}
      </div>
      <div className="footer-summary2">
        &copy; SoulM.com | Designed by Group 19
      </div>
    </>
  );
};

export default LikesPage;