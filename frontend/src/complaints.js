import React, { useState, useEffect, useRef } from "react";
import { Link, useNavigate, useLocation  } from "react-router-dom";
import "./css/modstyle.css";
import "./css/complaints.css";
import { FaUser } from "react-icons/fa";
import api from "./api/axiosInstance";

const Complaints = () => {
  const navigate = useNavigate();
  const [complaints, setComplaints] = useState([]);
  const location = useLocation();

    const handleLogout = () => {
      sessionStorage.clear();
        navigate("/login");
        
    };

    const handleViewProfile = () => {
      navigate('/profilePageForMod'); 
    };

    useEffect(() => {
      api.get('/moderation/complaints', {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('token')}`
        }
      })
        .then(response => {
          setComplaints(response.data);
        })
        .catch(err => {
          console.error('Error fetching profiles:', err);
          alert('Error fetching profiles. Please try again.');
        });
    }, []);

    const handleLogoClick = () => {
      navigate("/index"); 
    };

  return (
    <>
    <header className="header">
      <div className="header-left">
      <h1 className="logo-text"  onClick={handleLogoClick} style={{ cursor: "pointer" }}>SoulM</h1>
      </div>
      <nav className="header-nav">
        <Link to="/complaints" className={`nav-item ${location.pathname === "/complaints" ? "active" : ""}`}>
          Complaints
        </Link>
        <Link to="/approves" className={`nav-item ${location.pathname === "/approves" ? "active" : ""}`}>
          Approves
        </Link>
      </nav>
      <div className="profile-container">
                      <ul className="profile">
                        <li className="profile-item">
                          <a href="#"> <FaUser /> Profile</a>
                          <ul className="dropdown">
                            <li><a href="#" onClick={handleLogout}>Logout</a></li>
                            <li><a href="/profilePageForMod" onClick={handleViewProfile}>View Profile</a></li>
                          </ul>
                        </li>
                      </ul>
                    </div>
    </header>
    <div className="complaints-container">
    {complaints.map((complaint) => (
      <div
      key={complaint.id}
        className="complaint-box"
        onClick={() => navigate(`/complaints/${complaint.id}`)}
      >
        <div
          className="user-info"
          onClick={(e) => {
            e.stopPropagation(); // Şikayet sayfasına gitmesini engelle
            navigate(`/profileUser/${complaint.complainant.id}`);
          }}
        >
          <img
             src={complaint.complainant.images[0].imageUrl}
             alt={complaint.complainant.fullName}
            className="profile3-pic"
          />
          <span className="username">{complaint.complainant.fullName}</span>
        </div>
        <span className="complaint-text">➜</span>
        <div
          className="user-info"
          onClick={(e) => {
            e.stopPropagation(); // Şikayet sayfasına gitmesini engelle
            navigate(`/profileUser/${complaint.complained.id}`);
          }}
        >
          <img
            src={complaint.complained.images[0].imageUrl}
            alt={complaint.complained.fullName}
            className="profile3-pic"
          />
          <span className="username">{complaint.complained.fullName}</span>
        </div>
        <p className="complaint-reason">{complaint.reason}</p>
      </div>
    ))}
    <div className="footer-summary2">
        &copy; SoulM.com | Designed by Group 19
     </div>
  </div>
  </>
  );
};

export default Complaints;