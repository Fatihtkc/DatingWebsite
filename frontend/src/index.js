import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './css/style.css';
import { useNavigate } from 'react-router-dom'; 
import { FaPhone, FaEnvelope, FaLinkedin, FaInstagram, FaDiscord } from 'react-icons/fa';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';
import { useState } from 'react';
import Index from './index.js';
import Giriş from './giriş.js';
import SignUp from './signup.js';
import Login from './login.js';
import PersonalityTest from './PersonalityTest'; 
import PersonalInformation from './PersonalInformation';
import ProfilePage from './ProfilePage';
import LikesPage from './likes';
import ChatScreen from "./chatScreen";
import MatchScreen from "./matchScreen";
import ForgotPass from './forgottenPassword';
import EnterCode from './entercode';
import ChangePassword from './changepassword';
import Complaints from "./complaints";
import Approves from "./approves";
import ComplaintDetail from "./complaintDetail";
import ProfilePageForMod from "./profilePageForMod";
import ProfilePageForManager from "./profilePageForManager";
import ProfileDetails from "./profileDetails";
import ProfileDetails2 from "./profileDetails2";
import Manager from "./Manager";
import ProtectedRoute from "./protectedRoute";
import { Navigate } from "react-router-dom";
import 'react-toastify/dist/ReactToastify.css';




function MainPage() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const navigate = useNavigate();

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  const handleLogout = () => {
    sessionStorage.clear();
    navigate('/login'); 
  };

  const handleLogoClick = () => {
    navigate("/index"); 
  };

  const handleSignIn = () => {
      sessionStorage.clear();
    navigate('/login'); 
  };

  const handleButtonClick = () => {
    console.log("Button clicked!"); 
    sessionStorage.clear();
    navigate('/signup');
  };

  const handleSubmitClick = () => {
    console.log('Submitting..');
  }; 

  return (
    <>
    <header>
      <div className="logo">
        <h1 className="logo-text"  onClick={handleLogoClick} style={{ cursor: "pointer" }}>SoulM</h1>
      </div>
      <button className="signin-button" onClick={handleSignIn}>
            Or Sign In
          </button>
    </header>
    <div className="main-page">
      <div className="main-background-image">
        <div className="main-content">
          <h1>Welcome to SoulM!</h1>
          <h5 className="start-text">One soul, two hearts, endless possibilities!</h5>
          <button className="start-button" onClick={handleButtonClick}>
            Let's Start!
          </button>
        </div>
      </div>
    </div>
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
                <a href="#"><FaDiscord /></a>
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
        </>
  );
}

export default MainPage;

const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
  <Router>
    <Routes>
      <Route path="/" element={
                  <>
                    <MainPage/>
                  </>
                } />
      <Route path="/" element={<Index />} />
      <Route path="/giriş" element={<Giriş />} />
      <Route path="/login" element={<Login />} />
      <Route path="/signup" element={<SignUp />} />
      <Route path="/personality-test" element={<PersonalityTest />} />
      <Route path="/PersonalInformation" element={<PersonalInformation />} />
      <Route path="/forgottenPassword" element={<ForgotPass />} />
      <Route path="/entercode" element={<EnterCode />} />
      <Route path="/changepassword/:token" element={<ChangePassword />} />

      <Route element={<ProtectedRoute allowedRoles={['user']} />}>
        <Route path="/ProfilePage" element={<ProfilePage />} />
        <Route path="/matchScreen" element={<MatchScreen />} />
        <Route path="/chatScreen" element={<ChatScreen />} />
        <Route path="/likes" element={<LikesPage />} />
        <Route path="/profile/:id" element={<ProfileDetails2 />} />
      </Route>

      <Route element={<ProtectedRoute allowedRoles={['manager']} />}>
        <Route path="/Manager" element={<Manager />} />
        <Route path="/profilePageForManager" element={<ProfilePageForManager />} />
      </Route>

      <Route element={<ProtectedRoute allowedRoles={['moderator']} />}>
        <Route path="/approves" element={<Approves />} />
        <Route path="/profilePageForMod" element={<ProfilePageForMod />} />
        <Route path="/complaints" element={<Complaints />} />
        <Route path="/complaints/:id" element={<ComplaintDetail />} />
        <Route path="/profileUser/:id" element={<ProfileDetails />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  </Router>
);
