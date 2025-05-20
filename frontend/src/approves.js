import React, { useState, useEffect } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import "./css/approves.css";
import { FaUser } from "react-icons/fa";
import api from "./api/axiosInstance";

// Profil Kartı Component
const ProfilKarti = ({ id, images, fullName, email, phone, setProfiles }) => {
  const navigate = useNavigate();
  const [showApproveModal, setShowApproveModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [infoMessage, setInfoMessage] = useState("");

  const handleCardClick = () => {
    navigate(`/profileUser/${id}`);
  };

  const handleApprove = async (e) => {
    e.stopPropagation();
    setShowApproveModal(false);
    try {
      await api.put(`/moderation/approve-user/${id}`, {}, {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('token')}`
        }
      });
      setInfoMessage("User Approved");
      setProfiles(prev => prev.filter(profile => profile.id !== id));
    } catch (err) {
      console.error('Error approving profile:', err);
      alert('Approve failed.');
    }
  };

  const handleDelete = async (e) => {
    e.stopPropagation();
    setShowDeleteModal(false);
    try {
      await api.delete(`/users/${id}`, {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('token')}`
        }
      });
      setInfoMessage("User Deleted");
      setProfiles(prev => prev.filter(profile => profile.id !== id));
    } catch (err) {
      console.error('Error deleting profile:', err);
      alert('Delete failed.');
    }
  };

  return (
    <div className="profil-karti" onClick={handleCardClick}>
      <img src={images?.[0]?.imageUrl} alt={fullName} className="profil-resim" />
      <div className="profil-bilgi">
        <h3>{fullName}</h3>
        <p>Email: {email}</p>
        <p>Phone No: {phone}</p>
      </div>
      <div className="profil-buttons">
        <button className="approve-button" onClick={(e) => { e.stopPropagation(); setShowApproveModal(true); }}>
          Approve Account
        </button>
        <button className="delete-button" onClick={(e) => { e.stopPropagation(); setShowDeleteModal(true); }}>
          Delete Account
        </button>
      </div>

      {/* Approve Modal */}
      {showApproveModal && (
        <div className="modal-overlay" onClick={() => setShowApproveModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>Approve Account</h3>
            <p>Are you sure you want to approve this account?</p>
            <div className="modal-buttons">
              <button onClick={handleApprove} className="modal-confirm">Yes</button>
              <button onClick={() => setShowApproveModal(false)} className="modal-cancel">Cancel</button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Modal */}
      {showDeleteModal && (
        <div className="modal-overlay" onClick={() => setShowDeleteModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>Delete Account</h3>
            <p>Are you sure you want to delete this account?</p>
            <div className="modal-buttons">
              <button onClick={handleDelete} className="modal-confirm">Yes</button>
              <button onClick={() => setShowDeleteModal(false)} className="modal-cancel">Cancel</button>
            </div>
          </div>
        </div>
      )}

      {/* Info Popup */}
      {infoMessage && (
        <div className="info-popup">
          {infoMessage}
        </div>
      )}
    </div>
  );
};

// Ana Onay Sayfası
const Approves = () => {
  const [profiles, setProfiles] = useState([]);
  const location = useLocation();
  const navigate = useNavigate();

  const handleLogout = () => {
    sessionStorage.clear();
    navigate("/login");
  };

  useEffect(() => {
    api.get('/moderation/pending-users', {
      headers: {
        Authorization: `Bearer ${sessionStorage.getItem('token')}`
      }
    })
      .then(response => {
        setProfiles(response.data);
      })
      .catch(err => {
        console.error('Error fetching profiles:', err);
        alert('Error fetching profiles. Please try again.');
      });
  }, []);

  const handleLogoClick = () => {
    navigate("/index");
  };

  const handleViewProfile = () => {
    navigate('/profilePageForMod');
  };

  return (
    <>
      <header className="header">
        <div className="header-left">
          <h1 className="logo-text" onClick={handleLogoClick} style={{ cursor: "pointer" }}>SoulM</h1>
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
              <a href="#"><FaUser /> Profile</a>
              <ul className="dropdown">
                <li><a href="#" onClick={handleLogout}>Logout</a></li>
                <li><a href="/profilePageForMod" onClick={handleViewProfile}>View Profile</a></li>
              </ul>
            </li>
          </ul>
        </div>
      </header>

      <div className="profil-listesi">
        {profiles.map(profile => (
          <ProfilKarti
            key={profile.id}
            {...profile}
            setProfiles={setProfiles}
          />
        ))}
      </div>

      <div className="footer-summary2">
        &copy; SoulM.com | Designed by Group 19
      </div>
    </>
  );
};

export default Approves;
