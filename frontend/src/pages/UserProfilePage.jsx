import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { userApi } from '@/api/users';
import { useAuthContext } from '@/context/AuthContext';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { ArrowLeft, User, Mail, Shield, BookOpen, GraduationCap, Edit } from 'lucide-react';
import { CourseCard } from '@/components/dashboard/CourseCard';

export default function UserProfilePage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user: currentUser } = useAuthContext();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await userApi.getUserProfile(id);
        setProfile(data);
      } catch (err) {
        console.error('Failed to fetch profile:', err);
        setError(err.response?.data?.message || 'Kunde inte hämta profil.');
      } finally {
        setLoading(false);
      }
    };

    if (id) fetchProfile();
  }, [id]);

  const isAdmin = currentUser?.role?.name === 'ROLE_ADMIN';
  const isOwnProfile = currentUser?.id === id;

  const getRoleLabel = (roleName) => {
    switch (roleName) {
      case 'ROLE_ADMIN': return 'Administratör';
      case 'ROLE_TEACHER': return 'Lärare';
      case 'ROLE_STUDENT': return 'Student';
      default: return roleName;
    }
  };

  if (loading) return <div className="p-8 text-center">Laddar profil...</div>;
  
  if (error) {
    return (
      <div className="p-8 text-center">
        <p className="text-destructive mb-4">{error}</p>
        <Button onClick={() => navigate(-1)}>Gå tillbaka</Button>
      </div>
    );
  }

  if (!profile) return <div className="p-8 text-center">Profilen hittades inte.</div>;

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <Button variant="ghost" onClick={() => navigate(-1)} className="gap-2">
          <ArrowLeft className="h-4 w-4" />
          Tillbaka
        </Button>
        {isAdmin && (
          <Button onClick={() => navigate(`/admin/users/${id}/edit`)} className="gap-2">
            <Edit className="h-4 w-4" />
            Redigera användare
          </Button>
        )}
      </div>

      <Card className="overflow-hidden">
        <div className="h-32 bg-gradient-to-r from-primary/20 to-primary/5" />
        <CardContent className="relative pt-0">
          <div className="flex flex-col md:flex-row gap-6 -mt-12 items-start md:items-end mb-6">
            <div className="h-24 w-24 rounded-2xl bg-background border-4 border-background shadow-xl flex items-center justify-center">
              <User className="h-12 w-12 text-muted-foreground" />
            </div>
            <div className="space-y-1 pb-1">
              <h1 className="text-3xl font-bold">{profile.username}</h1>
              <div className="flex flex-wrap gap-2">
                <Badge variant="secondary" className="gap-1">
                  <Shield className="h-3 w-3" />
                  {getRoleLabel(profile.role?.name)}
                </Badge>
                <Badge variant="outline" className="gap-1">
                  <Mail className="h-3 w-3" />
                  {profile.email}
                </Badge>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pt-6 border-t">
            <div className="space-y-4">
              <div className="flex items-center gap-2 font-semibold">
                <GraduationCap className="h-5 w-5 text-primary" />
                <h2>Klasser</h2>
              </div>
              {profile.classes?.length > 0 ? (
                <div className="grid grid-cols-1 gap-2">
                  {profile.classes.map(cls => (
                    <Link key={cls.id} to={`/school-classes/${cls.id}`}>
                      <div className="p-3 rounded-lg border hover:bg-muted/50 transition-colors">
                        <p className="font-medium">{cls.name}</p>
                        <p className="text-xs text-muted-foreground">{cls.description}</p>
                      </div>
                    </Link>
                  ))}
                </div>
              ) : (
                <p className="text-sm text-muted-foreground italic">Inte med i några klasser än.</p>
              )}
            </div>

            <div className="space-y-4">
              <div className="flex items-center gap-2 font-semibold">
                <BookOpen className="h-5 w-5 text-primary" />
                <h2>Kurser</h2>
              </div>
              {profile.courses?.length > 0 ? (
                <div className="grid grid-cols-1 gap-4">
                  {profile.courses.map(course => (
                    <CourseCard key={course.id} course={course} />
                  ))}
                </div>
              ) : (
                <p className="text-sm text-muted-foreground italic">Inte anmäld till några kurser än.</p>
              )}
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
