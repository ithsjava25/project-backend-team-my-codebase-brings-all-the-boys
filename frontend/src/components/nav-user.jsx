"use client"

import {
  ChevronsUpDownIcon,
  LogOutIcon,
  UserRoundIcon,
} from "lucide-react"

import {
  Avatar,
  AvatarFallback,
} from "@/components/ui/avatar"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuSubItem,
  useSidebar,
} from "@/components/ui/sidebar"

function UserDisplay({ user, wrapperClassName }) {
  return (
    <SidebarMenuItem className={wrapperClassName}>
      <Avatar className="h-8 w-8">
        <AvatarFallback>
          <UserRoundIcon className="h-4 w-4"/>
        </AvatarFallback>
      </Avatar>
      <div className="grid flex-1 text-sm">
        <SidebarMenuSubItem className="truncate">
          {user.username}
        </SidebarMenuSubItem>
        <SidebarMenuSubItem className="truncate text-xs text-muted-foreground">
          {user.email}
        </SidebarMenuSubItem>
      </div>
    </SidebarMenuItem>
  )
}

export function NavUser({ user }) {
  const { isMobile } = useSidebar()

  return (
    <SidebarMenu>
      <SidebarMenuItem>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <SidebarMenuButton
              size="lg"
              className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-muted-foreground"
            >
              <UserDisplay user={user} wrapperClassName="flex items-center gap-2" />
              <ChevronsUpDownIcon className="ml-auto size-4" />
            </SidebarMenuButton>
          </DropdownMenuTrigger>
          <DropdownMenuContent
            className="w-(--radix-dropdown-menu-trigger-width) min-w-56 rounded-lg"
            side={isMobile ? "bottom" : "right"}
            align="end"
            sideOffset={4}
          >
              <UserDisplay user={user} wrapperClassName="flex items-center gap-2" />
            <DropdownMenuSeparator />
            <DropdownMenuGroup>
              <DropdownMenuItem>
                <LogOutIcon />
                Logga out
              </DropdownMenuItem>
            </DropdownMenuGroup>
          </DropdownMenuContent>
        </DropdownMenu>
      </SidebarMenuItem>
    </SidebarMenu>
  )
}