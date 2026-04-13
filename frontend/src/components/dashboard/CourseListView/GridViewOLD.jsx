import { CourseCard } from '../CourseCard';
import { Grid } from '@/components/ui/grid';

export function GridView({ courses }) {
  return (
    <Grid cols={3}>
      {courses.map((course) => (
        <CourseCard key={course.id} course={course} />
      ))}
    </Grid>
  );
}