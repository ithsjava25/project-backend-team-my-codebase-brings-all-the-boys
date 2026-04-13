"use client"

import {
  SidebarGroup,
  SidebarMenuButton
} from "@/components/ui/sidebar"
import {HomeIcon} from "lucide-react";
import {Link} from "react-router-dom";

export function NavHome() {
  return (
    <SidebarGroup className="group-data-[collapsible=icon]:hidden">
      <SidebarMenuButton>
        <Link to={"/dashboard"} className="flex items-center gap-2">
          <HomeIcon className={"h-4 w-4"}/> Startsida
        </Link>
      </SidebarMenuButton>
    </SidebarGroup>
  )
}