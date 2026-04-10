import { CourseCard } from './CourseCard';
import { Grid } from '@/components/ui/grid';

export default function CoursePreview({ courses }) {
  const activeCourses = courses.filter(course => course.progress < 100);

  const favorites = activeCourses
    .filter(c => c.isFavorite)
    .sort((a, b) => a.name.localeCompare(b.name));

  const recentCourses = activeCourses
    .filter(c => !c.isFavorite)
    .sort((a, b) => new Date(b.lastActive) - new Date(a.lastActive));

  const previewCourses = favorites.length < 3
    ? [...favorites, ...recentCourses].slice(0, 3)
    : favorites.slice(0, 6);

  return (
    <Grid cols={3}>
      {previewCourses.map((course) => (
        <CourseCard key={course.id} course={course} />
      ))}
    </Grid>
  );
}