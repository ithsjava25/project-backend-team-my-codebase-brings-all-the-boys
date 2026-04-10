import { Outlet } from 'react-router-dom';
import { AppSidebar } from '@/components/app-sidebar';
import {
  SidebarInset,
  SidebarProvider,
  SidebarTrigger,
} from '@/components/ui/sidebar';
import { Separator } from '@/components/ui/separator';
import WelcomeHeader from "@/components/dashboard/WelcomeHeader.jsx";
import {useAuthContext} from "@/context/AuthContext.jsx";

export default function DashboardLayout() {
  const { user } = useAuthContext();
  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>
        <header className="flex h-16 shrink-0 items-center gap-2 border-b">
          <div className="flex items-center gap-2 px-4">
            <SidebarTrigger className="-ml-1" />
            <WelcomeHeader user={user} />
          </div>
        </header>
        <div className="flex flex-1 flex-col gap-4 p-4">
          <div className="mx-auto w-full max-w-7xl">
            <Outlet />
          </div>
        </div>
      </SidebarInset>
    </SidebarProvider>
  );
}