import { CourseCard } from './CourseCard';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useCourses } from '@/hooks/useCourses';
import { mapToCardFormat } from '@/mappers/courseMapper';

export default function StudentOverview() {
  const { courses, loading, error } = useCourses();
  const myCourses = mapToCardFormat(courses);

  if (loading) return <div>Laddar kurser...</div>;
  if (error) return <div>Fel: {error}</div>;

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle>Mina Kurser</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {myCourses.map((course) => (
              <CourseCard key={course.id} course={course} />
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}