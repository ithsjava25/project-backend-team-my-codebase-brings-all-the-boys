import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import {TooltipProvider} from "@/components/ui/tooltip.jsx";
import DashboardLayout from './layouts/DashboardLayout';
import TestPage from './pages/TestPage';
import LoginPage from './pages/LoginPage';
import Dashboard from './pages/Dashboard.jsx';
import CourseDetailPage from './pages/CourseDetailPage';

export default function App() {
  return (
    <TooltipProvider>
      <ThemeProvider>
        <BrowserRouter>
          <AuthProvider>
            <Routes>
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/test" element={<TestPage />} />

              <Route element={
                <ProtectedRoute>
                  <DashboardLayout />
                </ProtectedRoute>}>
                <Route path="/" element={<Navigate to="/dashboard" replace />} />
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/courses/:courseId" element={<CourseDetailPage />} />
              </Route>

              <Route path="*" element={<Navigate to="/dashboard" replace />} />
            </Routes>

          </AuthProvider>
        </BrowserRouter>
      </ThemeProvider>
    </TooltipProvider>

  );
}