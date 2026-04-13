import { useAuthContext } from '@/context/AuthContext';
import { mapToCardFormat } from '@/mappers/courseMapper';
import { useCourses } from '@/hooks/useCourses';
import StatsSection from '@/components/dashboard/StatsSection';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import StudentOverview from '@/components/dashboard/StudentOverview';
import TeacherOverview from '@/components/dashboard/TeacherOverview';
import AdminOverview from '@/components/dashboard/AdminOverview';

export default function Dashboard() {
  const { user, loading: authLoading } = useAuthContext();
  const { courses, loading: coursesLoading, error: coursesError } = useCourses();

  const mappedCourses = mapToCardFormat(courses);
  const role = user?.role?.name;

  if (authLoading || coursesLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <p>Laddar...</p>
      </div>
    );
  }

  if (coursesError) {
    return (
      <div className="flex items-center justify-center h-64">
        <p className="text-destructive">Fel: {coursesError}</p>
      </div>
    );
  }

  const getTabs = () => {
    switch (role) {
      case 'ROLE_ADMIN':
        return [
          { value: 'overview', label: 'Översikt' },
          { value: 'users', label: 'Användare' },
          { value: 'activity', label: 'Aktivitet' },
        ];
      case 'ROLE_TEACHER':
        return [
          { value: 'overview', label: 'Översikt' },
          { value: 'courses', label: 'Mina Kurser' },
          { value: 'grading', label: 'Rättning' },
        ];
      default:
        return [
          { value: 'overview', label: 'Översikt' },
          { value: 'courses', label: 'Mina Kurser' },
          { value: 'assignments', label: 'Uppgifter' },
        ];
    }
  };

  const tabs = getTabs();

  return (
    <div className="space-y-6">
      <StatsSection role={role} user={user} />

      <Tabs defaultValue="overview" className="w-full">
        <TabsList>
          {tabs.map((tab) => (
            <TabsTrigger key={tab.value} value={tab.value}>
              {tab.label}
            </TabsTrigger>
          ))}
        </TabsList>

        <TabsContent value="overview" className="space-y-4">
          {role === 'ROLE_ADMIN' && <AdminOverview user={user} />}
          {role === 'ROLE_TEACHER' && <TeacherOverview user={user} />}
          {role === 'ROLE_STUDENT' && <StudentOverview courses={mappedCourses} />}
        </TabsContent>

        <TabsContent value="courses">
          <StudentOverview courses={mappedCourses} />
        </TabsContent>

        <TabsContent value="assignments">
          <div>Uppgifter kommer synas här</div>
        </TabsContent>

        <TabsContent value="users">
          <div>Användare kommer synas här</div>
        </TabsContent>

        <TabsContent value="activity">
          <div>Aktivitet kommer synas här</div>
        </TabsContent>
      </Tabs>
    </div>
  );
}