import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Navigate } from "react-router-dom";
import { useLocation } from 'react-router-dom';
import './css/style.css';
import api from "./api/axiosInstance";


const steps = ["Basic Info", "Preferences", "Lifestyle", "Interests"];

const requiredFields = {
  0: ["fullName", "birthDate", "gender", "location"],
  1: ["relationshipType", "agePreference", "distancePreference"],
  2: ["height", "weight", "smoke", "alcohol"],
  3: ["hobbies", "favoriteMusic", "weekendPlans"],
};


const PersonalInformation = () => {

  const location = useLocation();
  const user = location.state?.user;

  const [step, setStep] = useState(0);
  const [formData, setFormData] = useState({
    username:user.username,
    email:user.email,
    password:user.password,
    fullName: "",
    birthDate: "",
    gender: "",
    location: "",
    relationshipType: "",
    age_preference: "",
    distance_preference: "",
    height: "",
    weight: "",
    body_type: "",
    smoke: "",
    alcohol: "",
    diet: "",
    hobbies: "",
    favoriteMusic: "",
    weekendPlans: "",
  });
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const navigate = useNavigate();

  if (!location.state || !location.state.fromTest) {
    return <Navigate to="/index" />;
  }

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "birthDate") {
      const today = new Date();
      const birthDate = new Date(value);
      const age = today.getFullYear() - birthDate.getFullYear();

      if (
        age < 18 ||
        (age === 18 && today.getMonth() < birthDate.getMonth()) ||
        (age === 18 &&
          today.getMonth() === birthDate.getMonth() &&
          today.getDate() < birthDate.getDate())
      ) {
        setErrors({ ...errors, birthDate: "You must be at least 18 years old." });
      } else {
        setErrors({ ...errors, birthDate: "" });
      }
    }

    setFormData({ ...formData, [name]: value });
  };

  const prevStep = () => setStep((prev) => Math.max(prev - 1, 0));

  const nextStep = () => {
    if (!validateStep() || (step === 0 && errors.birthDate)) return;
    setStep((prev) => Math.min(prev + 1, steps.length - 1));
  };
  
  const handleSubmit = async () => {
    if (!validateStep()) return;
  
    try {
      setServerError('');
      const response = await api.post("/signup", formData);
      const { token, role } = response.data;
      sessionStorage.setItem("token", token);
      sessionStorage.setItem("role", role);
      sessionStorage.setItem("username", user.username);
      sessionStorage.setItem("email", user.email);
      await api.post('/api/token/send-verification', null, {
        params: { email: user.email },
      });
      navigate('/entercode', { state: { email: user.email , fromPersonalInfo : true} });

    } catch (error) {
      console.error("Error saving profile:", error);
      const msg = error.response?.data?.message || "Profile submission failed.";
      setServerError(msg);
    }
  };
  

  const validateStep = () => {
    const fieldsToCheck = requiredFields[step];
    const newErrors = {};
  
    fieldsToCheck.forEach((field) => {
      if (!formData[field] || formData[field].trim() === "") {
        newErrors[field] = "This field is required.";
      }
    });

    if (step === 2) {
      if (isNaN(formData.height) || Number(formData.height) <= 0) {
        newErrors.height = "Please enter a valid positive number for height.";
      }
      if (isNaN(formData.weight) || Number(formData.weight) <= 0) {
        newErrors.weight = "Please enter a valid positive number for weight.";
      }
    }
    
  
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
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
      <div className="page-container2">
        <div className="form-container">
          <h1 className="form-title">Tell us more about yourself</h1>

          {/* 🔹 Progress Bar */}
          <div className="progress-bar-container">
            <div className="progress-bar" style={{ width: `${((step + 1) / steps.length) * 100}%` }}></div>
          </div>

          {/* 🔹 Form İçeriği */}
          {step === 0 && (
            <>
              <div className="form-group">
                <label className="form-label">Full Name</label>
                <input className="form-input" type="text" name="fullName" value={formData.fullName} onChange={handleChange} />
                {errors.fullName && <p className="error-message">{errors.fullName}</p>}
              </div>

              <div className="form-group">
                <label className="form-label">birthDate</label>
                <input className="form-input" type="date" name="birthDate" value={formData.birthDate} onChange={handleChange} />
                {errors.birthDate && <p className="error-message">{errors.birthDate}</p>}
              </div>

              <div className="form-group">
                <label className="form-label">Gender</label>
                <select className="form-input" name="gender" value={formData.gender} onChange={handleChange}>
                  <option value="">Select</option>
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                  <option value="Non-binary">Non-binary</option>
                </select>
                {errors.gender && <p className="error-message">{errors.gender}</p>}
              </div>

              <div className="form-group">
                <label className="form-label">Location</label>
                <input className="form-input" type="text" name="location" value={formData.location} onChange={handleChange} />
                {errors.location && <p className="error-message">{errors.location}</p>}
              </div>
            </>
          )}

          {step === 1 && (
            <>
              <div className="form-group">
                <label className="form-label">Looking for</label>
                <select className="form-input" name="relationshipType" value={formData.relationshipType} onChange={handleChange}>
                <option value="">Select</option>
                  <option value="Serious Relationship">Serious Relationship</option>
                  <option value="Casual Dating">Casual Dating</option>
                  <option value="Friendship">Friendship</option>
                </select>
                {errors.lookingFor && <p className="error-message">{errors.lookingFor}</p>}
              </div>

              <div className="form-group">
                <label className="form-label">Age Preference</label>
                <input className="form-input" type="text" name="agePreference" value={formData.agePreference} onChange={handleChange} />
                {errors.agePreference && <p className="error-message">{errors.agePreference}</p>}
              </div>

              <div className="form-group">
                <label className="form-label">Distance Preference (km)</label>
                <input className="form-input" type="number" name="distancePreference" value={formData.distancePreference} onChange={handleChange} />
                {errors.distancePreference && <p className="error-message">{errors.distancePreference}</p>}
              </div>
            </>
          )}

          {step === 2 && (
            <>
              <div className="form-group">
                <label className="form-label">Height (cm)</label>
                <input
                  className="form-input"
                  type="number"
                  name="height"
                  value={formData.height}
                  onChange={handleChange}
                  min="0"
                />
                {errors.height && <p className="error-message">{errors.height}</p>}
              </div>

              <div className="form-group">
                <label className="form-label">Weight (kg)</label>
                <input
                  className="form-input"
                  type="number"
                  name="weight"
                  value={formData.weight}
                  onChange={handleChange}
                  min="0"
                />
                {errors.weight && <p className="error-message">{errors.weight}</p>}
              </div>
              <div className="form-group">
                <label className="form-label">Do you smoke?</label>
                <select className="form-input" name="smoke" value={formData.smoke} onChange={handleChange}>
                  <option value="">Select</option>
                  <option value="Yes">Yes</option>
                  <option value="No">No</option>
                </select>
                {errors.smoker && <p className="error-message">{errors.smoker}</p>}
              </div>

              <div className="form-group">
                <label className="form-label">Do you drink?</label>
                <select className="form-input" name="alcohol" value={formData.alcohol} onChange={handleChange}>
                  <option value="">Select</option>
                  <option value="Yes">Yes</option>
                  <option value="No">No</option>
                </select>
                {errors.drinking && <p className="error-message">{errors.drinking}</p>}
              </div>
            </>
          )}

          {step === 3 && (
            <>
              <div className="form-group">
                <label className="form-label">Hobbies</label>
                <input className="form-input" type="text" name="hobbies" value={formData.hobbies} onChange={handleChange} />
                {errors.hobbies && <p className="error-message">{errors.hobbies}</p>}
              </div>

              <div className="form-group">
                <label className="form-label">Favorite Music & Movies</label>
                <input className="form-input" type="text" name="favoriteMusic" value={formData.favoriteMusic} onChange={handleChange} />
                {errors.favoriteMusic && <p className="error-message">{errors.favoriteMusic}</p>}
              </div>

              <div className="form-group">
                <label className="form-label">Weekend Plans?</label>
                <input className="form-input" type="text" name="weekendPlans" value={formData.weekendPlans} onChange={handleChange} />
                {errors.weekendPlans && <p className="error-message">{errors.weekendPlans}</p>}
              </div>
            </>
          )}

          {/* 🔹 Navigasyon Butonları */}
          <div className="navigation-container">
            {step > 0 && <button className="nav-button" onClick={prevStep}>◀ Back</button>}
            {step < steps.length - 1 ? (
              <button className="navnext-button" onClick={nextStep}>Next ▶</button>
            ) : (
              <button className="primary-button" onClick={handleSubmit}>✔ Finish & Save</button>
            )}
          </div>
        </div>
      </div>
        <div className="footer-summary">
          &copy; SoulM.com | Designed by Group 19
        </div>
    </>
  );
};

export default PersonalInformation;