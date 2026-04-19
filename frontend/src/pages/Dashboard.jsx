import {useAuthContext} from '@/context/AuthContext';
import {mapToCardFormat} from '@/mappers/courseMapper';
import {useCourses} from '@/hooks/useCourses';
import {useAssignments} from '@/hooks/useAssignments';
import StatsSection from '@/components/dashboard/StatsSection';
import {Tabs, TabsContent, TabsList, TabsTrigger} from '@/components/ui/tabs';
import {CourseListView} from '@/components/dashboard/CourseListView';
import {AssignmentListView} from '@/components/dashboard/AssignmentListView';
import TeacherOverview from '@/components/dashboard/TeacherOverview';
import AdminOverview from '@/components/dashboard/AdminOverview';
import {ActivityLogView} from '@/components/dashboard/ActivityLogView';
import {UpcomingDeadlinesView} from '@/components/dashboard/UpcomingDeadlinesView';
import {PendingSubmissionsView} from '@/components/dashboard/PendingSubmissionsView';
import UserManagementPage from './admin/UserManagementPage';
import {useSearchParams} from "react-router-dom";

export default function Dashboard() {
    const {user} = useAuthContext();
    const {courses, error: coursesError} = useCourses();
    const {assignments, error: assignmentsError} = useAssignments();

    const [searchParams, setSearchParams] = useSearchParams();

    const role = user?.role?.name;

    // ✅ VALID tabs per role
    const getTabs = () => {
        switch (role) {
            case 'ROLE_ADMIN':
                return ['overview', 'users', 'activity'];
            case 'ROLE_TEACHER':
                return ['overview', 'courses', 'grading'];
            default:
                return ['overview', 'courses', 'assignments'];
        }
    };

    const VALID_TABS = getTabs();

    // ✅ derive state from URL (NO useEffect)
    const activeTabRaw = searchParams.get('tab');
    const activeTab = VALID_TABS.includes(activeTabRaw) ? activeTabRaw : 'overview';

    const handleTabChange = (value) => {
        if (!VALID_TABS.includes(value)) return;
        setSearchParams({tab: value});
    };

    const mappedCourses = mapToCardFormat(courses);

    if (coursesError) {
        return (
            <div className="flex items-center justify-center h-64">
                <p className="text-destructive">Fel: {coursesError}</p>
            </div>
        );
    }

    const tabs = VALID_TABS.map((value) => ({
        value,
        label:
            value === 'overview' ? 'Översikt' :
                value === 'users' ? 'Användare' :
                    value === 'activity' ? 'Aktivitet' :
                        value === 'courses' ? 'Mina Kurser' :
                            value === 'grading' ? 'Bedömning' :
                                value === 'assignments' ? 'Uppgifter' :
                                    value
    }));

    return (
        <div className="space-y-6">

            {/* ✅ Stats now controls tabs */}
            <StatsSection role={role} onSelectTab={handleTabChange}/>

            {/* ✅ Controlled Tabs (NO warnings) */}
            <Tabs value={activeTab} onValueChange={handleTabChange} className="w-full">
                <TabsList>
                    {tabs.map((tab) => (
                        <TabsTrigger key={tab.value} value={tab.value}>
                            {tab.label}
                        </TabsTrigger>
                    ))}
                </TabsList>

                <TabsContent value="overview" className="space-y-4">
                    {role === 'ROLE_ADMIN' && <AdminOverview user={user}/>}
                    {role === 'ROLE_TEACHER' && <TeacherOverview user={user}/>}
                    {role === 'ROLE_STUDENT' && (
                        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                            <div className="lg:col-span-2">
                                <CourseListView courses={mappedCourses} view="grid" role="student"/>
                            </div>
                            <div>
                                <UpcomingDeadlinesView/>
                            </div>
                        </div>
                    )}
                </TabsContent>

                <TabsContent value="courses">
                    <CourseListView
                        courses={mappedCourses}
                        view="grid"
                        role={
                            role === 'ROLE_ADMIN' ? 'admin' :
                                role === 'ROLE_TEACHER' ? 'teacher' : 'student'
                        }
                    />
                </TabsContent>

                <TabsContent value="assignments">
                    <AssignmentListView
                        assignments={assignments}
                        error={assignmentsError}
                    />
                </TabsContent>

                <TabsContent value="users">
                    <UserManagementPage/>
                </TabsContent>

                <TabsContent value="activity">
                    <ActivityLogView limit={20}/>
                </TabsContent>

                <TabsContent value="grading">
                    <PendingSubmissionsView/>
                </TabsContent>
            </Tabs>
        </div>
    );
}