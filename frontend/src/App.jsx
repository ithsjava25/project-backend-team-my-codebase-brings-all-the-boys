import {BrowserRouter, Routes, Route, Navigate} from 'react-router-dom';
import {AuthProvider} from './context/AuthContext';
import {ThemeProvider} from './context/ThemeContext';
import {ProtectedRoute} from './components/ProtectedRoute';
import {TooltipProvider} from "@/components/ui/tooltip.jsx";

import DashboardLayout from './layouts/DashboardLayout';
import LoginPage from './pages/LoginPage';
import Dashboard from './pages/Dashboard.jsx';
import CourseDetailPage from './pages/CourseDetailPage';
import AssignmentDetailPage from './pages/AssignmentDetailPage';
import UserCreatePage from './pages/admin/UserCreatePage';
import UserEditPage from './pages/admin/UserEditPage';
import CourseCreatePage from './pages/admin/CourseCreatePage';
import CourseEditPage from './pages/admin/CourseEditPage';

// Admin pages
import UserManagementPage from './pages/admin/UserManagementPage';
import CourseManagementPage from './pages/admin/CourseManagementPage';
import TestCoursesPage from "@/pages/TestCoursesPage.jsx";

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
                                <Route path="/assignments/:assignmentId" element={<AssignmentDetailPage/>}/>

                                {/* Admin routes */}
                                {/* Users */}
                                <Route path="/admin/users" element={<UserManagementPage/>}/>
                                <Route path="/admin/users/new" element={<UserCreatePage/>}/>
                                <Route path="/admin/users/:id/edit" element={<UserEditPage/>}/>

                                {/* Courses */}
                                <Route path="/admin/courses" element={<CourseManagementPage/>}/>
                                <Route path="/admin/courses/new" element={<CourseCreatePage/>}/>
                                <Route path="/admin/courses/:id/edit" element={<CourseEditPage/>}/>

                                <Route path="/test/courses" element={<TestCoursesPage/>}/>
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