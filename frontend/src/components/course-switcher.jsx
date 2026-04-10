"use client"

import * as React from "react"
import { ChevronsUpDown, House, Shield } from "lucide-react"

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
  const [activeCourse, setActiveCourse] = React.useState(courses[0])

  if (!activeCourse) {
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
                <activeCourse.logo className="size-4" />
              </div>
              <div className="grid flex-1 text-left text-sm leading-tight">
                <span className="truncate font-medium">{activeCourse.name}</span>
                {activeCourse.description && (
                  <span className="truncate text-xs">{activeCourse.description}</span>
                )}
              </div>
              <ChevronsUpDown className="ml-auto" />
            </SidebarMenuButton>
          </DropdownMenuTrigger>
          <DropdownMenuContent
            className="w-(--radix-dropdown-menu-trigger-width) min-w-56 rounded-lg"
            align="start"
            side={isMobile ? "bottom" : "right"}
            sideOffset={4}
          >
            <DropdownMenuLabel className="text-xs text-muted-foreground">
              Navigation
            </DropdownMenuLabel>

            <DropdownMenuItem
              onClick={() => setActiveCourse({ name: 'Startsida', logo: House, url: '/dashboard' })}
              className="gap-2 p-2"
            >
              <div className="flex size-6 items-center justify-center rounded-md border bg-sidebar-accent">
                <House className="size-3.5 shrink-0" />
              </div>
              Startsida
            </DropdownMenuItem>

            <DropdownMenuSeparator />
            <DropdownMenuLabel>Kurser</DropdownMenuLabel>

            {courses.map((course, index) => (
              <DropdownMenuItem
                key={course.name}
                onClick={() => setActiveCourse(course)}
                className="gap-2 p-2"
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
                  onClick={() => setActiveCourse({ name: 'Admin', logo: Shield, url: '/admin' })}
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