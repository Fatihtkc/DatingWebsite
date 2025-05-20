import React, { useState, useEffect } from "react";
import { useNavigate, useParams, Navigate } from "react-router-dom";
import { FaExclamation } from "react-icons/fa";
import api from './api/axiosInstance';
import { ToastContainer, toast } from 'react-toastify';  // ToastContainer ve toast import
import 'react-toastify/dist/ReactToastify.css';  // CSS dosyasını import ediyoruz
import './css/style.css';

const ChangePassword = () => {
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errors, setErrors] = useState({});
  const [isTokenValid, setIsTokenValid] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const { token } = useParams();  // URL'den tokenType ve token'ı alıyoruz
  const [passwordChanged, setPasswordChanged] = useState(false);  // Password değişim durumu state'i
  const navigate = useNavigate();

  useEffect(() => {
    const validateToken = async () => {
      try {
        const response = await api.get(`/api/token/validate?token=${token}&type=PASSWORD_RESET`);
        if (response.status === 200) {
          setIsTokenValid(true);
        }
      } catch (error) {
        console.error("Invalid token:", error);
        setIsTokenValid(false);
      } finally {
        setIsLoading(false);  // Her durumda yükleme biter
      }
    };
  
    if (token) {
      validateToken();
    } else {
      setIsLoading(false); // Token yoksa da bitir
    }
  }, [token]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({});

    const uppercaseRegex = /[A-Z]/;
    const specialCharRegex = /[!@#$%^&*(),.?":{}|<>]/;

    if (!password) {
      setErrors({ password: "Password is required" });
      return;
    }

    if (password.length < 8) {
      setErrors({ password: "Password must be at least 8 characters" });
      return;
    }

    if (!uppercaseRegex.test(password)) {
      setErrors({ password: "Password must include at least one uppercase letter" });
      return;
    }

    if (!specialCharRegex.test(password)) {
      setErrors({ password: "Password must include at least one special character" });
      return;
    }

    if (password !== confirmPassword) {
      setErrors({ confirmPassword: "Passwords do not match!" });
      return;
    }

    try {
      const response = await api.post(`/api/token/reset-password?token=${token}&newPassword=${password}`);

      if (response.status === 200) {
        console.log("Password changed successfully");
        setPasswordChanged(true);  // Şifre başarılı bir şekilde değiştirildiğinde state'i güncelle
        toast.success("Password successfully changed!");  // Success toast mesajı göster

        // Yönlendirmeyi toast mesajı gösterildikten sonra yapıyoruz
        setTimeout(() => {
          navigate("/login", { state: { passwordChanged: true } });
        }, 3000);  // 3 saniye sonra login sayfasına yönlendir
      }
    } catch (error) {
      console.error("Error occurred:", error.response ? error.response.data : error.message);
      setErrors({ general: "An error occurred while changing your password." });
    }
  };

  if (isLoading) {
    return <p>Validating token...</p>;  // veya bir spinner koyabilirsin
  }

  if (!isTokenValid) {
    return <Navigate to="/index" />;
  }

  return (
    <>
      <header>
        <div className="logo">
          <h1 className="logo-text" onClick={() => navigate("/index")} style={{ cursor: "pointer" }}>SoulM</h1>
        </div>
      </header>
      <div className="change-password-container">
        <div className="change-password-box">
          <h2 className="change-password-title">Change Your Password</h2>
          <p className="change-password-text">
            Please enter your new password and confirm it.
          </p>

          <form className="change-password-form" onSubmit={handleSubmit}>
            {errors.general && <p className="error-message"><FaExclamation /> {errors.general}</p>}

            <div className="form-group">
              <label className="form-label" htmlFor="password">New Password</label>
              <input
                className="form-input"
                type="password"
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              {errors.password && <p className="error-message"><FaExclamation /> {errors.password}</p>}
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="confirmPassword">Confirm Password</label>
              <input
                className="form-input"
                type="password"
                id="confirmPassword"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />
              {errors.confirmPassword && <p className="error-message"><FaExclamation /> {errors.confirmPassword}</p>}
            </div>

            <button type="submit" className="primary-button">
              Change Password
            </button>
          </form>
        </div>
      </div>
      <div className="footer-summary2">
        &copy; SoulM.com | Designed by Group 19
      </div>
      {/* ToastContainer'ı burada ekliyoruz */}
      <ToastContainer position="top-center" autoClose={3000} />
    </>
  );
};

export default ChangePassword;
