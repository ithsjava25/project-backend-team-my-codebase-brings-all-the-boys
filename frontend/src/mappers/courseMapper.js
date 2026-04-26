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
    assistants: Array.isArray(course.assistants) ? course.assistants : [],
    students: Array.isArray(course.students) ? course.students : [],
    assignments: Array.isArray(course.assignments) ? course.assignments.map(a => ({
      id: a.id,
      title: a.title,
      status: a.status,
      studentStatus: a.studentStatus,
      deadline: a.deadline,
      createdAt: a.createdAt,
      updatedAt: a.updatedAt
    })) : []
  };
};

/**
 * Maps backend course data to format needed by CourseSwitcher/Sidebar
 * @param {Array} courses - Array of CourseSurfaceResponse
 * @returns {Array} - Formatted
 */
export const mapToSidebarFormat = (courses) => {
  if (!Array.isArray(courses)) return [];

  return courses.map(course => ({
    id: course.id,
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
  if (!Array.isArray(courses)) return [];

  return courses.map(course => ({
    id: course.id,
    name: course.name,
    class: course.schoolClassName,
    // Add dates, favorite and so on when/if implemented
  }));
};

/**
 * Maps to admin table format
 * TODO: Implementera när admin-panel ska byggas
 */
export const mapToTableRowFormat = (courses) => {
  if (!Array.isArray(courses)) return [];

  return courses.map(course => ({
    id: course.id,
    name: course.name,
    schoolClass: course.schoolClassName,
  }));
};