import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import "./css/Manager.css";
import { FaUser } from "react-icons/fa";
import { FaUsers, FaBirthdayCake, FaRulerVertical, FaWeight, FaVenusMars, FaHeart } from 'react-icons/fa';
import api from "./api/axiosInstance";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { toast, ToastContainer } from "react-toastify";  // react-toastify importu
import "react-toastify/dist/ReactToastify.css";  // react-toastify stilleri
import Swal from "sweetalert2";

const ManagerPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState("");
  const [editData, setEditData] = useState({});
  const [stats, setStats] = useState({});
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [expandedKey, setExpandedKey] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState("");



  useEffect(() => {
    api.get('/api/stats')
      .then((response) => {
        console.log(response.data); // 👈 Buraya bak
        setStats(response.data);
      })
      .catch((error) => {
        console.error('İstatistikler alınırken hata oluştu:', error);
      });
  }, []);

  const handleFileChange = e => {
    const file = e.target.files[0];
    if (!file) return;
    setSelectedFile(file);
    setPreviewUrl(URL.createObjectURL(file));
  };

  useEffect(() => {
    const fetchEmployees = async () => {
      try {
        const [moderatorsRes, managersRes] = await Promise.all([
          api.get("/admin/moderators"),
          api.get("/admin/managers"),
        ]);

        const allEmployees = [
          ...moderatorsRes.data.map((m) => ({ ...m, role: "moderator" })),
          ...managersRes.data.map((m) => ({ ...m, role: "manager" })),
        ];

        setEmployees(allEmployees);
      } catch (err) {
        console.error("Çalışanlar alınamadı:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchEmployees();
  }, []);

  // Validation helpers
  const isValidFullName = name => {
    if (!name) return false;
    if (name.length > 50) return false;
    const parts = name.trim().split(/\s+/);
    if (parts.length < 2) return false;
    return parts.every(p => p.length >= 2 && /^[A-Za-zğüşöçıİĞÜŞÖÇ]+$/.test(p));
  };
  const isValidEmail = email => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  };
  const isValidPhone = phone => {
    const re = /^\+?\d{10,15}$/;
    return re.test(phone);
  };
  const calculateAge = dateStr => {
    const today = new Date();
    const dob = new Date(dateStr);
    let age = today.getFullYear() - dob.getFullYear();
    const m = today.getMonth() - dob.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) age--;
    return age;
  };

  const validateAll = data => {
    if (!isValidFullName(data.fullName)) {
      toast.error("Full name must be max 50 chars, at least two words, each ≥2 letters.");
      return false;
    }
    if (!isValidEmail(data.email)) {
      toast.error("Invalid email format.");
      return false;
    }
    if (!isValidPhone(data.phone)) {
      toast.error("Phone must be 10-15 digits, can include leading +.");
      return false;
    }
    if (!data.birthDate) {
      toast.error("Birth date is required.");
      return false;
    }
    const age = calculateAge(data.birthDate);
    if (age < 18) {
      toast.error("Employee must be at least 18 years old.");
      return false;
    }
    if (!data.startDate) {
      toast.error("Start date is required.");
      return false;
    }
    const start = new Date(data.startDate);
    const birth = new Date(data.birthDate);
    const now = new Date();
    if (start < birth) {
      toast.error("Start date cannot be before birth date.");
      return false;
    }
    const diffYears = start.getFullYear() - birth.getFullYear() - ((start.getMonth() < birth.getMonth() || (start.getMonth() === birth.getMonth() && start.getDate() < birth.getDate())) ? 1 : 0);
    if (diffYears < 18) {
      toast.error("Start date must be at least 18 years after birth date.");
      return false;
    }
    if (start > now) {
      toast.error("Start date cannot be in the future.");
      return false;
    }
    return true;
  };

  const handleSave = async (id) => {
    if (!validateAll(editData)) return;
  
    const originalRole = employees.find(emp => emp.id === id)?.role.toLowerCase() || "";
    const updatedRole = editData.role.toLowerCase();
    const formData = new FormData();
    let updatedEditData = { ...editData };

    if (!id) {
      updatedEditData = {
        ...updatedEditData,
        password: "Password123",
      };
    } else {
      updatedEditData = {
        ...updatedEditData,
        id:"",
      };
    }

    formData.append(
      "data",
      new Blob([JSON.stringify(updatedEditData)], { type: "application/json" })
    );
    if (selectedFile) {
      formData.append("image", selectedFile);
    }

    try {
      if (!id) {
        await api.post(`/admin/${updatedRole}s`, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });
        toast.success("New employee hired successfully!");
        // Önizleme için imageUrl değil imageFile üzerinden gösterdiğinizi varsayalım
        setEmployees(prev => [...prev, {
          ...editData,
          id: Date.now(),
          role: updatedRole,
          imageUrl: selectedFile, // ya da sunucudan dönen imageUrl
        }]);
      } else {
        if (originalRole === updatedRole) {
          await api.put(`/admin/${updatedRole}s/${id}`, formData, {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          });
        } else {
          await api.post(`/admin/${updatedRole}s`, formData, {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          });
          await api.delete(`/admin/${originalRole}s/${id}`);
        }
        toast.success("Employee successfully updated!");
        setEmployees(prev =>
          prev.map(emp =>
            `${emp.role}-${emp.id}` === expandedKey 
              ? {
                  ...emp,
                  ...editData,
                  imageUrl: selectedFile ? URL.createObjectURL(selectedFile) : editData.imageUrl,
                }
              : emp
          )
        );
        
      }
      setExpandedKey(null);
      setEditData({});
    } catch (error) {
      console.error(error);
      if (error.response && error.response.data) {
        toast.error(error.response.data);  // Backend'den gelen hata mesajını göster
      } else {
        toast.error("Error saving employee.");
      }
    }
  };
  
  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };

  const toggleExpand = (key) => {
    if (expandedKey === key) {
      setExpandedKey(null);
      setEditData({});
    } else {
      setExpandedKey(key);
      const [role, id] = key.split("-");
      const employee = employees.find(
        (emp) => emp.role === role && String(emp.id) === id
      );
      setEditData({ ...employee });
    }
  };

  const handleEditChange = (e) => {
    const { name, value } = e.target;
    setEditData({ ...editData, [name]: value });
  };

  const handleCancel = () => {
    setExpandedKey(null); 
    setEditData({});
  };

  const handleDeleteClick = async (emp) => {
    const result = await Swal.fire({
      title: 'Are you sure?',
      text: `Do you really want to delete ${emp.fullName}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, delete it!',
    });
  
    if (result.isConfirmed) {
      try {
        await api.delete(`/admin/${emp.role}s/${emp.id}`);
        toast.success("Employee deleted successfully.");
        setEmployees(prev =>
          prev.map(emp =>
            `${emp.role}-${emp.id}` === expandedKey 
              ? {
                  ...emp,
                  ...editData,
                  imageUrl: selectedFile ? URL.createObjectURL(selectedFile) : editData.imageUrl,
                }
              : emp
          )
        );
      } catch (err) {
        console.error(err);
        toast.error("Failed to delete employee.");
      }
    }
  };

  const handleViewProfile = () => {
    navigate('/profilePageForManager');
  };

  const handleLogout = () => {
    sessionStorage.clear();
    navigate("/login");
  };

  const handleLogoClick = () => {
    navigate("/index");
  };

  const filteredEmployees = employees.filter((emp) =>
    emp.fullName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <>
      <header className="header">
        <div className="header-left">
          <div className="logo">
            <h1 className="logo-text" onClick={handleLogoClick} style={{ cursor: "pointer" }}>SoulM</h1>
          </div>
        </div>
        <div className="profile-container">
          <ul className="profile">
            <li className="profile-item">
              <a href="#"> <FaUser /> Profile</a>
              <ul className="dropdown">
                <li><a href="#" onClick={handleLogout}>Logout</a></li>
                <li><a href="/profilePageForManager" onClick={handleViewProfile}>View Profile</a></li>
              </ul>
            </li>
          </ul>
        </div>
      </header>

      <div className="manager-page-container">
        <h1 className="manager-page-title">Employee Management</h1>
        
        <div className="search-container">
          <input
            type="text"
            placeholder="Search by name..."
            value={searchQuery}
            onChange={handleSearchChange}
            className="search-input"
          />
        </div>

        <div className="hire-button-container">
          <button
            className="hire-button"
            onClick={() => {
              setExpandedKey("new-employee");
              setEditData({
                fullName: "",
                email: "",
                phone: "",
                birthDate: "",
                startDate: "",
                role: "moderator", 
                imageUrl: "",
                password: "",
              });
            }}
          >
            Hire New Employee
          </button>
        </div>

        <div className="employee-header">
          <span className="header-fullname">Full Name</span>
          <span className="header-email">Email</span>
          <span className="header-phone">Phone No</span>
          <span className="header-birthdate">Birth Date</span>
          <span className="header-startdate">Start Date</span>
          <span className="header-role">Role</span>
        </div>

        <div className="employee-list">
          {expandedKey === "new-employee" && (
          <div className="employee-card">
            <div className="employee-edit">
              <div className="edit-row">
                <label>Full Name:</label>
                <input
                  type="text"
                  name="fullName"
                  value={editData.fullName || ""}
                  onChange={handleEditChange}
                />
              </div>
              <div className="edit-row">
                <label>Email:</label>
                <input
                  type="email"
                  name="email"
                  value={editData.email || ""}
                  onChange={handleEditChange}
                />
              </div>
              <div className="edit-row">
                <label>Phone:</label>
                <input
                  type="text"
                  name="phone"
                  value={editData.phone || ""}
                  onChange={handleEditChange}
                />
              </div>
              <div className="edit-row">
                <label>Birth Date:</label>
                <DatePicker
                  selected={editData.birthDate ? new Date(editData.birthDate) : null}
                  onChange={(date) =>
                    setEditData((prev) => ({
                      ...prev,
                      birthDate: date.toISOString().split("T")[0],
                    }))
                  }
                  dateFormat="yyyy-MM-dd"
                />
              </div>
              <div className="edit-row">
                <label>Start Date:</label>
                <DatePicker
                  selected={editData.startDate ? new Date(editData.startDate) : null}
                  onChange={(date) =>
                    setEditData((prev) => ({
                      ...prev,
                      startDate: date.toISOString().split("T")[0],
                    }))
                  }
                  dateFormat="yyyy-MM-dd"
                />
              </div>
              <div className="edit-row">
                <label>Image:</label>
                <input
                  type="file"
                  accept="image/*"
                  name="imageFile"
                  onChange={handleFileChange}
                />
                {previewUrl && (
                  <div className="preview-container">
                    <img
                      src={previewUrl}
                      alt="Image Preview"
                      className="employee-photo"
                    />
                  </div>
                )}
              </div>
              <div className="edit-row">
                <label>Role:</label>
                <select
                  name="role"
                  value={editData.role || ""}
                  onChange={handleEditChange}
                >
                  <option value="manager">Manager</option>
                  <option value="moderator">Moderator</option>
                </select>
              </div>
              <div className="edit-buttons">
                <button
                  onClick={() => handleSave(null)}
                  className="save-button"
                >
                  Hire
                </button>
                <button onClick={handleCancel} className="cancel-button">
                  Cancel
                </button>
              </div>
            </div>
          </div>
        )}
          {filteredEmployees.map((emp) => {
            const compKey = `${emp.role}-${emp.id}`;  // compKey burada doğru şekilde tanımlanmalı

            return (
              <div key={compKey} className="employee-card">
                <div
                  className="employee-summary"
                  onClick={() => toggleExpand(compKey)}  // buradaki key de compKey olacak
                >
                  <img
                    src={emp.imageUrl}
                    alt={emp.fullName}
                    className="employee-photo"
                  />
                  <div className="employee-info">
                    <span className="info-fullname">{emp.fullName}</span>
                    <span className="info-email">{emp.email}</span>
                    <span className="info-phone">{emp.phone}</span>
                    <span className="info-birthdate">{emp.birthDate}</span>
                    <span className="info-startdate">{emp.startDate}</span>
                    <span className="info-role">{emp.role}</span>
                  </div>
                </div>
                {expandedKey === compKey && (
                  <div className="employee-edit">
                    <div className="edit-row">
                      <label>Full Name:</label>
                      <input
                        type="text"
                        name="fullName"
                        value={editData.fullName || ""}
                        onChange={handleEditChange}
                      />
                    </div>
                    <div className="edit-row">
                      <label>Email:</label>
                      <input
                        type="email"
                        name="email"
                        value={editData.email || ""}
                        onChange={handleEditChange}
                      />
                    </div>
                    <div className="edit-row">
                      <label>Phone:</label>
                      <input
                        type="text"
                        name="phone"
                        value={editData.phone || ""}
                        onChange={handleEditChange}
                      />
                    </div>
                    <div className="edit-row">
                      <label>Birth Date:</label>
                      <DatePicker
                        selected={editData.birthDate ? new Date(editData.birthDate) : null}
                        onChange={(date) =>
                          setEditData((prev) => ({ ...prev, birthDate: date.toISOString().split("T")[0] }))
                        }
                        dateFormat="yyyy-MM-dd"
                      />
                    </div>
                    <div className="edit-row">
                      <label>Start Date:</label>
                      <DatePicker
                        selected={editData.startDate ? new Date(editData.startDate) : null}
                        onChange={(date) =>
                          setEditData((prev) => ({ ...prev, startDate: date.toISOString().split("T")[0] }))
                        }
                        dateFormat="yyyy-MM-dd"
                      />
                    </div>
                    <div className="edit-row">
                      <label>Image:</label>
                      <input
                        type="file"
                        accept="image/*"
                        name="imageFile"
                        onChange={handleFileChange}
                      />
                      {previewUrl && (
                        <div className="preview-container">
                          <img
                            src={previewUrl}
                            alt="Image Preview"
                            className="employee-photo"
                          />
                        </div>
                      )}
                    </div>
                    <div className="edit-row">
                      <label>Role:</label>
                      <select
                        name="role"
                        value={editData.role || ""}
                        onChange={handleEditChange}
                      >
                        <option value="manager">Manager</option>
                        <option value="moderator">Moderator</option>
                      </select>
                    </div>
                    <div className="edit-buttons">
                      <button onClick={() => handleSave(emp.id)} className="save-button">
                        Save
                      </button>
                      <button onClick={handleCancel} className="cancel-button">
                        Cancel
                      </button>
                      <button onClick={() => handleDeleteClick(emp)} className="delete-button2">
                        Delete
                      </button>
                    </div>
                  </div>
                )}
              </div>
            );
          })}
        </div>
                <div className="stats-container">
          <h1>Statistics</h1>

          <div className="stats-cards">
            <div className="stat-card">
              <h4><FaUsers /> Total Users</h4>
              <p>{stats.totalUsers}</p>
            </div>
            <div className="stat-card">
            <h4>
            <span><FaBirthdayCake /></span>{"  "}
            <span>Average Age</span>
            </h4>
              <p>{Math.round(stats.averageAge)}</p>
            </div>
          </div>

          <div className="distribution-section">
            <h3><FaVenusMars /> Gender Distribution</h3>
            <ul>
              {stats.genderDistribution &&
                Object.entries(stats.genderDistribution).map(([key, value]) => (
                  <li key={key}>
                    <strong>{key}:</strong> {value}
                  </li>
                ))}
            </ul>
          </div>

          <div className="distribution-section">
            <h3><FaHeart /> Relationship Types</h3>
            <ul>
              {stats.relationshipTypes &&
                Object.entries(stats.relationshipTypes).map(([key, value]) => (
                  <li key={key}>
                    <strong>{key}:</strong> {value}
                  </li>
                ))}
            </ul>
          </div>
        </div>


      </div>

      <div className="footer-summary2">
        &copy; SoulM.com | Designed by Group 19
      </div>

      {/* ToastContainer component */}
      <ToastContainer />  {/* Bildirimlerin görüneceği alan */}
    </>
  );
};

export default ManagerPage;