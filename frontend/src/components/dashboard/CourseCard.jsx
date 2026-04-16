import { Link } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';

export function CourseCard({ course }) {
  return (
    <Link to={`/courses/${course.id}`}>
      <Card className="cursor-pointer hover:bg-accent transition-colors h-full">
        <CardHeader>
          <CardTitle>{course.name}</CardTitle>
          <CardDescription>{course.class}</CardDescription>
        </CardHeader>
      </Card>
    </Link>
  );
}