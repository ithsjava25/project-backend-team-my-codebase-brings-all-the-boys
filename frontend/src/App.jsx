import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import TestPage from './pages/TestPage';
import LoginPage from './pages/LoginPage';
import Dashboard from './pages/Dashboard.jsx';
import DashboardV2 from './pages/Dashboardv2.jsx';
import {TooltipProvider} from "@/components/ui/tooltip.jsx";

export default function App() {
  return (
    <TooltipProvider>
      <BrowserRouter>
        <AuthProvider>
          <Routes>
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/test" element={<TestPage />} />
            <Route path="/dashboardv2" element={<DashboardV2 />} />
            <Route path="/dashboard" element={
              <ProtectedRoute><Dashboard /></ProtectedRoute>
            } />
            <Route path="*" element={<Navigate to="/dashboardv2" replace />} />
          </Routes>

        </AuthProvider>
      </BrowserRouter>
    </TooltipProvider>

  );
}