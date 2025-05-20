import React, { useState, useEffect, useRef } from "react";
import { Link, useNavigate, useLocation, useParams } from "react-router-dom";
import "./css/complaintDetail.css";
import { FaUser } from "react-icons/fa";
import api from "./api/axiosInstance";

const ComplaintDetail = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [complaint, setComplaint] = useState(null);
  const [loading, setLoading] = useState(true);
  const [lightboxOpen, setLightboxOpen] = useState(false);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  const location = useLocation();

  const handleLogout = () => {
    sessionStorage.clear();
      navigate("/login"); // Çıkış yapınca login sayfasına yönlendir
  };

  useEffect(() => {
    const fetchComplaint = async () => {
      try {
        const response = await api.get(`/moderation/complaints/${id}`, {
          headers: {
            Authorization: `Bearer ${sessionStorage.getItem('token')}`
          }
        });
        setComplaint(response.data);
      } catch (error) {
        console.error("Error fetching profile:", error);
        alert("Şikayet alınamadı.");
      } finally {
        setLoading(false);
      }
    };

    fetchComplaint();
  }, [id]);

  if (loading) return <div>Loading...</div>;

  const openLightbox = (index) => {
    setCurrentImageIndex(index);
    setLightboxOpen(true);
  };

  const closeLightbox = () => {
    setLightboxOpen(false);
  };

  const showPrevImage = (e) => {
    e.stopPropagation();
    setCurrentImageIndex(
      (prev) => (prev - 1 + complaint.images.length) % complaint.images.length
    );
  };

  const showNextImage = (e) => {
    e.stopPropagation();
    setCurrentImageIndex((prev) => (prev + 1) % complaint.images.length);
  };

  const handleDeleteComplaint = async() => {
    try {
      await api.delete(`/moderation/complaints/${id}`, {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('token')}`
        }
      });
      console.log("Delete Profile:", id);
  
      // Silinen şikayeti listeden çıkar
    } catch (err) {
      console.error('Error deleting complaint:', err);
      alert('Delete failed.');
    }
    navigate("/complaints");
  };

  //DEĞİŞECEKKKKKKKKKKKK
  const handleBanUser = (e) => {
    e.stopPropagation();
    navigate("/complaints");
  };

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
    <div className="complaint-detail-container">
      {/* Sol Bölüm: Bilgiler */}
      <div className="complaint-detail-sidebar">
        <div
          className="complaint-owner-box clickable"
          onClick={() => navigate(`/profileUser/${complaint.complainant.id}`)}
        >
           <h3>Complainant</h3>
          <div className="owner-info">
            <img
              src={complaint.complainant.images[0].imageUrl}
              alt={complaint.complainant.fullName}
              className="profile-pic-square"
            />
            <p>
            <span className="user-info-label">Name Surname:</span>{" "}
              <span className="user-info-value">{complaint.complainant.fullName}</span>
            </p>
            <p>
              <span className="user-info-label">Email:</span>{" "}
              <span className="user-info-value">{complaint.complainant.email}</span>
            </p>
            <p>
            <span className="user-info-label">Phone:</span>{" "}
              <span className="user-info-value">{complaint.complainant.phone}</span>
            </p>
          </div>
        </div>
        <div
          className="complaint-target-box clickable"
          onClick={() => navigate(`/profileUser/${complaint.complained.id}`)}
        >
           <h3>Person complained about</h3>
          <div className="target-info">
            <img
              src={complaint.complained.images[0].imageUrl}
              alt={complaint.complained.fullName}
              className="profile-pic-square"
            />
            <p>
            <span className="user-info-label">Name Surname:</span>{" "}
              <span className="user-info-value">{complaint.complained.fullName}</span>
            </p>
            <p>
              <span className="user-info-label">Email:</span>{" "}
              <span className="user-info-value">{complaint.complained.email}</span>
            </p>
            <p>
            <span className="user-info-label">Phone:</span>{" "}
              <span className="user-info-value">{complaint.complained.phone}</span>
            </p>
            <button className="ban-user-button" onClick={handleBanUser}>
            Ban User
            </button>
          </div>
        </div>
      </div>

      {/* Sağ Bölüm: Şikayet Detayları */}
      <div className="complaint-detail-main">
        <div className="complaint-content-box">
          <p className="complaint-reason-text">{complaint.reason}</p>
          <div className="complaint-images">
            {complaint.images.map((img, index) => (
              <img
                key={index}
                src={img.imageUrl}
                alt={`Complaint ${index}`}
                className="complaint-image"
                onClick={() => openLightbox(index)}
              />
            ))}
          </div>
          <button
            className="delete-complaint-button"
            onClick={handleDeleteComplaint}
          >
            Delete Complaint
          </button>
        </div>
      </div>

      {/* Lightbox Overlay */}
      {lightboxOpen && (
        <div className="lightbox-overlay" onClick={closeLightbox}>
          <div className="lightbox-content" onClick={(e) => e.stopPropagation()}>
            <button className="lightbox-close" onClick={closeLightbox}>
              &times;
            </button>
            <img
              src={complaint.images[currentImageIndex].imageUrl}
              alt={`Complaint ${currentImageIndex}`}
              className="lightbox-image"
            />
            <button className="lightbox-prev" onClick={showPrevImage}>
              &#8249;
            </button>
            <button className="lightbox-next" onClick={showNextImage}>
              &#8250;
            </button>
          </div>
        </div>
      )}
      <div className="footer-summary2">
        &copy; SoulM.com | Designed by Group 19
     </div>
    </div>
    </>
  );
};

export default ComplaintDetail;