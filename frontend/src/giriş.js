import React, { useState, useEffect } from 'react'; 
import { FaUser, FaBars } from "react-icons/fa";
import './css/style.css';
import { Routes, Route, useNavigate } from 'react-router-dom';
import { useLocation} from 'react-router-dom';
import { FaPhone, FaEnvelope, FaLinkedin, FaInstagram, FaTwitter } from 'react-icons/fa';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';
import Login from './login.js';
import LikesPage from './likes.js';
import ChatScreen from "./chatScreen";
import MatchScreen from "./matchScreen";
import AuthForm from './signup.js';
import api from "./api/axiosInstance";
import { X, Heart, Info, FlagTriangleLeft} from 'lucide-react';
import { FaHeart } from "react-icons/fa";
import {
  FaHandshake, 
  FaSmile, 
  FaVenusMars, 
  FaMars, FaVenus, FaGenderless, FaTransgender,
  FaMapMarkerAlt, 
  FaSmokingBan, 
  FaBeer
} from "react-icons/fa";

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

const Header = ({ likedProfiles }) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const navigate = useNavigate();

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  const handleLogout = () => {
    sessionStorage.clear();
    console.log('Çıkış..');
    navigate('/login'); 
  };

  const handleEditProfile = () => {
    navigate('/ProfilePage'); 
  };

  const handleLogoClick = () => {
    navigate("/index"); 
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

  return (
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
        <li><a href="" onClick={goToLikesPage} >Likes</a></li>
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
  );
};

const Footer = () => {

    const handleSubmitClick = () => {
        console.log('Submitting..');
      }; 

  return (
    <div className="footer">
    <div className="footer-content">
        <div className="footer-section about">
          <h1 className="logo-text"><span>Soul</span>M</h1>
          <p className="site-description">
            Did the universe forget to send you its signals? Don’t worry, we’re right here! With the most accurate algorithms (and maybe a little bit of magic), we help you find someone who truly matches you. So, you’ll not only have someone to share your coffee with but also your life.
            Ready? Sign up now, because maybe your soulmate is waiting for you right this second. Believe in coincidences, but believe in 'SoulM' even more. ❤️
          </p>
          <p className="site-description-2">One soul, two hearts, endless possibilities!</p>
          <div className="contact" >
            <span><FaPhone /> &nbsp; 05397826654 </span>
            <span><FaEnvelope /> &nbsp; info@SoulM.com</span>
          </div>
          <div className="socials">
            <a href="#"><FaLinkedin /></a>
            <a href="#"><FaInstagram /></a>
            <a href="#"><FaTwitter /></a>
          </div>
        </div>
        <div className="footer-section testimonials">
          <h2>What People Say</h2>
          <br />
          <div className="testimonial">
            <p className="testimonial-text">"SoulM changed my life! I never thought I'd find someone who gets me so well." – Emily J.</p>
          </div>
          <div className="testimonial">
            <p className="testimonial-text">"The best decision I ever made was joining SoulM. It's like magic!" – Michael T.</p>
          </div>
          
        </div>
        <div className="footer-section contact-form">
          <h2>Contact us</h2>
          <br />
          <form action="index.js" method="post">
            <input type="email" name="email" className="text-input contact-input" placeholder="Your email address..." />
            <textarea rows="4" name="message" className="text-input contact-input" placeholder="Your message..."></textarea>
          </form>
        </div>
      </div>
      <div className="footer-bottom">
        &copy; SoulM.com | Designed by Group 19
      </div>
    </div>
  );
};

const LikesScreen = ({ likedProfiles, setLikedProfiles }) => {
  const [profiles, setProfiles] = useState([]);
  const [currentProfileIndex, setCurrentProfileIndex] = useState(0);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [selectedProfile, setSelectedProfile] = useState(null);
  const [isReportModalOpen, setIsReportModalOpen] = useState(false);
  const [reportMessage, setReportMessage] = useState("");
  const [reportReason, setReportReason] = useState("");
  const location = useLocation();
  const navigate = useNavigate();
  const [relationshipTypeFilter, setRelationshipTypeFilter] = useState('');
  const [minAge, setMinAge] = useState(18);    // Minimum yaş
  const [maxAge, setMaxAge] = useState(100);   // Maksimum yaş
  const [distanceFilter, setDistanceFilter] = useState(1000); 
  const [genderFilter, setGenderFilter] = useState('');
  const loggedInUserId = sessionStorage.getItem('id');
  const [userLocation, setUserLocation] = useState(null);

  useEffect(() => {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setUserLocation({
          lat: position.coords.latitude,
          lon: position.coords.longitude,
        });
      },
      (error) => {
        console.error("Location access denied:", error);
        alert("Location permission is required to filter nearby users.");
      }
    );
  }, []);

  useEffect(() => {
    if (userLocation) {
      api
        .get('/nearby', {
          params: {
            lat: userLocation.lat,
            lon: userLocation.lon,
            distanceKm: distanceFilter,
          },
        })
        .then((response) => {
          console.log("Nearby profiles:", response.data);
          setProfiles(response.data);
        })
        .catch((err) => {
          console.error('Error fetching nearby profiles:', err);
          alert('Error fetching nearby profiles. Please try again.');
        });
    }
  }, [userLocation, distanceFilter]);

  // Fetch profiles from backend
  // useEffect(() => {
  //   api.get('/profiles')
  //     .then(response => {
  //       console.log('API Response:', response.data); // API yanıtını konsola yazdır
  //       setProfiles(response.data);  // Direkt response.data'yi kullan
  //     })
  //     .catch(err => {
  //       console.error('Error fetching profiles:', err);
  //       alert('Error fetching profiles. Please try again.');
  //     });
  // }, []);

  useEffect(() => {
    if (location.state?.passwordChanged) {
      const timer = setTimeout(() => {
        navigate("/giriş", { state: {} }); // state'i temizle
      }, 3000); // Bildirimi 3 saniye göster

      return () => clearTimeout(timer); // Cleanup
    }
  }, [location, navigate]);

  if (profiles.length === 0) {
    return <div className="loading">Loading profiles...</div>;
  }

  const filteredProfiles = profiles.filter(profile => {
    const matchesGender = genderFilter === '' || profile.gender === genderFilter;
    const matchesRelationship = relationshipTypeFilter === '' || profile.relationshipType === relationshipTypeFilter;
    const matchesAge = calculateAge(profile.birthDate) >= minAge && calculateAge(profile.birthDate) <= maxAge;
    const matchesDistance = !profile.distance || profile.distance <= distanceFilter; // distance yoksa dahil et
    return matchesRelationship && matchesAge && matchesDistance && matchesGender;
  });

  const currentProfile = filteredProfiles[currentProfileIndex] || {};

  if (currentProfileIndex >= filteredProfiles.length) {
    return <div className="no-more-profiles">No more profiles to show.
    <div className="heart-animation"></div>
    <div className="filters2-container">
        <div className="filters2">
          <h3 className="filter2-title">Filter Profiles</h3>

          <div className="gender-buttons">
          <h3>Gender:</h3>
          <button className={genderFilter === '' ? 'selected' : ''} onClick={() => setGenderFilter('')}>
            <FaGenderless /> All
          </button>
          <button className={genderFilter === 'Male' ? 'selected' : ''} onClick={() => setGenderFilter('Male')}>
            <FaMars /> Male
          </button>
          <button className={genderFilter === 'Female' ? 'selected' : ''} onClick={() => setGenderFilter('Female')}>
            <FaVenus /> Female
          </button>
          <button className={genderFilter === 'Other' ? 'selected' : ''} onClick={() => setGenderFilter('Other')}>
            <FaTransgender /> Other
          </button>
        </div>


          <div className="relationship-type-buttons">
        <h3>Relationship Type:</h3>
        <button className={relationshipTypeFilter === '' ? 'selected' : ''} onClick={() => setRelationshipTypeFilter('')}>
          <i className="fas fa-users"></i> All
        </button>
        <button className={relationshipTypeFilter === 'Serious Relationship' ? 'selected' : ''} onClick={() => setRelationshipTypeFilter('Serious Relationship')}>
          <i className="fas fa-heart"></i> Serious Relationship
        </button>
        <button className={relationshipTypeFilter === 'Casual Dating' ? 'selected' : ''} onClick={() => setRelationshipTypeFilter('Casual Dating')}>
          <i className="fas fa-glass-cheers"></i> Casual Dating
        </button>
        <button className={relationshipTypeFilter === 'Friendship' ? 'selected' : ''} onClick={() => setRelationshipTypeFilter('Friendship')}>
          <i className="fas fa-handshake"></i> Friends
        </button>
      </div>

        <label>
      Age Range: {minAge} - {maxAge}
      <div className="age-range-sliders">
        {/* Min Age Slider */}
        <input
          type="range"
          min="18"
          max={maxAge}
          value={minAge}
          onChange={(e) => setMinAge(Math.min(parseInt(e.target.value), maxAge - 1))}
          className="slider min-age-slider"
        />
        
          {/* Max Age Slider */}
          <input
            type="range"
            min={minAge + 1}
            max="100"
            value={maxAge}
            onChange={(e) => setMaxAge(Math.max(parseInt(e.target.value), minAge + 1))}
            className="slider max-age-slider"
          />
        </div>
      </label>

      <label>
        Max Distance:
        <select value={distanceFilter} onChange={(e) => setDistanceFilter(parseInt(e.target.value))}>
          <option value="5">5 km</option>
          <option value="10">10 km</option>
          <option value="20">20 km</option>
          <option value="30">30 km</option>
          <option value="50">50 km</option>
          <option value="100">100 km</option>
        </select>
      </label>

      </div>
      </div>
    </div>;
    
  }
    
  const handleSwipeRight = async() => {
    try{

      if (!loggedInUserId) {
        console.error("No logged in user ID found.");
        return;
      }

      await api.post('/likes', {
        "likerId": loggedInUserId,
        "likedId": currentProfile.id
      },{
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('token')}`
        }
      }).catch(err => console.error('Error sending like:', err));

      await api.post('/matches', null,
        {
          params: {
            user1Id: loggedInUserId,
            user2Id: currentProfile.id
          }
        }
      );
      console.log('Match created:');

      setLikedProfiles([...likedProfiles, currentProfile]);
      if (currentProfileIndex < profiles.length - 1) {
        setCurrentProfileIndex(currentProfileIndex + 1);
        setCurrentImageIndex(0); 
        setSelectedProfile(null);
      }

    } catch (error) {
      console.error("Error sending like:", error);
    }
  };

  const handleSwipeLeft = () => {
    if (currentProfileIndex < profiles.length - 1) {
      setCurrentProfileIndex(currentProfileIndex + 1);
      setCurrentImageIndex(0);
      setSelectedProfile(null);
    }
  };

  const handlePrevImage = () => {
    if (!currentProfile.images || currentProfile.images.length === 0) return;
    setCurrentImageIndex((prevIndex) =>
      prevIndex === 0 ? currentProfile.images.length - 1 : prevIndex - 1
    );
  };
  
  const handleNextImage = () => {
    if (!currentProfile.images || currentProfile.images.length === 0) return;
    setCurrentImageIndex((prevIndex) =>
      prevIndex === currentProfile.images.length - 1 ? 0 : prevIndex + 1
    );
  };  
  

  const handleInfoClick = () => {
    console.log('Info..');
    setSelectedProfile(currentProfile);
  };

  const handleFlagClick = () => {
    setIsReportModalOpen(true);
  };

  const submitReport = () => {
    api.post('/complaints', {
      "complainant": { "id": loggedInUserId },
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

  const closeProfileDetails = () => {
    setSelectedProfile(null);
  };

  if (currentProfileIndex >= profiles.length) {
    return <div className="no-more-profiles">No more profiles to show.</div>;
  }


  
  const age = calculateAge(currentProfile.birthDate);

  return (
    <div className="match-screen-container">
        {location.state?.passwordChanged && (
        <div className="notification2">
          Password changed successfully!
        </div>
      )}

      <div className="filters-container">
        <div className="filters">
          <h3 className="filter-title">Filter Profiles</h3>

          <div className="gender-buttons">
          <h3>Gender:</h3>
          <button className={genderFilter === '' ? 'selected' : ''} onClick={() => setGenderFilter('')}>
            <FaGenderless /> All
          </button>
          <button className={genderFilter === 'Male' ? 'selected' : ''} onClick={() => setGenderFilter('Male')}>
            <FaMars /> Male
          </button>
          <button className={genderFilter === 'Female' ? 'selected' : ''} onClick={() => setGenderFilter('Female')}>
            <FaVenus /> Female
          </button>
          <button className={genderFilter === 'Other' ? 'selected' : ''} onClick={() => setGenderFilter('Other')}>
            <FaTransgender /> Other
          </button>
        </div>

          <div className="relationship-type-buttons">
        <h3>Relationship Type:</h3>
        <button className={relationshipTypeFilter === '' ? 'selected' : ''} onClick={() => setRelationshipTypeFilter('')}>
          <i className="fas fa-users"></i> All
        </button>
        <button className={relationshipTypeFilter === 'Serious Relationship' ? 'selected' : ''} onClick={() => setRelationshipTypeFilter('Serious Relationship')}>
          <i className="fas fa-heart"></i> Serious Relationship
        </button>
        <button className={relationshipTypeFilter === 'Casual Dating' ? 'selected' : ''} onClick={() => setRelationshipTypeFilter('Casual Dating')}>
          <i className="fas fa-glass-cheers"></i> Casual Dating
        </button>
        <button className={relationshipTypeFilter === 'Friendship' ? 'selected' : ''} onClick={() => setRelationshipTypeFilter('Friendship')}>
          <i className="fas fa-handshake"></i> Friends
        </button>
      </div>

        <label>
      Age Range: {minAge} - {maxAge}
      <div className="age-range-sliders">
        {/* Min Age Slider */}
        <input
          type="range"
          min="18"
          max={maxAge}
          value={minAge}
          onChange={(e) => setMinAge(Math.min(parseInt(e.target.value), maxAge - 1))}
          className="slider min-age-slider"
        />
        
          {/* Max Age Slider */}
          <input
            type="range"
            min={minAge + 1}
            max="100"
            value={maxAge}
            onChange={(e) => setMaxAge(Math.max(parseInt(e.target.value), minAge + 1))}
            className="slider max-age-slider"
          />
        </div>
      </label>

      <label>
        Max Distance:
        <select value={distanceFilter} onChange={(e) => setDistanceFilter(parseInt(e.target.value))}>
          <option value="5">5 km</option>
          <option value="10">10 km</option>
          <option value="20">20 km</option>
          <option value="30">30 km</option>
          <option value="50">50 km</option>
          <option value="100">100 km</option>
        </select>
      </label>
      </div>
      </div>

      <div className={`match-screen-content ${selectedProfile ? 'slide-left' : ''}`}>
        <div className="match-card">
          <div className="profile-images">
            {currentProfile.images && currentProfile.images.length > 0 && (
            <img
            src={currentProfile.images[currentImageIndex]?.imageUrl}
            alt={`Profile ${currentProfile.name} ${currentImageIndex + 1}`}
            className="match-profile-img"
            />
            )}
          </div>
          <h2 className="match-profile-name">{currentProfile.fullName}, {age}</h2>
          <p className="match-profile-bio">{currentProfile.shorterbio}</p>
          <h7 className=".match-profile-relationship-type"><FaHeart className="heart-icon" />  {currentProfile.relationshipType}</h7>
          <div className="image-navigation">
          <div className="image-navigation">
          <button onClick={handlePrevImage} className="prev-button">{'<'}</button>
          <button onClick={handleNextImage} className="next-button">{'>'}</button>
        </div>

          </div>
        </div>

        {/* Buton Container'ı */}
        <div className="match-button-container">
          <div onClick={() => { 
            handleSwipeLeft();
            setSelectedProfile(null);
            setCurrentProfileIndex(currentProfileIndex + 1);
             }} className="match-button dislike">
            <X className="w-6 h-6" />
          </div>
          <div onClick={() => {
            handleSwipeRight();
            setLikedProfiles([...likedProfiles, currentProfile]);
            setCurrentProfileIndex(currentProfileIndex + 1);
            setSelectedProfile(null);
          }} className="match-button like">
            <Heart className="w-6 h-6" />
          </div>
          <div onClick={handleInfoClick} className="match-button info">
            <Info className="w-6 h-6" />
          </div>
          <div onClick={handleFlagClick} className="match-button flag">
          <FlagTriangleLeft className="w-6 h-6" />
        </div>

        </div>
      </div>

      {/* Profil Detayları */}
      {selectedProfile && (
        <div className="profile-details-container active">
          <button className="close-button" onClick={closeProfileDetails}>X</button>

          <img 
            src={selectedProfile.images?.[currentImageIndex]?.imageUrl} 
            alt={`${selectedProfile.fullName}`} 
            className="profile-detail-img"
          />

          <div className="thumbnail-gallery">
            {selectedProfile.images?.length > 0 && selectedProfile.images.map((img, index) => (
              <img 
                key={index} 
                src={img.imageUrl} 
                alt={`Thumbnail ${index + 1}`} 
                className={`thumbnail ${index === currentImageIndex ? "active" : ""}`} 
                onClick={() => setCurrentImageIndex(index)}
              />
            ))}
          </div>
          <div className="profile-detail-color">
            <h2>{selectedProfile?.fullName}, {age}</h2>
            <h6>{selectedProfile?.bio}</h6>
            <div className="custom3-heading">
              <div><FaVenusMars />&nbsp; Gender: {selectedProfile?.gender}</div>
              <div><FaHeart />&nbsp; Relationship: {selectedProfile?.relationshipType}</div>
              <div><FaMapMarkerAlt />&nbsp; Location: {selectedProfile?.location}</div>
              <div><FaSmokingBan />&nbsp; Smokes: {selectedProfile?.smoke ? "Yes" : "No"}</div>
              <div><FaBeer />&nbsp; Drinks: {selectedProfile?.alcohol}</div>
            </div>

            </div>
          </div>
      )}
      {isReportModalOpen && (
        <div className="report-modal">
          <h3>Report {currentProfile.fullName}</h3>
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
        <div className="report-notification">
          {reportMessage}
        </div>
      )}
    </div>
  );
};

const Giriş = () => {
  const [likedProfiles, setLikedProfiles] = useState([]);

  return (
    <div className="page-wrapper">
      <Routes>
        <Route path="/" element={
          <>
            <Header likedProfiles={likedProfiles} />
            <LikesScreen likedProfiles={likedProfiles} setLikedProfiles={setLikedProfiles} />
            <Footer />
          </>
        } />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<AuthForm isRegister={true} />} />
        <Route path="/likes" element={<LikesPage />} />
        <Route 
          path="/matchScreen" 
          element={<MatchScreen likedProfiles={likedProfiles} />} 
        />
        <Route path="/chatScreen" element={<ChatScreen />} />
      </Routes>
    </div>
  );
};

export default Giriş;