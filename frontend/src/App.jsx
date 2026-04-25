import {BrowserRouter, Routes, Route, Navigate, Outlet} from 'react-router-dom';
import {AuthProvider} from './context/AuthContext';
import {ThemeProvider} from './context/ThemeContext';
import {ProtectedRoute} from './components/ProtectedRoute';
import {TooltipProvider} from "@/components/ui/tooltip.jsx";

import DashboardLayout from './layouts/DashboardLayout';
import LoginPage from './pages/LoginPage';
import Dashboard from './pages/Dashboard.jsx';
import CourseDetailPage from './pages/CourseDetailPage';
import UserProfilePage from './pages/UserProfilePage';
import ProfileEditPage from './pages/ProfileEditPage';
import SchoolClassDetailPage from './pages/SchoolClassDetailPage';
import AssignmentDetailPage from './pages/AssignmentDetailPage';
import AssignmentCreatePage from './pages/AssignmentCreatePage';
import AssignmentGradingPage from './pages/AssignmentGradingPage';
import UserCreatePage from './pages/admin/UserCreatePage';
import UserEditPage from './pages/admin/UserEditPage';
import CourseCreatePage from './pages/admin/CourseCreatePage';
import CourseEditPage from './pages/admin/CourseEditPage';
import AssignmentEditPage from './pages/admin/AssignmentEditPage';

import UserManagementPage from './pages/admin/UserManagementPage';
import CourseManagementPage from './pages/admin/CourseManagementPage';
import SchoolClassManagementPage from './pages/admin/SchoolClassManagementPage';
import SchoolClassCreatePage from './pages/admin/SchoolClassCreatePage';
import SchoolClassEditPage from './pages/admin/SchoolClassEditPage';

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
                                <Route path="/profile/:id" element={<UserProfilePage/>}/>
                                <Route path="/profile/edit" element={<ProfileEditPage/>}/>
                                <Route path="/courses/:courseId/assignments/new" element={<AssignmentCreatePage/>}/>
                                <Route path="/assignments/:assignmentId" element={<AssignmentDetailPage/>}/>
                                <Route
                                    path="/assignments/:assignmentId/grade/:studentId"
                                    element={
                                        <ProtectedRoute allowedRoles={['ROLE_TEACHER', 'ROLE_ADMIN']}>
                                            <AssignmentGradingPage/>
                                        </ProtectedRoute>
                                    }
                                />
                                <Route path="/school-classes/:id" element={<SchoolClassDetailPage/>}/>

                                {/* Admin-prefixed routes */}
                                <Route path="admin"
                                       element={<ProtectedRoute allowedRoles={['ROLE_TEACHER', 'ROLE_ADMIN']}><Outlet/></ProtectedRoute>}>
                                    {/* Shared (Teacher + Admin) */}
                                    <Route path="courses/new" element={<CourseCreatePage/>}/>
                                    <Route path="courses/:id/edit" element={<CourseEditPage/>}/>
                                    <Route path="assignments/:id/edit" element={<AssignmentEditPage/>}/>

                                    {/* Strictly Admin */}
                                    <Route
                                        element={<ProtectedRoute requiredRole="ROLE_ADMIN"><Outlet/></ProtectedRoute>}>
                                        <Route path="users" element={<UserManagementPage/>}/>
                                        <Route path="users/new" element={<UserCreatePage/>}/>
                                        <Route path="users/:id/edit" element={<UserEditPage/>}/>
                                        <Route path="courses" element={<CourseManagementPage/>}/>
                                        <Route path="school-classes" element={<SchoolClassManagementPage/>}/>
                                        <Route path="school-classes/new" element={<SchoolClassCreatePage/>}/>
                                        <Route path="school-classes/:id/edit" element={<SchoolClassEditPage/>}/>
                                    </Route>
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
