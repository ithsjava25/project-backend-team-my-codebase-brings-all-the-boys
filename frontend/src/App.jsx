import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import { TooltipProvider } from "@/components/ui/tooltip.jsx";

import DashboardLayout from './layouts/DashboardLayout';
import LoginPage from './pages/LoginPage';
import Dashboard from './pages/Dashboard.jsx';
import CourseDetailPage from './pages/CourseDetailPage';
import AssignmentDetailPage from './pages/AssignmentDetailPage';

// Admin pages
import UserManagementPage from './pages/admin/UserManagementPage';
import CourseManagementPage from './pages/admin/CourseManagementPage';

export default function App() {
    return (
        <TooltipProvider>
            <ThemeProvider>
                <BrowserRouter>
                    <AuthProvider>
                        <Routes>

                            {/* Redirect root */}
                            <Route path="/" element={<Navigate to="/dashboard" replace />} />

                            {/* Public route */}
                            <Route path="/login" element={<LoginPage />} />

                            {/* Protected app layout */}
                            <Route
                                element={
                                    <ProtectedRoute>
                                        <DashboardLayout />
                                    </ProtectedRoute>
                                }
                            >
                                <Route path="/dashboard" element={<Dashboard />} />
                                <Route path="/courses/:courseId" element={<CourseDetailPage />} />
                                <Route path="/assignments/:assignmentId" element={<AssignmentDetailPage />} />

                                {/* Admin routes */}
                                <Route path="/admin/users" element={<UserManagementPage />} />
                                <Route path="/admin/users/new" element={<UserManagementPage />} />
                                <Route path="/admin/users/:id/edit" element={<UserManagementPage />} />

                                <Route path="/admin/courses" element={<CourseManagementPage />} />
                                <Route path="/admin/courses/new" element={<CourseManagementPage />} />
                                <Route path="/admin/courses/:id/edit" element={<CourseManagementPage />} />
                            </Route>

                            {/* Catch-all */}
                            <Route path="*" element={<Navigate to="/dashboard" replace />} />

                        </Routes>
                    </AuthProvider>
                </BrowserRouter>
            </ThemeProvider>
        </TooltipProvider>
    );
}