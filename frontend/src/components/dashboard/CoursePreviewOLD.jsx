export default function CoursePreview({ courses }) {
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