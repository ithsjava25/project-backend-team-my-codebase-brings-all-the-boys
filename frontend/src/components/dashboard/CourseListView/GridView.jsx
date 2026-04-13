import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Grid } from '@/components/ui/grid';
import { CourseCard } from '../CourseCard';

export function GridView({ courses }) {
  if (!courses || courses.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Mina Kurser</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground">Inga kurser att visa.</p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Grid cols={3}>
      {courses.map((course) => (
        <CourseCard key={course.id} course={course} />
      ))}
    </Grid>
  );
}