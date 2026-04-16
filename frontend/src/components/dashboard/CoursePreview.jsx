import { CourseCard } from './CourseCard';
import { Grid } from '@/components/ui/grid';

export default function CoursePreview({ courses }) {
  if (!Array.isArray(courses)) return [];

  const sortedCourses = [...courses]
    .sort((a, b) => a.name.localeCompare(b.name));

  const previewCourses = sortedCourses.slice(0, 3);

  return (
    <Grid cols={3}>
      {previewCourses.map((course) => (
        <CourseCard key={course.id} course={course} />
      ))}
    </Grid>
  );
}