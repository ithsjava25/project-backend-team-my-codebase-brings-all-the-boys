"use client"

import * as React from "react"

import { NavMain } from "@/components/nav-main"
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
  SidebarRail,
} from "@/components/ui/sidebar"
import {BookOpenIcon, Shield, Users, Activity} from "lucide-react";
import {useMemo} from "react";

export function AppSidebar({ ...props }) {
  const { user } = useAuthContext()
  const { courses, error } = useCourses();
  const location = useLocation();

  const showHomeButton = useMemo(() => {
    const path = location.pathname;

    if (path !== '/dashboard') {
      return <NavHome/>
    }
    return null
  }, [location.pathname])

  const navItems = useMemo(() => {
    const items = [{
      title: "Kurser",
      url: "/dashboard",
      icon: BookOpenIcon,
      isActive: true,
      items: courses.map(course => ({
        title: course.name,
        url: `/courses/${course.id}`,
        items: [
          { title: "Uppgifter", url: `/courses/${course.id}?tab=assignments` },
          { title: "Deltagare", url: `/courses/${course.id}?tab=participants` },
        ]
      }))
    }, {
      title: "Klasser",
      url: "/dashboard?tab=classes",
      icon: Users,
      items: []
    }];

    if (user?.role?.name === 'ROLE_ADMIN') {
      items.push({
        title: "Administration",
        url: "/admin/users",
        icon: Shield,
        isActive: location.pathname.startsWith('/admin'),
        items: [
          { title: "Användare", url: "/admin/users" },
          { title: "Kurser", url: "/admin/courses" },
          { title: "Klasser", url: "/admin/school-classes" },
          { title: "Aktivitetslogg", url: "/dashboard?tab=activity" },
        ]
      });
    } else {
      // Add activity log as a top-level item for non-admins
      items.push({
          title: "Min Aktivitet",
          url: "/dashboard?tab=activity",
          icon: Activity,
          items: []
      });
    }

    return items;
  }, [courses, user, location.pathname]);

  // TODO: replace with proper error handling
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