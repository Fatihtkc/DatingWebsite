import React, { useState } from 'react';
import './css/style.css';
import { useNavigate } from 'react-router-dom';
import api from "./api/axiosInstance";


const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);

  const validateEmail = (email) => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email); // Basit e-posta doğrulaması
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!validateEmail(email)) {
      setErrorMessage('Please enter a valid email address.');
      return;
    }

    try {
      const response = await api.post("/login", { email, password });

      const { token, role , username, id} = response.data;

      // Token'ı ve rol bilgisini sessionStorage'a kaydet
      sessionStorage.setItem('token', token);
      sessionStorage.setItem('role', role);
      sessionStorage.setItem('username', username);
      sessionStorage.setItem('id', id);
      console.log("Email:", email); // Burada email değeri konsola yazdırılacak
      sessionStorage.setItem("email",email)

      // Kullanıcı rolüne göre yönlendirme
      if (role === 'manager') {
        navigate('/Manager');
      } else if (role === 'moderator') {
        navigate('/complaints');
      } else if (role === 'user') {
        navigate('/giriş');
      } else {
        setErrorMessage('Unknown user role.');
      }

    } catch (error) {
      setErrorMessage('Invalid Email or Password.');
      console.error('Login error:', error);
    }
  };

  const handleSignup = () => {
    navigate('/signup');
  };

  const handleLogoClick = () => {
    navigate("/index");
  };

  const handleForgotPassword = () => {
    navigate('/forgottenPassword', { state: { fromLogin:true } });
  };

  return (
    <div>
      <header>
        <div className="logo">
          <h1 
            className="logo-text" 
            onClick={handleLogoClick} 
            style={{ cursor: "pointer" }}
          >
            <span>Soul</span>M
          </h1>
        </div>
      </header>

      <div className="auth-content">
        <form onSubmit={handleSubmit} className="login-form">
          <h2 className="log-title">Login</h2>

          {errorMessage && <p className="error">{errorMessage}</p>}

          <div>
            <label>Email</label>
            <input
                type="email"
                name="email"
                className="text-input"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />
          </div>
          <label>Password</label>
          <input
              type={showPassword ? 'text' : 'password'}
              name="password"
              className="text-input"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
          />
          <div className="password-options-row">
            <div className="show-password-wrapper">
              <input
                  type="checkbox"
                  id="showPassword"
                  checked={showPassword}
                  onChange={() => setShowPassword(!showPassword)}
              />
              <label htmlFor="showPassword">Show Password</label>
            </div>

            <button
                type="button"
                className="forgot-password-btn"
                onClick={handleForgotPassword}
            >
              Forgotten Password?
            </button>
      </div>
      <div>
        <button type="submit" className="btn btn-big">Login</button>
      </div>
      <p>
        Or <a href="#" onClick={handleSignup}>Sign Up</a>
      </p>
    </form>
</div>
  <div className="footer-summary">
    &copy; SoulM.com | Designed by Group 19
  </div>
</div>
)
  ;
};

export default Login;