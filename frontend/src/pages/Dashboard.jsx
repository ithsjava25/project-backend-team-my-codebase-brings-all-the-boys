import { useAuthContext } from '@/context/AuthContext';
import { coursesDataPlaceholder } from '@/data/coursesDataPlaceholder.js';
import WelcomeHeader from '@/components/dashboard/WelcomeHeader';
import StatsSection from '@/components/dashboard/StatsSection';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import StudentOverview from '@/components/dashboard/StudentOverview';
import TeacherOverview from '@/components/dashboard/TeacherOverview';
import AdminOverview from '@/components/dashboard/AdminOverview';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import CoursePreview from '@/components/dashboard/CoursePreview';
import CourseList from '@/components/dashboard/CourseList';

export default function Dashboard() {
  const { user, loading } = useAuthContext();

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <p>Laddar...</p>
      </div>
    );
  }

  const role = user?.role?.name;

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
          {role === 'ROLE_STUDENT' && (
            <Card>
              <CardHeader>
                <CardTitle>Pågående kurser</CardTitle>
              </CardHeader>
              <CardContent>
                <CoursePreview courses={coursesDataPlaceholder} />
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="courses">
          <CourseList courses={coursesDataPlaceholder} />
        </TabsContent>

        <TabsContent value="assignments">
          <div>Uppgifter kommer synas här</div>
        </TabsContent>
      </Tabs>
    </div>
  );
}