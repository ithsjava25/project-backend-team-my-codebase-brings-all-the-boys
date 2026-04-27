import {useState, useEffect, useMemo} from 'react';
import {useParams, useNavigate, Link} from 'react-router-dom';
import {schoolClassApi} from '@/api/schoolClasses';
import {userApi} from '@/api/users';
import {useAuthContext} from '@/context/AuthContext';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Button} from '@/components/ui/button';
import {ArrowLeft, Users, BookOpen, UserPlus, Trash2} from 'lucide-react';
import {Tabs, TabsContent, TabsList, TabsTrigger} from '@/components/ui/tabs';
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from '@/components/ui/table';
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";

export default function SchoolClassDetailPage() {
    const {id} = useParams();
    const navigate = useNavigate();
    const {user: currentUser} = useAuthContext();
    const [sc, setSc] = useState(null);
    const [allStudents, setAllStudents] = useState([]);
    const [allTeachers, setAllTeachers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [selectedUser, setSelectedUser] = useState('');
    const [selectedRole, setSelectedRole] = useState('STUDENT');
    const [isEnrolling, setIsEnrolling] = useState(false);

    const isAdmin = currentUser?.role?.name === 'ROLE_ADMIN';
    const isTeacher = currentUser?.role?.name === 'ROLE_TEACHER';

    const allParticipants = useMemo(() => {
        if (!sc) return [];
        const teachers = sc.teachers || [];
        const students = sc.students || [];
        
        const map = new Map();
        
        teachers.forEach(t => {
            map.set(t.id, { ...t, displayRole: 'Lärare/Mentor' });
        });
        
        students.forEach(s => {
            if (!map.has(s.id)) {
                map.set(s.id, { ...s, displayRole: 'Elev' });
            }
        });
        
        return Array.from(map.values());
    }, [sc]);

    const fetchDetails = async () => {
        try {
            setLoading(true);
            const data = await schoolClassApi.getSchoolClassById(id);
            setSc(data);

            if (isAdmin) {
                const [students, teachers] = await Promise.all([
                    userApi.getStudents(),
                    userApi.getTeachers()
                ]);
                setAllStudents(students);
                setAllTeachers(teachers);
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Kunde inte hämta klassdetaljer.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchDetails();
    }, [id, isAdmin]);

    const handleEnroll = async () => {
        if (!selectedUser || isEnrolling) return;

        setIsEnrolling(true);
        try {
            await schoolClassApi.enrollUser(id, selectedUser, selectedRole);
            setSelectedUser('');
            await fetchDetails();
        } catch (err) {
            console.error('Enrollment failed:', err);
            alert('Kunde inte lägga till användaren i klassen.');
        } finally {
            setIsEnrolling(false);
        }
    };

    const handleRemove = async (userId) => {
        if (!confirm('Är du säker på att du vill ta bort användaren från klassen?')) return;

        try {
            await schoolClassApi.removeEnrollment(id, userId);
            await fetchDetails();
        } catch (err) {
            if (err.response?.status === 501) {
                alert('Borttagning är inte implementerad i backend än.');
            } else {
                console.error('Removal failed:', err);
                alert('Kunde inte ta bort användaren.');
            }
        }
    };

    // Filter out users already in class
    const availableUsers = useMemo(() => {
        const enrolledIds = new Set([
            ...(sc?.students?.map(s => s.id) || []),
            ...(sc?.teachers?.map(t => t.id) || [])
        ]);

        const list = selectedRole === 'STUDENT' ? allStudents : allTeachers;
        return list.filter(u => !enrolledIds.has(u.id));
    }, [sc, allStudents, allTeachers, selectedRole]);

    if (loading) return <div className="p-8">Laddar klassdetaljer...</div>;
    if (error) return <div className="p-8 text-destructive">Fel: {error}</div>;
    if (!sc) return <div className="p-8">Klassen hittades inte.</div>;

    return (
        <div className="space-y-6">
            <Button 
                variant="ghost" 
                onClick={() => navigate(-1)} 
                className="gap-2"
            >
                <ArrowLeft className="h-4 w-4"/>
                Tillbaka
            </Button>

            <div className="flex flex-col gap-2">
                <h1 className="text-3xl font-bold">{sc.name}</h1>
                <p className="text-muted-foreground">{sc.description}</p>
            </div>

            <Tabs defaultValue="courses">
                <TabsList>
                    <TabsTrigger value="courses" className="gap-2">
                        <BookOpen className="h-4 w-4"/>
                        Kurser
                    </TabsTrigger>
                    <TabsTrigger value="participants" className="gap-2">
                        <Users className="h-4 w-4"/>
                        Deltagare
                    </TabsTrigger>
                    {isAdmin && (
                        <TabsTrigger value="management" className="gap-2">
                            <UserPlus className="h-4 w-4"/>
                            Hantera
                        </TabsTrigger>
                    )}
                </TabsList>

                <TabsContent value="courses" className="mt-4">
                    <Card>
                        <CardHeader>
                            <CardTitle>Kurser i denna klass</CardTitle>
                        </CardHeader>
                        <CardContent>
                            {sc.courses?.length === 0 ? (
                                <p>Inga kurser än.</p>
                            ) : (
                                <Table>
                                    <TableHeader>
                                        <TableRow>
                                            <TableHead>Namn</TableHead>
                                            <TableHead>Lärare</TableHead>
                                        </TableRow>
                                    </TableHeader>
                                    <TableBody>
                                        {sc.courses?.map((course) => (
                                            <TableRow 
                                                key={course.id}
                                                className="cursor-pointer hover:bg-muted/50"
                                                onClick={() => navigate(`/courses/${course.id}`)}
                                            >
                                                <TableCell className="font-medium">{course.name}</TableCell>
                                                <TableCell>
                                                    {course.leadTeacher ? (
                                                        <Link 
                                                            to={`/profile/${course.leadTeacher.id}`}
                                                            className="hover:underline text-primary"
                                                            onClick={(e) => e.stopPropagation()}
                                                        >
                                                            {course.leadTeacher.username}
                                                        </Link>
                                                    ) : '-'}
                                                </TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            )}
                        </CardContent>
                    </Card>
                </TabsContent>

                <TabsContent value="participants" className="mt-4">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <Card>
                            <CardHeader>
                                <CardTitle>Lärare & Mentorer</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <Table>
                                    <TableBody>
                                        {sc.teachers?.map((t) => (
                                            <TableRow key={t.id}>
                                                <TableCell className="font-medium">
                                                    <Link to={`/profile/${t.id}`} className="hover:underline text-primary">
                                                        {t.username}
                                                    </Link>
                                                </TableCell>
                                                <TableCell className="text-right text-muted-foreground">{t.email}</TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </CardContent>
                        </Card>

                        <Card>
                            <CardHeader>
                                <CardTitle>Elever</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <Table>
                                    <TableBody>
                                        {sc.students?.map((s) => (
                                            <TableRow key={s.id}>
                                                <TableCell className="font-medium">
                                                    <Link to={`/profile/${s.id}`} className="hover:underline text-primary">
                                                        {s.username}
                                                    </Link>
                                                </TableCell>
                                                <TableCell className="text-right text-muted-foreground">{s.email}</TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </CardContent>
                        </Card>
                    </div>
                </TabsContent>

                {isAdmin && (
                    <TabsContent value="management" className="mt-4">
                        <Card>
                            <CardHeader>
                                <CardTitle>Lägg till deltagare</CardTitle>
                            </CardHeader>
                            <CardContent className="space-y-4">
                                <div className="flex gap-4 items-end">
                                    <div className="space-y-2 flex-1">
                                        <label className="text-sm font-medium">Roll</label>
                                        <Select value={selectedRole} onValueChange={setSelectedRole}>
                                            <SelectTrigger>
                                                <SelectValue />
                                            </SelectTrigger>
                                            <SelectContent>
                                                <SelectItem value="STUDENT">Elev</SelectItem>
                                                <SelectItem value="TEACHER">Lärare</SelectItem>
                                                <SelectItem value="MENTOR">Mentor</SelectItem>
                                            </SelectContent>
                                        </Select>
                                    </div>
                                    <div className="space-y-2 flex-[2]">
                                        <label className="text-sm font-medium">Användare</label>
                                        <Select value={selectedUser} onValueChange={setSelectedUser}>
                                            <SelectTrigger>
                                                <SelectValue placeholder="Välj användare..." />
                                            </SelectTrigger>
                                            <SelectContent>
                                                {availableUsers.map(u => (
                                                    <SelectItem key={u.id} value={u.id}>{u.username} ({u.email})</SelectItem>
                                                ))}
                                            </SelectContent>
                                        </Select>
                                    </div>
                                    <Button onClick={handleEnroll} disabled={!selectedUser || isEnrolling}>
                                        Lägg till
                                    </Button>
                                </div>

                                <div className="pt-6">
                                    <h3 className="text-lg font-medium mb-4 text-destructive">Ta bort deltagare</h3>
                                    <Table>
                                        <TableHeader>
                                            <TableRow>
                                                <TableHead>Namn</TableHead>
                                                <TableHead>Roll</TableHead>
                                                <TableHead className="text-right">Åtgärd</TableHead>
                                            </TableRow>
                                        </TableHeader>
                                        <TableBody>
                                            {allParticipants.map(u => (
                                                <TableRow key={u.id}>
                                                    <TableCell>{u.username}</TableCell>
                                                    <TableCell>{u.displayRole}</TableCell>
                                                    <TableCell className="text-right">
                                                        <Button 
                                                            variant="ghost" 
                                                            size="icon" 
                                                            className="text-destructive"
                                                            onClick={() => handleRemove(u.id)}
                                                        >
                                                            <Trash2 className="h-4 w-4"/>
                                                        </Button>
                                                    </TableCell>
                                                </TableRow>
                                            ))}
                                        </TableBody>
                                    </Table>
                                </div>
                            </CardContent>
                        </Card>
                    </TabsContent>
                )}
            </Tabs>
        </div>
    );
}
