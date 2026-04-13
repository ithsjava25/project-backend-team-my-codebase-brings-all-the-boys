"use client"

import {
  SidebarGroup,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from "@/components/ui/sidebar"
import {HomeIcon} from "lucide-react";

export function NavHome({ home }) {
  const { isMobile } = useSidebar()

  return (
    <SidebarGroup className="group-data-[collapsible=icon]:hidden">
      <SidebarGroupLabel>Startsida</SidebarGroupLabel>
      <SidebarMenu>
        <SidebarMenuItem>
          <HomeIcon className={"h-4 w-4"}/> Startsida
        </SidebarMenuItem>
      </SidebarMenu>

    </SidebarGroup>
  )
}