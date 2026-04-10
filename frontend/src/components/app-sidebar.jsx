"use client"

import * as React from "react"
import {
  BookOpen,
  Bot,
  Frame,
  Map,
  PieChart,
  Settings2,
  SquareTerminal,
  Code,
  GraduationCap,
  Database
} from "lucide-react"

import { NavMain } from "@/components/nav-main"
import { NavFavorites } from "@/components/nav-favorites"
import { NavUser } from "@/components/nav-user"
import { CourseSwitcher } from "@/components/course-switcher"
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
} from "@/components/ui/sidebar"

// Demo data - hårdkodade kurser, byt till API
const coursesData = [
  {
    name: "Java Backend 1",
    logo: Code,
    schoolClassName: "TE24A",
    description: "Backend-utveckling med Java"
  },
  {
    name: "React Frontend 2",
    logo: Code,
    schoolClassName: "TE24B",
    description: "Frontend med React"
  },
  {
    name: "Databasteknik",
    logo: Database,
    schoolClassName: "TE24A",
    description: "SQL och NoSQL"
  },
  {
    name: "Webbutveckling",
    logo: GraduationCap,
    schoolClassName: "TE24C",
    description: "HTML, CSS, JavaScript"
  }
]

const data = {
  user: {
    name: "shadcn",
    email: "m@example.com",
    avatar: "/avatars/shadcn.jpg",
    role: { name: "ROLE_ADMIN" } // Testa med ROLE_STUDENT också
  },
  courses: coursesData,
  navMain: [
    {
      title: "Playground",
      url: "#",
      icon: SquareTerminal,
      isActive: true,
      items: [
        {
          title: "History",
          url: "#",
        },
        {
          title: "Starred",
          url: "#",
        },
        {
          title: "Settings",
          url: "#",
        },
      ],
    },
    {
      title: "Models",
      url: "#",
      icon: Bot,
      items: [
        {
          title: "Genesis",
          url: "#",
        },
        {
          title: "Explorer",
          url: "#",
        },
        {
          title: "Quantum",
          url: "#",
        },
      ],
    },
    {
      title: "Documentation",
      url: "#",
      icon: BookOpen,
      items: [
        {
          title: "Introduction",
          url: "#",
        },
        {
          title: "Get Started",
          url: "#",
        },
        {
          title: "Tutorials",
          url: "#",
        },
        {
          title: "Changelog",
          url: "#",
        },
      ],
    },
    {
      title: "Settings",
      url: "#",
      icon: Settings2,
      items: [
        {
          title: "General",
          url: "#",
        },
        {
          title: "Team",
          url: "#",
        },
        {
          title: "Billing",
          url: "#",
        },
        {
          title: "Limits",
          url: "#",
        },
      ],
    },
  ],
  favorites: [
    {
      name: "Java Backend 1",
      url: "/courses/java-backend-1",
      icon: Code
    },
    {
      name: "Databasteknik",
      url: "/courses/databases",
      icon: Database
    },
    {
      name: "React Frontend",
      url: "/courses/react-frontend",
      icon: BookOpen
    }
  ],
}

export function AppSidebar({ ...props }) {
  const { user } = data // TODO: Byt till useAuthContext() senare

  return (
    <Sidebar collapsible="icon" {...props}>
      <SidebarHeader>
        <CourseSwitcher courses={data.courses} user={user} />
      </SidebarHeader>
      <SidebarContent>
        <NavMain items={data.navMain} />
        <NavFavorites favorites={data.favorites} />
      </SidebarContent>
      <SidebarFooter>
        <NavUser user={data.user} />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  )
}