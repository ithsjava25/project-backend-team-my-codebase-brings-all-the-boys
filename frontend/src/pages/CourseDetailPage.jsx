import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useCourseDetail } from '@/hooks/useCourseDetail';
import { mapToCourseDetailFormat } from '@/mappers/courseMapper';
import { useAuthContext } from '@/context/AuthContext';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { ArrowLeft } from 'lucide-react';
import { Link } from 'react-router-dom';
import { AssignmentListView } from '@/components/dashboard/AssignmentListView';

export default function CourseDetailPage() {
  const { courseId } = useParams();
  const { course, error } = useCourseDetail(courseId);
  const { user } = useAuthContext();
  const role = user?.role?.name;

  const [activeTab, setActiveTab] = useState('overview');

  if (error) return <div>Ett fel uppstod: {error}</div>;
  if (!course) return null; // Don't render anything while loading

  const courseData = mapToCourseDetailFormat(course);

  const getTabs = () => {
    const baseTabs = [
      { value: 'overview', label: 'Översikt' },
      { value: 'assignments', label: 'Uppgifter' },
    ];

    if (role === 'ROLE_TEACHER' || role === 'ROLE_ADMIN') {
      return [
        ...baseTabs,
        { value: 'grading', label: 'Bedömning' },
        { value: 'participants', label: 'Deltagare' },
      ];
    }

    return baseTabs;
  };

  const tabs = getTabs();

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="space-y-2">
        <Link to="/dashboard" className="inline-flex items-center text-sm text-muted-foreground hover:text-foreground">
          <ArrowLeft className="mr-2 h-4 w-4" />
          Tillbaka till dashboard
        </Link>

        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold">{courseData.name}</h1>
          <Badge variant="secondary">{courseData.schoolClassName}</Badge>
        </div>
      </div>

      {/* Tabs */}
      <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
        <TabsList>
          {tabs.map((tab) => (
            <TabsTrigger key={tab.value} value={tab.value}>
              {tab.label}
            </TabsTrigger>
          ))}
        </TabsList>

        {/* Översikt */}
        <TabsContent value="overview" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Om kursen</CardTitle>
            </CardHeader>
            <CardContent>
              <p>{courseData.description}</p>
            </CardContent>
          </Card>

          {courseData.leadTeacher && (
            <Card className="w-60">
              <CardHeader>
                <CardTitle>Kursansvarig</CardTitle>
              </CardHeader>
              <CardContent>
                <p>{courseData.leadTeacher.username}</p>
                <p className="text-sm text-muted-foreground">
                  {courseData.leadTeacher.email}
                </p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        {/* Uppgifter */}
        <TabsContent value="assignments">
          <AssignmentListView
            assignments={courseData.assignments}
            title={`Uppgifter i ${courseData.name}`}
            subtitle={`${courseData.assignments.length} uppgift(er)`}
          />
        </TabsContent>

        {/* Lärare: Bedömning */}
        {(role === 'ROLE_TEACHER' || role === 'ROLE_ADMIN') && (
          <TabsContent value="grading">
            <Card>
              <CardHeader>
                <CardTitle>Bedömning</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-muted-foreground">Inlämningar att rätta kommer snart...</p>
              </CardContent>
            </Card>
          </TabsContent>
        )}

        {/* Lärare: Deltagare */}
        {(role === 'ROLE_TEACHER' || role === 'ROLE_ADMIN') && (
          <TabsContent value="participants">
            <Card>
              <CardHeader>
                <CardTitle>Deltagare</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-muted-foreground">Deltagarlista kommer snart...</p>
              </CardContent>
            </Card>
          </TabsContent>
        )}
      </Tabs>
    </div>
  );
}