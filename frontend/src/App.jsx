import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import TestPage from './pages/TestPage';
import LoginPage from './pages/LoginPage';
import Dashboard from './pages/Dashboard.jsx';

export default function App() {
  return (
      <BrowserRouter>
        <AuthProvider>
          <Routes>
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/test" element={<TestPage />} />
            <Route path="/dashboard" element={
              <ProtectedRoute><Dashboard /></ProtectedRoute>
            } />
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Routes>

        </AuthProvider>
      </BrowserRouter>
  );
}