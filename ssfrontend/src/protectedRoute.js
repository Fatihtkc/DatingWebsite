import { Navigate, Outlet } from "react-router-dom";

const ProtectedRoute = ({ allowedRoles }) => {
  const token = sessionStorage.getItem('token');
  const role = sessionStorage.getItem('role');

  if (!token) {
    // Token yoksa giriş yapması gerekiyor
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(role)) {
    // Rol uygunsuzsa erişim yok
    return <Navigate to="/unauthorized" replace />;
  }

  // Token ve role uygunsa sayfayı göster
  return <Outlet />;
};

export default ProtectedRoute;
