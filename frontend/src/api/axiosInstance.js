// src/api/axiosInstance.js

import axios from "axios";

// Axios örneği oluşturuluyor
const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || "http://localhost:8080", // .env'den al, yoksa fallback kullan
  headers: {
    "Content-Type": "application/json"
  }
});

// İsteğe bağlı: Token varsa her isteğe otomatik ekle
api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem("token"); // token'ı sessionStorage'dan al
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

// İsteğe bağlı: Hata yakalama/log işlemleri
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("API error:", error);
    return Promise.reject(error);
  }
);

export default api;
