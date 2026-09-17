import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import api from "./api/axiosInstance";
import "./css/style.css";


const ProfilePage = () => {

  const [originalData, setOriginalData] = useState(null);
    const [formData, setFormData] = useState({
        fullName: "",
        birthDate: "",
        gender: "",
        location: "",
        latitude: "",
        longitude: "",
        relationshipType: "",
        agePreference: "",
        distancePreference: "",
        height: "",
        weight: "",
        bodyType: "",
        smoke: "",
        alcohol: "",
        diet: "",
        shorterbio: "",
        profilePhoto: "",
    });
  const [notification, setNotification] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const [selectedFile, setSelectedFile] = useState(null);
  const email= sessionStorage.getItem("email");
  const [previewUrls, setPreviewUrls] = useState([null, null, null]);
  const [images, setImages] = useState([]);

  const handleGetLocation = () => {
    if (!navigator.geolocation) {
      setNotification("Tarayıcınız konum bilgisi sağlamıyor.");
      setTimeout(() => setNotification(""), 3000);
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        const locationString = `${longitude.toFixed(6)},${latitude.toFixed(6)}`;;
        setOriginalData(prev => ({
          ...prev,
          location: locationString,
          latitude: latitude.toFixed(6),
          longitude: longitude.toFixed(6)
        }));
        setFormData(prev => ({
          ...prev,
          location: locationString
        }));
        setNotification("Konum güncellendi.");
        setTimeout(() => setNotification(""), 2000);
      },
      (error) => {
        console.error(error);
        setNotification("Konum alınamadı.");
        setTimeout(() => setNotification(""), 3000);
      }
    );
  };

  // Sayfa açıldığında mevcut profili çek
  useEffect(() => {
    setLoading(true);
    if (!email) {
      return;
    }
    api.get(`/profile/${email}`)
      .then(({ data }) => {
        setOriginalData(data);
        console.log(data);
        sessionStorage.setItem('id', data.id);
          setFormData({
              fullName: data.fullName || "",
              birthDate: data.birthDate || "",
              gender: data.gender || "",
              location: data.location || "",
              longitude: data.longitude || "",
              latitude: data.latitude || "",
              relationshipType: data.relationshipType || "",
              agePreference: data.agePreference || "",
              distancePreference: data.distancePreference || "",
              height: data.height || "",
              weight: data.weight || "",
              bodyType: data.bodyType || "",
              smoke: data.smoke || "",
              shorterbio: data.shorterbio || "",
              alcohol: data.alcohol || "",
              diet: data.diet || "",
          });
      })
        .catch(err => {
            console.error(err);
            if (err.response && err.response.status === 500) {
                setNotification("Server error: Please try again later.");
            } else if (err.response && err.response.status === 400) {
                setNotification("Invalid data. Please check your form.");
            } else {
                setNotification("An unexpected error occurred.");
            }
            setTimeout(() => setNotification(""), 4000);
        })
      .finally(() => setLoading(false));
    fetchUserImages();
  }, [email]);

  useEffect(() => {
    const urls = images.map((image) => {
      if (!image) return "";
      if (image.file) return URL.createObjectURL(image.file); // blob'dan üret
      return image.imageUrl || ""; // normal URL varsa onu al
    });
    setPreviewUrls(urls);
  }, [images]);


  const fetchUserImages = async () => {
    const userId = sessionStorage.getItem('id');

    try {
        const response = await api.get(`/users/${userId}/images`, {
            headers: {
                'Authorization': `Bearer ${sessionStorage.getItem('authToken')}` // Gerekliyse auth token
            }
        });
        console.log(response.data);

        setImages(response.data);
    } catch (error) {
      console.error('Resimleri alırken hata oluştu:', error);
    }
};

  const handleChange = (e) => {
    const { name, value } = e.target;
  
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  
    setOriginalData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const validateForm = () => {
    const {
      fullName, birthDate, gender, location,
      relationshipType, agePreference, distancePreference,
      height, weight, bodyType, smoke,
      alcohol, profilePhoto,shorterbio
    } = formData;

    if (!fullName.trim()) return "Full Name cannot be empty";

    const birthDateObj = new Date(birthDate);
    const today = new Date();
    const age = today.getFullYear() - birthDateObj.getFullYear();
    if (isNaN(birthDateObj) || age < 18) return "You must be at least 18 years old";


    if (!location.trim()) return "Location cannot be empty";
    if (!/^\d{2}-\d{2}$/.test(agePreference)) return "Age Preference must be in 'min-max' format";
    if (isNaN(distancePreference) || distancePreference <= 0) return "Distance Preference must be a positive number";
    if (!(height)) return "Height must be in 'number cm' format";
    if (!(weight)) return "Weight must be in 'number kg' format";
    if (!bodyType) return "Body Type cannot be empty";


    return "";
  };

  const uploadImages = async () => {
    const userId = sessionStorage.getItem('id');
    const formData = new FormData();

    // images dizisini döngüye alıyoruz ve her resmin file'ını formData'ya ekliyoruz
    images.forEach((image, index) => {
      if (image?.file) {
        formData.append("images", image.file); // Birden fazla dosya gönderiyoruz
      }
    });

    try {
      const response = await api.post(`/users/${userId}/images`, formData, {
        headers: {
          'Authorization': `Bearer ${sessionStorage.getItem('authToken')}`,
          'Content-Type': 'multipart/form-data',
        },
      });

      console.log('Images uploaded successfully:', response.data);
    } catch (error) {
      console.error('Error uploading images:', error);
    }
  };

  const handlePhotoUpload = (e, index) => {
    const file = e.target.files[0];
    if (!file) return;

    const previewUrl = URL.createObjectURL(file);

    // PreviewUrls güncelle
    setPreviewUrls((prev) => {
      const updated = [...prev];
      while (updated.length <= index) updated.push("");
      updated[index] = previewUrl;
      return updated;
    });
    console.log(images);
    // Images dizisini güncelle
    setImages((prev) => {
      const updated = [...prev];
      while (updated.length <= index) updated.push({ file: null });
      updated[index] = { file };
      return updated;
    });
    console.log(images);

  };


  const handleSave = () => {
    const error = validateForm();
    if (error) {
      setNotification(error);
      setTimeout(() => setNotification(""), 3000);
      return;
    }

    const updatedData = {
      ...originalData,
      gender: formData.gender || "Male",
      relationshipType: formData.relationshipType || "Serious Relationship",
      smoke: formData.smoke || "Yes",
      alcohol: formData.alcohol || "Occasionally"
    };

    setLoading(true);
    api.put(`/profile/${updatedData.id}`, updatedData)
        .then(() => {
        setNotification("Profil başarıyla güncellendi!");
        setTimeout(() => {
          setNotification("");
          navigate("/giriş");
        }, 1500);
      })
      .catch(err => {
        console.error(err);
        setNotification("Profil kaydedilirken bir hata oluştu.");
        setTimeout(() => setNotification(""), 3000);
      })
      .finally(() => setLoading(false));
    
    uploadImages();
  };


  const handleLogoClick = () => {
    navigate("/index");
  };

  const handleGoHome = () => {
    navigate("/giriş");
  };
  

  if (loading) return <div>Yükleniyor...</div>;

    return (
        <>
    <header>
        <div className="logo">
          <h1 className="logo-text" onClick={handleLogoClick} style={{ cursor: "pointer" }}>SoulM</h1>
        </div>
      </header>
            <div className="page-container2">
                <div className="profile-container2">
                    <h1 className="profile-title2">Your Account</h1>
                    {notification && <div className="notification">{notification}</div>}
                    <div className="photo-row">
                                    {/* Fotoğraf 1 */}
                  <div className="profile5-avatar-wrapper">
                    <img
                      key={previewUrls[0]}
                      src={previewUrls[0] || "/images/default-avatar.png"}
                      alt="Profile 1"
                      className="profile5-avatar"
                    />
                    <label htmlFor="photoUpload1" className="profile5-photo-button">
                      Change Photo 1
                    </label>
                    <input
                      type="file"
                      id="photoUpload1"
                      accept="image/*"
                      onChange={(e) => handlePhotoUpload(e, 0)}
                      style={{ display: "none" }}
                    />
                  </div>

                  {/* Fotoğraf 2 */}
                  <div className="profile5-avatar-wrapper">
                    <img
                      src={previewUrls[1]}
                      alt="Profile 2"
                      className="profile5-avatar"
                    />
                    <label htmlFor="photoUpload2" className="profile5-photo-button">
                      Change Photo 2
                    </label>
                    <input
                      type="file"
                      id="photoUpload2"
                      accept="image/*"
                      onChange={(e) => handlePhotoUpload(e, 1)}
                      style={{ display: "none" }}
                    />
                  </div>

                  {/* Fotoğraf 3 */}
                  <div className="profile5-avatar-wrapper">
                    <img
                      src={previewUrls[2]}
                      alt="Profile 3"
                      className="profile5-avatar"
                    />
                    <label htmlFor="photoUpload3" className="profile5-photo-button">
                      Change Photo 3
                    </label>
                    <input
                      type="file"
                      id="photoUpload3"
                      accept="image/*"
                      onChange={(e) => handlePhotoUpload(e, 2)}
                      style={{ display: "none" }}
                    />
                  </div>

                  </div>

                        <div className="form-group2 full-width">
                            <label className="form-label2">Your Bio</label>
                            <textarea
                                className="profile5-input-bio"
                                name="shorterbio"
                                value={formData.shorterbio}
                                onChange={handleChange}
                                rows="1"
                                placeholder="Tell us about yourself..."
                            />
                        </div>
                    {/* Profil Bilgileri */}
                    <div className="profile-group2">
                    <div className="form-group2 full-width">
                                <label className="form-label2">Full Name</label>
                                <input className="profile-input2" type="text" name="fullName" value={formData.fullName}
                                       onChange={handleChange}/>
                            </div>

                        <div className="form-group2 full-width">
                            <label className="form-label2">Birthdate</label>
                            <input className="profile-input2" type="date" name="birthDate" value={formData.birthDate}
                                   onChange={handleChange}/>
                        </div>

                        <div className="form-group2">
                            <label className="form-label2">Gender</label>
                            <select className="profile-input2" name="gender" value={formData.gender}
                                    onChange={handleChange}>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                                <option value="OTHER">Other</option>
                            </select>
                        </div>

                        <div className="form-group2">
                            <label className="form-label2">Looking For</label>
                            <select className="profile-input2" name="relationshipType" value={formData.relationshipType}
                                    onChange={handleChange}>
                                <option value="SERIOUS_RELATIONSHIP">Serious Relationship</option>
                                <option value="CASUAL_DATING">Casual Dating</option>
                                <option value="FRIENDSHIP">Friendship</option>
                                <option value="NO_PREFERENCE">No Preference</option>
                            </select>
                        </div>

                        <div className="form-group2">
                            <label className="form-label2">Age Preference</label>
                            <input className="profile-input2" type="text" name="agePreference"
                                   value={formData.agePreference} onChange={handleChange}/>
                        </div>

                        <div className="form-group2">
                            <label className="form-label2">Distance Preference (km)</label>
                            <input className="profile-input2" type="text" name="distancePreference"
                                   value={formData.distancePreference} onChange={handleChange}/>
                        </div>

                        <div className="form-group2">
                            <label className="form-label2">Height</label>
                            <input className="profile-input2" type="text" name="height" value={formData.height}
                                   onChange={handleChange}/>
                        </div>

                        <div className="form-group2">
                            <label className="form-label2">Weight</label>
                            <input className="profile-input2" type="text" name="weight" value={formData.weight}
                                   onChange={handleChange}/>
                        </div>

                        <div className="form-group2">
                            <label className="form-label2">Body Type</label>
                            <select className="profile-input2" name="bodyType" value={formData.bodyType}
                                    onChange={handleChange}>
                                <option value="SLIM">Slim</option>
                                <option value="AVERAGE">Average</option>
                                <option value="ATHLETIC">Athletic</option>
                                <option value="HEAVY">Heavy</option>
                                <option value="NO_PREFERENCE">No Preference</option>
                            </select>
                        </div>

                        <div className="form-group2">
                            <label className="form-label2">Do you smoke?</label>
                            <select className="profile-input2" name="smoke" value={formData.smoke}
                                    onChange={handleChange}>
                                <option value="NON_SMOKER">Non-Smoker</option>
                                <option value="OCCASIONAL">Occasional</option>
                                <option value="REGULAR">Regular</option>
                                <option value="TRYING_TO_QUIT">Trying to Quit</option>
                                <option value="NO_PREFERENCE">No Preference</option>
                            </select>
                        </div>

                        <div className="form-group2">
                            <label className="form-label2">Do you drink?</label>
                            <select className="profile-input2" name="alcohol" value={formData.alcohol}
                                    onChange={handleChange}>
                                <option value="NEVER">Never</option>
                                <option value="SOMETIMES">Sometimes</option>
                                <option value="REGULARLY">Regularly</option>
                                <option value="NO_PREFERENCE">No Preference</option>
                            </select>
                        </div>

                        <div className="form-group2">
                            <label className="form-label2">Dietary Preferences</label>
                            <select className="profile-input2" name="diet" value={formData.diet}
                                    onChange={handleChange}>
                                <option value="OMNIVORE">Omnivore</option>
                                <option value="VEGETARIAN">Vegetarian</option>
                                <option value="VEGAN">Vegan</option>
                                <option value="PESCATARIAN">Pescatarian</option>
                                <option value="FLEXITARIAN">Flexitarian</option>
                                <option value="OTHER">Other</option>
                                <option value="NO_PREFERENCE">No Preference</option>
                            </select>
                        </div>

                        <div className="form-group2">
                          <label className="form-label2">Location</label>
                          <input
                            className="profile-input2"
                            type="text"
                            name="location"
                            value={formData.location}
                            onChange={handleChange}
                          />
                          <button
                            className="profile-button2"
                            type="button"
                            onClick={handleGetLocation}
                            style={{ marginTop: "8px" }}
                          >
                            Use Current Location
                          </button>
                        </div>
                    </div>

                    {/* Butonlar */}
                    <div className="profile-buttons">
                        <button className="profile-button2" onClick={handleSave}>Save Changes</button>
                        <button className="profile-button2" onClick={handleGoHome}>Go Back to Home</button>
                    </div>
                </div>
            </div>
            <div className="footer-summary">
                    &copy; SoulM.com | Designed by Group 19
                </div>
        </>
    );
};

export default ProfilePage;