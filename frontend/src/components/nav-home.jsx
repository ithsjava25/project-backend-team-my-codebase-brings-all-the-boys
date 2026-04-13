"use client"

import {
  SidebarGroup,
  SidebarMenuButton,
  useSidebar,
} from "@/components/ui/sidebar"
import {HomeIcon} from "lucide-react";

export function NavHome({ home }) {
  return (
    <SidebarGroup className="group-data-[collapsible=icon]:hidden">
      <SidebarMenuButton>
        <a href={"/dashboard"} className="flex items-center gap-2">
          <HomeIcon className={"h-4 w-4"}/> Startsida
        </a>
      </SidebarMenuButton>
    </SidebarGroup>
  )
}