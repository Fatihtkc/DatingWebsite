// src/signup.js
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FaCheckCircle, FaTimesCircle, FaExclamationCircle } from 'react-icons/fa';
import './css/style.css';
import api from "./api/axiosInstance";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';


const AuthForm = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [passwordConf, setPasswordConf] = useState('');
  const [errors, setErrors]     = useState({});
  const [serverError, setServerError] = useState('');
  const navigate = useNavigate();

  const validateForm = () => {
    let newErrors = {};
    const emailRegex    = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const usernameRegex = /^[a-zA-Z0-9_]+$/;
    const passwordRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&.])[A-Za-z\d@$!%*?&.]{8,}$/;

    if (!username.trim()) newErrors.username = "Username is required";
    else if (!usernameRegex.test(username))
      newErrors.username = "Username can only contain letters, numbers, and underscores";

    if (!email.trim()) newErrors.email = "Email is required";
    else if (!emailRegex.test(email))
      newErrors.email = "Invalid email format";

    if (!password) newErrors.password = "Password is required";
    else if (!passwordRegex.test(password))
      newErrors.password = "Password must be at least 8 characters, include an uppercase letter, a number, and a special character";

    if (!passwordConf) newErrors.passwordConf = "Please confirm your password";
    else if (passwordConf !== password)
      newErrors.passwordConf = "Passwords do not match";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };


  const handleSubmit = async (e) => {
    e.preventDefault();
    setServerError(''); // Server error'ı sıfırla
  
    // Formun geçerli olup olmadığını kontrol et
    if (!validateForm()) return;
  
    try {
  
      const response = await api.get('/check-user', {
        params: { email, username },
      });
  
      const user = {
        username,
        email,
        password,
      };
      navigate('/personality-test', { state: { user, fromSignup: true } });
  
      return { success: true };
    } catch (error) {
    
      if (error.response) {
        const message = error.response.data;
    
        if (message.includes("Email")) {
          toast.error("This email is already registered.");
          return;
        } else if (message.includes("Username")) {
          toast.error("This username is already taken.");
          return;
        } else {
          toast.error("Validation failed. Please check your inputs.");
          return;
        }
      }
    
      toast.error("Unexpected error occurred. Please try again.");
    }
    
  };
  

  const handleLogoClick = () => {
    navigate('/index');
  };

  const checkPasswordCriteria = (pwd) => ({
    length: pwd.length >= 8,
    uppercase: /[A-Z]/.test(pwd),
    number: /[0-9]/.test(pwd),
    specialChar: /[@$!%*?&.]/.test(pwd)
  });

  const passwordCriteria = checkPasswordCriteria(password);

  return (
    <div className="auth-page">
      <header>
        <h1 className="logo-text" onClick={handleLogoClick} style={{ cursor: 'pointer' }}>
          SoulM
        </h1>
      </header>

      <div className="auth-content">
        <div className="auth-form-container">
          <form onSubmit={handleSubmit}>
            <h2 className="form-title">Register</h2>

            {serverError && (
              <p className="error">
                <FaExclamationCircle className="error-icon" /> {serverError}
              </p>
            )}

            <div>
              <label>Username</label>
              <input
                type="text"
                name="username"
                className="text-input"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
              {errors.username && (
                <p className="error">
                  <FaExclamationCircle className="error-icon" /> {errors.username}
                </p>
              )}
            </div>

            <div>
              <label>Email</label>
              <input
                type="email"
                name="email"
                className="text-input"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
              {errors.email && (
                <p className="error">
                  <FaExclamationCircle className="error-icon" /> {errors.email}
                </p>
              )}
            </div>

            <div>
              <label>Password</label>
              <input
                type="password"
                name="password"
                className="text-input"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
              <div className="password-requirements">
                <p>
                  {passwordCriteria.length
                    ? <FaCheckCircle className="valid-icon" />
                    : <FaTimesCircle className="invalid-icon" />
                  }
                  At least 8 characters
                </p>
                <p>
                  {passwordCriteria.uppercase
                    ? <FaCheckCircle className="valid-icon" />
                    : <FaTimesCircle className="invalid-icon" />
                  }
                  One uppercase letter
                </p>
                <p>
                  {passwordCriteria.number
                    ? <FaCheckCircle className="valid-icon" />
                    : <FaTimesCircle className="invalid-icon" />
                  }
                  One number
                </p>
                <p>
                  {passwordCriteria.specialChar
                    ? <FaCheckCircle className="valid-icon" />
                    : <FaTimesCircle className="invalid-icon" />
                  }
                  One special character (., @, #, !, etc.)
                </p>
              </div>
              {errors.password && (
                <p className="error">
                  <FaExclamationCircle className="error-icon" /> {errors.password}
                </p>
              )}
            </div>

            <div>
              <label>Password Confirmation</label>
              <input
                type="password"
                name="passwordConf"
                className="text-input"
                value={passwordConf}
                onChange={(e) => setPasswordConf(e.target.value)}
              />
              {errors.passwordConf && (
                <p className="error">
                  <FaExclamationCircle className="error-icon" /> {errors.passwordConf}
                </p>
              )}
            </div>

            <div>
              <button type="submit" className="btn btn-big">
                Register
              </button>
            </div>

            <p>
              Already have an account?{' '}
              <Link to="/login" className="register-link">
                Sign In
              </Link>
            </p>
          </form>
        </div>
      </div>

      <footer className="footer-summary">
        &copy; SoulM.com | Designed by Group 19
      </footer>

      <ToastContainer position="top-right" autoClose={3000} />

    </div>
  );
};

export default AuthForm;
