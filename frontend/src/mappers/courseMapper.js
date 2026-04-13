import { GraduationCap } from 'lucide-react';

export const mapToCourseDetailFormat = (course) => {
  if (!course) return null;

  return {
    id: course.id,
    name: course.name,
    description: course.description,
    schoolClassName: course.schoolClassName,
    leadTeacher: course.leadTeacher ? {
      username: course.leadTeacher.username,
      email: course.leadTeacher.email
    } : null,
    assistants: course.assistants || [],
    assignments: course.assignments || []
  };
};

/**
 * Maps backend course data to format needed by CourseSwitcher/Sidebar
 * @param {Array} courses - Array of CourseSurfaceResponse
 * @returns {Array} - Formatted
 */
export const mapToSidebarFormat = (courses) => {
  if (!courses) return [];

  return courses.map(course => ({
    name: course.name,
    logo: GraduationCap, // Replace with actual logo component if implemented
    schoolClassName: course.schoolClassName,
    url: `/courses/${course.id}`
  }));
};

/**
 * Maps to dashboard card format
 * TODO: Implementera när dashboard ska byggas
 */
export const mapToCardFormat = (courses) => {
  if (!courses) return [];

  return courses.map(course => ({
    id: course.id,
    title: course.name,
    schoolClass: course.schoolClassName,
    link: `/courses/${course.id}`
    // Add more fields when dashboard needs it
  }));
};

/**
 * Maps to admin table format
 * TODO: Implementera när admin-panel ska byggas
 */
export const mapToTableRowFormat = (courses) => {
  if (!courses) return [];

  return courses.map(course => ({
    id: course.id,
    name: course.name,
    schoolClass: course.schoolClassName,
  }));
};