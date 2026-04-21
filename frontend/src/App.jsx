import {BrowserRouter, Routes, Route, Navigate, Outlet} from 'react-router-dom';
import {AuthProvider} from './context/AuthContext';
import {ThemeProvider} from './context/ThemeContext';
import {ProtectedRoute} from './components/ProtectedRoute';
import {TooltipProvider} from "@/components/ui/tooltip.jsx";

import DashboardLayout from './layouts/DashboardLayout';
import LoginPage from './pages/LoginPage';
import Dashboard from './pages/Dashboard.jsx';
import CourseDetailPage from './pages/CourseDetailPage';
import SchoolClassDetailPage from './pages/SchoolClassDetailPage';
import AssignmentDetailPage from './pages/AssignmentDetailPage';
import UserCreatePage from './pages/admin/UserCreatePage';
import UserEditPage from './pages/admin/UserEditPage';
import CourseCreatePage from './pages/admin/CourseCreatePage';
import CourseEditPage from './pages/admin/CourseEditPage';

import UserManagementPage from './pages/admin/UserManagementPage';
import CourseManagementPage from './pages/admin/CourseManagementPage';
import SchoolClassManagementPage from './pages/admin/SchoolClassManagementPage';

export default function App() {
    return (
        <TooltipProvider>
            <ThemeProvider>
                <BrowserRouter>
                    <AuthProvider>
                        <Routes>

                            {/* Redirect root */}
                            <Route path="/" element={<Navigate to="/dashboard" replace/>}/>

                            {/* Public route */}
                            <Route path="/login" element={<LoginPage/>}/>

                            {/* Protected app layout */}
                            <Route
                                element={
                                    <ProtectedRoute>
                                        <DashboardLayout/>
                                    </ProtectedRoute>
                                }
                            >
                                <Route path="/dashboard" element={<Dashboard/>}/>
                                <Route path="/courses/:courseId" element={<CourseDetailPage/>}/>
                                <Route path="/school-classes/:id" element={<SchoolClassDetailPage/>}/>
                                <Route path="/assignments/:assignmentId" element={<AssignmentDetailPage/>}/>

                                {/* Admin routes - role-gated */}
                                <Route
                                    path="admin/*"
                                    element={
                                        <ProtectedRoute requiredRole="ROLE_ADMIN">
                                            <Outlet/>
                                        </ProtectedRoute>
                                    }
                                >
                                    {/* Users */}
                                    <Route path="users" element={<UserManagementPage/>}/>
                                    <Route path="users/new" element={<UserCreatePage/>}/>
                                    <Route path="users/:id/edit" element={<UserEditPage/>}/>

                                    {/* Courses */}
                                    <Route path="courses" element={<CourseManagementPage/>}/>
                                    <Route path="courses/new" element={<CourseCreatePage/>}/>
                                    <Route path="courses/:id/edit" element={<CourseEditPage/>}/>

                                    {/* School Classes */}
                                    <Route path="school-classes" element={<SchoolClassManagementPage/>}/>
                                </Route>
                            </Route>

                            {/* Catch-all */}
                            <Route path="*" element={<Navigate to="/dashboard" replace/>}/>

                        </Routes>
                    </AuthProvider>
                </BrowserRouter>
            </ThemeProvider>
        </TooltipProvider>
    );
}