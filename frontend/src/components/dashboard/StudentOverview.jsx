import { CourseCard } from './CourseCard';
import { AssignmentList } from './AssignmentList';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { coursesDataPlaceholder } from '@/data/coursesDataPlaceholder.js';

export default function StudentOverview() {
  const myCourses = coursesDataPlaceholder;

  return (
    <div className="space-y-6">

      <Card>
        <CardHeader>
          <CardTitle>Kommande Uppgifter</CardTitle>
        </CardHeader>
        <CardContent className="p-0">
          <AssignmentList assignments={upcomingAssignments} />
        </CardContent>
      </Card>
      <Card>
        <CardHeader>
          <CardTitle>Pågående Kurser</CardTitle>
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