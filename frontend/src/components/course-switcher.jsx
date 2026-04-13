"use client"

import * as React from "react"
import { ChevronsUpDown, House, Shield } from "lucide-react"
import { useNavigate } from 'react-router-dom';
import { useLocation } from 'react-router-dom';

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from "@/components/ui/sidebar"

export function CourseSwitcher({ courses, user }) {
  const { isMobile } = useSidebar()
  const navigate = useNavigate();
  const location = useLocation();

  const activeItem = React.useMemo(() => {
    const path = location.pathname;

    if (path === '/dashboard') {
      return { name: 'Startsida', logo: House };
    }

    if (path.startsWith('/courses/')) {
      const courseId = path.split('/courses/')[1]?.split('/')[0];
      const course = courses.find(c => c.id === courseId);
      if (course) return course;
    }

    if (path.startsWith('/admin')) {
      return { name: 'Admin', logo: Shield };
    }

    return courses[0];
  }, [location.pathname, courses]);

  if (!activeItem) {
    return null
  }

  // Check if user is admin
  const isAdmin = user?.role?.name === 'ROLE_ADMIN'

  return (
    <SidebarMenu>
      <SidebarMenuItem>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <SidebarMenuButton
              size="lg"
              className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
            >
              <div className="flex aspect-square size-8 items-center justify-center rounded-lg bg-sidebar-primary text-sidebar-primary-foreground">
                <activeItem.logo className="size-4" />
              </div>
              <div className="grid flex-1 text-left text-sm leading-tight">
                <span className="truncate font-medium">{activeItem.name}</span>
              </div>
              <ChevronsUpDown className="ml-auto" />
            </SidebarMenuButton>
          </DropdownMenuTrigger>
          <DropdownMenuContent
            className="w-auto rounded-lg"
            align="start"
            side={isMobile ? "bottom" : "right"}
            sideOffset={4}
          >
            <DropdownMenuLabel className="text-xs text-muted-foreground">
              Navigation
            </DropdownMenuLabel>

            <DropdownMenuItem
              onClick={() => {
                navigate('/dashboard');
              }}
              className="gap-2 p-2"
            >
              <div className="flex size-6 items-center justify-center rounded-md border bg-sidebar-accent">
                <House className="size-3.5 shrink-0" />
              </div>
              Startsida
            </DropdownMenuItem>

            <DropdownMenuSeparator />
            <DropdownMenuLabel>Kurser</DropdownMenuLabel>

            {courses.map((course) => (
              <DropdownMenuItem
                key={course.id}
                onClick={() => {navigate(course.url);}}
                className={"gap-2 p-2" + activeItem.id === course.id ? "bg-accent" : ""}
              >
                <div className="flex size-6 items-center justify-center rounded-md border">
                  <course.logo className="size-3.5 shrink-0" />
                </div>
                {course.name}
                {course.schoolClassName && (
                  <span className="ml-auto text-xs text-muted-foreground">
                    {course.schoolClassName}
                  </span>
                )}
              </DropdownMenuItem>
            ))}

            {isAdmin && (
              <>
                <DropdownMenuSeparator />
                <DropdownMenuItem
                  onClick={() => {
                    navigate('/admin');
                  }}
                  className="gap-2 p-2"
                >
                  <div className="flex size-6 items-center justify-center rounded-md border bg-red-500/10">
                    <Shield className="size-3.5 shrink-0 text-red-600" />
                  </div>
                  <div className="font-medium text-red-600">Admin</div>
                </DropdownMenuItem>
              </>
            )}
          </DropdownMenuContent>
        </DropdownMenu>
      </SidebarMenuItem>
    </SidebarMenu>
  )
}