import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Navigate } from "react-router-dom";
import { useLocation } from 'react-router-dom';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import api from "./api/axiosInstance";
import './css/style.css';

const ForgottenPassword = () => {
  const [email, setEmail] = useState("");
  const [step, setStep] = useState(1); // Tracks current step in the process
  const [showAlert, setShowAlert] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  if (!location.state || !location.state.fromLogin) {
    return <Navigate to="/index" />;
  }

  const handleEmailSubmit = async (e) => {
    e.preventDefault();
    const emailFromForm = e.target.email.value;

    try {
      const response = await api.post("/api/token/forgot-password", {
        email: emailFromForm
      });

      // Change to step 2 and show the email sent alert
      setStep(2);
      setShowAlert(true);

      // Show success message to the user
      toast.success("A reset code has been sent to your email.");

    } catch (err) {
      console.error("Full error response:", err.response);
      const message = err.response?.data?.message || "";
      if (message.includes("User not found")) {
        toast.error("No user found with this email address.");
      } else {
        toast.error("An error occurred while sending the reset email.");
      }
    }
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

      <div className="forgot-password-container">
        <div className="forgot-password-box">
          <h2 className="forgot-password-title">Request Password Reset</h2>
          {step === 1 ? (
            <>
              <p className="forgot-password-text">
                Forgot your password? Please enter your email address and click on
                "Request Password" button. We will send you a link to reset your password.
              </p>

              <form className="forgot-password-form" onSubmit={handleEmailSubmit}>
                <div className="form-group">
                  <label className="form-label" htmlFor="email">Email</label>
                  <input
                    className="form-input"
                    type="email"
                    id="email"
                    value={email}
                    onChange={(e) => { setEmail(e.target.value); }}
                    required
                  />
                </div>
                <button type="submit" className="primary-button">
                  Request Password
                </button>
              </form>
            </>
          ) : (
            <>
              <p className="forgot-password-text">
                A reset link has been sent to your email. Please check your inbox to reset your password.
              </p>
            </>
          )}
        </div>

        {/* Alert Box for Email Sent */}
        {showAlert && step === 1 && (
          <div className="alert-box">
            <p>A reset code has been sent to your email address.</p>
            <button className="ok-button" onClick={handleOkClick}>OK</button>
          </div>
        )}
      </div>

      <div className="footer-summary2">
        &copy; SoulM.com | Designed by Group 19
      </div>
      <ToastContainer position="top-center" autoClose={3000} />
    </>
  );
};

export default ForgottenPassword;
