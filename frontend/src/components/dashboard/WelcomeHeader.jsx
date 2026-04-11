import {UserRoundIcon} from "lucide-react";

export default function WelcomeHeader({ user }) {
  const roleName = {
    'ROLE_ADMIN': 'Admin',
    'ROLE_TEACHER': 'Lärare',
    'ROLE_STUDENT': 'Student'
  }[user?.role?.name] || 'Användare';

  return (
    <div className="flex gap-1 items-center">
      <p className="text-muted-foreground mr-1">|</p>
      <p className="flex gap-2 text- text-lg font-semibold"><UserRoundIcon/> {user?.username}</p>
      <p className="text-muted-foreground font-normal">({roleName})</p>
    </div>
  );
}