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
    useSidebar,
} from "@/components/ui/sidebar"

import {useNavigate, Link} from 'react-router-dom';
import {useAuthContext} from "@/context/AuthContext.jsx";

function UserDisplay({user, wrapperClassName, linkToProfile = false}) {
    const content = (
        <>
            <Avatar className="h-8 w-8">
                <AvatarFallback>
                    <UserRoundIcon className="h-4 w-4"/>
                </AvatarFallback>
            </Avatar>
            <div className="grid flex-1 text-sm">
        <span className="truncate font-medium">
          {user.username}
        </span>
                <span className="truncate text-xs text-muted-foreground">
          {user.email}
        </span>
            </div>
        </>
    );

    if (linkToProfile) {
        return (
            <Link to={`/profile/${user.id}`} className={`${wrapperClassName} hover:opacity-80 transition-opacity`}>
                {content}
            </Link>
        );
    }

    return (
        <div className={wrapperClassName}>
            {content}
        </div>
    )
}

export function NavUser({user}) {
    const {isMobile} = useSidebar()
    const navigate = useNavigate();
    const {logout} = useAuthContext();  // ← NY

    const handleLogout = async () => {
        try {
            await logout();
            navigate('/login');
        } catch (error) {
            console.error('Logout failed:', error);
            alert('Utloggning misslyckades. Försök igen.');
        }
    };

    return (
        <SidebarMenu>
            <SidebarMenuItem>
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <SidebarMenuButton
                            size="lg"
                            className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-muted-foreground"
                        >
                            <UserDisplay user={user} wrapperClassName="flex items-center gap-2"/>
                            <ChevronsUpDownIcon className="ml-auto size-4"/>
                        </SidebarMenuButton>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent
                        className="w-(--radix-dropdown-menu-trigger-width) min-w-56 rounded-lg"
                        side={isMobile ? "bottom" : "right"}
                        align="end"
                        sideOffset={4}
                    >
                        <DropdownMenuGroup>
                            <DropdownMenuItem onClick={() => navigate(`/profile/${user.id}`)}
                                              className="cursor-pointer">
                                <UserRoundIcon className="mr-2 h-4 w-4"/>
                                Min profil
                            </DropdownMenuItem>
                            <DropdownMenuItem onClick={handleLogout}
                                              className="cursor-pointer text-destructive focus:text-destructive">
                                <LogOutIcon className="mr-2 h-4 w-4"/>
                                Logga ut
                            </DropdownMenuItem>
                        </DropdownMenuGroup>
                    </DropdownMenuContent>
                </DropdownMenu>
            </SidebarMenuItem>
        </SidebarMenu>
    )
}