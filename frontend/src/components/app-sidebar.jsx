"use client"

import * as React from "react"

import { NavMain } from "@/components/nav-main"
import { NavFavorites } from "@/components/nav-favorites"
import { NavUser } from "@/components/nav-user"
import { NavHome } from "@/components/nav-home"
import { CourseSwitcher } from "@/components/course-switcher"
import { useAuthContext } from "@/context/AuthContext.jsx";
import {mapToSidebarFormat} from "@/mappers/courseMapper.js";
import { useCourses } from '@/hooks/useCourses';
import { useLocation } from 'react-router-dom';
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail, SidebarSeparator,
} from "@/components/ui/sidebar"
import {BookOpenIcon, FileTextIcon, LayersIcon} from "lucide-react";
import {useMemo} from "react";

export function AppSidebar({ ...props }) {
  const { user } = useAuthContext()
  const { courses, loading, error } = useCourses();
  const location = useLocation();

  const showHomeButton = useMemo(() => {
    const path = location.pathname;

    if (path !== '/dashboard') {
      return <NavHome/>
    }
    return null
  })

  const navItems = useMemo(() => {
    return [{
      title: "Kurser",
      url: "/dashboard",
      icon: BookOpenIcon,
      isActive: true,
      items: courses.map(course => ({
        title: course.name,
        url: `/courses/${course.id}`
      }))
    }];
  }, [courses]);

  // TODO: replace with proper error handling
  if (loading) return <Sidebar collapsible="icon" {...props}>Laddar kurser...</Sidebar>;
  if (error) return <Sidebar collapsible="icon" {...props}>Ett fel uppstod: {error}</Sidebar>;

  // Map backend data to sidebar format
  const sidebarCourses = mapToSidebarFormat(courses);

  return (
    <Sidebar collapsible="icon" {...props}>
      <SidebarHeader>
        <CourseSwitcher courses={sidebarCourses} user={user} />
      </SidebarHeader>
      <SidebarContent>
        {showHomeButton && <NavHome/>}
        <NavMain items={navItems} />
      </SidebarContent>
      <SidebarFooter>
        <NavUser user={user} />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  )
}