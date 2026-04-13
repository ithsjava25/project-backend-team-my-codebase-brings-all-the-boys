"use client"

import * as React from "react"

import { NavMain } from "@/components/nav-main"
import { NavFavorites } from "@/components/nav-favorites"
import { NavUser } from "@/components/nav-user"
import { CourseSwitcher } from "@/components/course-switcher"
import { useAuthContext } from "@/context/AuthContext.jsx";
import {mapToSidebarFormat} from "@/mappers/courseMapper.js";
import { useNavigationItems } from '@/hooks/useNavigationItems';
import { useCourses } from '@/hooks/useCourses';
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
} from "@/components/ui/sidebar"

export function AppSidebar({ ...props }) {
  const { user } = useAuthContext()
  const { courses, loading, error } = useCourses();
  const navItems = useNavigationItems();

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
        <NavMain items={navItems} />
        <NavFavorites favorites={[]} />
      </SidebarContent>
      <SidebarFooter>
        <NavUser user={user} />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  )
}