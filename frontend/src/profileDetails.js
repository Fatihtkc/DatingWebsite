import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { 
  FaUser, 
  FaInfoCircle, 
  FaAlignLeft, 
  FaVenusMars, 
  FaHeart, 
  FaMapMarkerAlt, 
  FaSmokingBan, 
  FaBeer, 
  FaCheck,
  FaArrowLeft
} from "react-icons/fa";
import "./css/profileDetails.css";
import api from "./api/axiosInstance";


const ProfileDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [showApproveModal, setShowApproveModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [infoMessage, setInfoMessage] = useState("");
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await api.get(`/users/${id}`, {
          headers: {
            Authorization: `Bearer ${sessionStorage.getItem('token')}`
          }
        });
        setProfile(response.data);
      } catch (error) {
        console.error("Error fetching profile:", error);
        alert("Kullanıcı profili alınamadı.");
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [id]);

  if (loading) return <div>Loading...</div>;
  if (!profile) return <div>Profile not found.</div>;

  const handlePrevImage = () => {
    if (!profile.images || profile.images.length === 0) return;
    setCurrentImageIndex((prevIndex) =>
      prevIndex === 0 ? profile.images.length - 1 : prevIndex - 1
    );
  };

  const handleNextImage = () => {
    if (!profile.images || profile.images.length === 0) return;
    setCurrentImageIndex((prevIndex) =>
    prevIndex === profile.images.length - 1 ? 0 : prevIndex + 1
    );
  };

  const handleLogoClick = () => {
    navigate("/index");
  };

  const confirmApprove = async () => {
    setShowApproveModal(false);
    setInfoMessage("User Approved");
    setTimeout(() => setInfoMessage(""), 3000);
    try {
      await api.put(`/moderation/approve-user/${id}`, {}, {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('token')}`
        }
      });
      console.log("Approve Profile:", id);
  
      setProfile(prevProfile => ({
        ...prevProfile,
        approved: true
      }));
  
    } catch (err) {
      console.error('Error approving profile:', err);
      alert('Approve failed.');
    }
  };
  

  const confirmDelete = async() => {
    setShowDeleteModal(false);
    setInfoMessage("User Deleted");
    setTimeout(() => setInfoMessage(""), 3000);
    try {
      await api.delete(`/users/${id}`, {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('token')}`
        }
      });
      console.log("Delete Profile:", id);
  
      // Silinen kullanıcıyı listeden çıkar
    } catch (err) {
      console.error('Error deleting profile:', err);
      alert('Delete failed.');
    }
    navigate(-1);
  };

  return (
    <>
<header>
        <div className="logo">
          <h1 className="logo-text" onClick={handleLogoClick} style={{ cursor: "pointer" }}>SoulM</h1>
          <button className="back-button3" onClick={() => navigate(-1)}>
        <FaArrowLeft style={{ marginRight: "5px" }} />
      </button>
        </div>
      </header>
    <div className="profile2-details-page">
      <div className="profile2-details-container">
        <div className="details-card">
          {/* Sol Kolon: Image Slider */}
          <div className="image2-container">
            <div className="image2-slider">
              <button className="arrow-button left" onClick={handlePrevImage}>◀</button>
              <img
                src={profile.images[currentImageIndex].imageUrl}
                alt={profile.fullName}
                className="profile2-detail-img"
              />
              <button className="arrow-button right" onClick={handleNextImage}>▶</button>
            </div>
          </div>
          {/* Orta Kolon: Text Details */}
          <div className="text-container">
            <div className="text-details">
              <div className="detail-row">
                <span className="detail-heading"><FaUser /> Name:</span>
                <span className="detail-value">{profile.fullName}, {profile.age}</span>
              </div>
              <div className="detail-row">
                <span className="detail-heading"><FaInfoCircle /> Short Bio:</span>
                <span className="detail-value">{profile.shorterbio}</span>
              </div>
              <div className="detail-row">
                <span className="detail-heading"><FaAlignLeft /> Bio:</span>
                <span className="detail-value">{profile.bio}</span>
              </div>
              <div className="detail-row">
                <span className="detail-heading"><FaVenusMars /> Gender:</span>
                <span className="detail-value">{profile.gender}</span>
              </div>
              <div className="detail-row">
                <span className="detail-heading"><FaHeart /> Relationship:</span>
                <span className="detail-value">{profile.relationshipType}</span>
              </div>
              <div className="detail-row">
                <span className="detail-heading"><FaMapMarkerAlt /> Location:</span>
                <span className="detail-value">{profile.location}</span>
              </div>
              <div className="detail-row">
                <span className="detail-heading"><FaSmokingBan /> Smokes:</span>
                <span className="detail-value">{profile.smoke === "true" ? "Yes" : "No"}</span>
              </div>
              <div className="detail-row">
                <span className="detail-heading"><FaBeer /> Drinks:</span>
                <span className="detail-value">{profile.alcohol}</span>
              </div>
            </div>
          </div>
        </div>
        {/* Sağ Kolon: Action Panel (Kartın dışında, sağda) */}
        <div className="action-panel">
          <div className="action-box approval-status">
            {profile.approved ? (
              <>
                Approved Account <FaCheck style={{ color: "green", marginLeft: "8px" }} />
              </>
            ) : (
              "Not Approved Yet"
            )}
          </div>
          <button 
            className="action-box approve-button" 
            onClick={() => setShowApproveModal(true)} 
            disabled={profile.approved}
            style={{ opacity: profile.approved ? 0.5 : 1 }}
          >
            Approve Account
          </button>
          <button className="action-box delete-button" onClick={() => setShowDeleteModal(true)}>
            Delete Account
          </button>
        </div>
      </div>

      {/* Approve Modal */}
      {showApproveModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Approve Account</h3>
            <p>Are you sure you want to approve this account?</p>
            <div className="modal-buttons">
              <button onClick={confirmApprove} className="modal-confirm">Yes</button>
              <button onClick={() => setShowApproveModal(false)} className="modal-cancel">Cancel</button>
            </div>
          </div>
        </div>
      )}
      {/* Delete Modal */}
      {showDeleteModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Delete Account</h3>
            <p>Are you sure you want to delete this account?</p>
            <div className="modal-buttons">
              <button onClick={confirmDelete} className="modal-confirm">Yes</button>
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
    <div className="footer-summary2">
        &copy; SoulM.com | Designed by Group 19
     </div>
    </>
  );
};

export default ProfileDetails;