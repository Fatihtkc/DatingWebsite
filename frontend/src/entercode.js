import React, { useState } from "react";
import { useNavigate, Navigate, useLocation } from "react-router-dom";
import './css/style.css';
import api from "./api/axiosInstance";

const EnterCode = () => {
  const [showAlert, setShowAlert] = useState(false); 
  const navigate = useNavigate();
  const [otp, setOtp] = useState("");
  const location = useLocation(); // Use this hook to get location
  const email = location.state?.email;

  if (!location.state || !location.state.fromPersonalInfo) {
    return <Navigate to="/index" />;
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    api.post('/api/token/verify', null, {
      params: {
        email: email,
        code: otp
      }
    })
    .then(res => {
      alert("OTP verified successfully!");
      navigate("/ProfilePage");
    })
    .catch(err => {
      alert("OTP invalid or expired.");
    });
  };

  const handleOkClick = () => {
    setShowAlert(false);
  };

  const handleLogoClick = () => {
    navigate("/index");
  };

  return (
    <>
<header>
        <div className="logo">
          <h1 className="logo-text" onClick={handleLogoClick} style={{ cursor: "pointer" }}>SoulM</h1>
        </div>
      </header>
    <div className="enter-code-container">

      <div className="enter-code-box">
        <h2 className="enter-code-title">Enter Verification Code</h2>
        <p className="enter-code-text">
          Please enter the 6-digit verification code we sent to your email.
        </p>

        <form className="enter-code-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label" htmlFor="code">Verification Code</label>
            <input
              className="form-input"
              type="text"
              id="code"
              autocomplete="off"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              maxLength={6}
              required
            />
          </div>

          <button type="submit" className="primary-button">
            Verify Code
          </button>
        </form>
      </div>

      {/* Uyarı Kutusu */}
      {showAlert && (
        <div className="alert-box">
          <p>Verification successful! Redirecting to password change screen...</p>
          <button className="ok-button" onClick={handleOkClick}>OK</button>
        </div>
      )}
    </div>
    <div className="footer-summary2">
        &copy; SoulM.com | Designed by Group 19
     </div>
    </>
  );
};

export default EnterCode;