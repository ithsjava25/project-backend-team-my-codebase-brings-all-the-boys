import {useState, useEffect, useCallback} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {schoolClassApi} from '@/api/schoolClasses';
import {userApi} from '@/api/users';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Input} from '@/components/ui/input';
import {Label} from '@/components/ui/label';
import {Textarea} from '@/components/ui/textarea';
import {Tabs, TabsContent, TabsList, TabsTrigger} from '@/components/ui/tabs';
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from '@/components/ui/table';
import {Plus, Trash2, Search} from 'lucide-react';

export default function SchoolClassEditPage() {
    const {id} = useParams();
    const navigate = useNavigate();
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [form, setForm] = useState(null);
    const [enrollments, setEnrollments] = useState([]);
    const [allStudents, setAllStudents] = useState([]);
    const [studentSearch, setStudentSearch] = useState('');

    const fetchData = useCallback(async () => {
        try {
            setLoading(true);
            const [classData, students] = await Promise.all([
                schoolClassApi.getSchoolClassById(id),
                userApi.getStudents()
            ]);
            
            setForm({
                name: classData.name || '',
                description: classData.description || ''
            });
            setEnrollments(classData.enrollments || []);
            setAllStudents(students || []);
        } catch (err) {
            console.error('Failed to fetch class:', err);
            setError('Kunde inte hämta data.');
        } finally {
            setLoading(false);
        }
    }, [id]);

    useEffect(() => {
        if (id) fetchData();
    }, [id, fetchData]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (isSubmitting || !form) return;
        setIsSubmitting(true);
        setError(null);
        try {
            await schoolClassApi.updateSchoolClass(id, form);
            window.dispatchEvent(new CustomEvent('courses-changed'));
            alert('Klassen har uppdaterats!');
            navigate('/admin/school-classes');
        } catch (err) {
            console.error('Failed to update class:', err);
            setError(err.response?.data?.message || 'Kunde inte uppdatera klass.');
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleEnroll = async (studentId) => {
        try {
            await schoolClassApi.enrollUser(id, studentId, 'STUDENT');
            fetchData();
        } catch (err) {
            alert('Kunde inte lägga till student: ' + (err.response?.data?.message || err.message));
        }
    };

    const handleRemove = async (studentId) => {
        if (!window.confirm('Är du säker på att du vill ta bort studenten från klassen?')) return;
        try {
            await schoolClassApi.removeEnrollment(id, studentId);
            fetchData();
        } catch (err) {
            alert('Kunde inte ta bort student: ' + (err.response?.data?.message || err.message));
        }
    };

    const enrolledStudentIds = enrollments.map(e => e.user?.id);
    const availableStudents = allStudents.filter(s => 
        !enrolledStudentIds.includes(s.id) && 
        (s.username.toLowerCase().includes(studentSearch.toLowerCase()) || 
         s.email.toLowerCase().includes(studentSearch.toLowerCase()))
    );

    if (loading) return <div className="p-8">Laddar...</div>;
    if (error && !form) return <div className="p-8 text-destructive">Fel: {error}</div>;
    if (!form) return <div className="p-8">Ingen data hittades.</div>;

    return (
        <div className="p-8 space-y-6">
            <h1 className="text-3xl font-bold">Redigera Klass</h1>
            
            <Tabs defaultValue="general">
                <TabsList>
                    <TabsTrigger value="general">Allmänt</TabsTrigger>
                    <TabsTrigger value="students">Studenter ({enrollments.filter(e => e.classRole === 'STUDENT').length})</TabsTrigger>
                </TabsList>

                <TabsContent value="general" className="mt-4">
                    <Card>
                        <CardHeader>
                            <CardTitle>Klassinformation</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <form onSubmit={handleSubmit} className="space-y-4">
                                <div>
                                    <Label htmlFor="class-name">Klassnamn</Label>
                                    <Input
                                        id="class-name"
                                        name="name"
                                        required
                                        placeholder="T.ex. TE21A"
                                        value={form.name}
                                        onChange={e => setForm({...form, name: e.target.value})}
                                    />
                                </div>

                                <div>
                                    <Label htmlFor="class-description">Beskrivning</Label>
                                    <Textarea
                                        id="class-description"
                                        name="description"
                                        placeholder="Klassens beskrivning..."
                                        value={form.description}
                                        onChange={e => setForm({...form, description: e.target.value})}
                                    />
                                </div>

                                {error && <p className="text-destructive">Fel: {error}</p>}

                                <div className="flex justify-end gap-2">
                                    <Button type="submit" disabled={isSubmitting}>
                                        {isSubmitting ? 'Sparar...' : 'Spara ändringar'}
                                    </Button>
                                    <Button type="button" variant="outline" onClick={() => navigate('/admin/school-classes')}>
                                        Avbryt
                                    </Button>
                                </div>
                            </form>
                        </CardContent>
                    </Card>
                </TabsContent>

                <TabsContent value="students" className="mt-4 space-y-4">
                    <Card>
                        <CardHeader>
                            <CardTitle>Hantera Studenter</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-6">
                            <div className="space-y-2">
                                <Label>Lägg till student</Label>
                                <div className="flex gap-2">
                                    <div className="relative flex-1">
                                        <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                                        <Input 
                                            placeholder="Sök efter student..." 
                                            className="pl-8"
                                            value={studentSearch}
                                            onChange={e => setStudentSearch(e.target.value)}
                                        />
                                    </div>
                                </div>
                                {studentSearch && availableStudents.length > 0 && (
                                    <div className="mt-2 border rounded-md max-h-48 overflow-auto">
                                        {availableStudents.map(student => (
                                            <div key={student.id} className="flex items-center justify-between p-2 hover:bg-muted">
                                                <div className="text-sm">
                                                    <p className="font-medium">{student.username}</p>
                                                    <p className="text-xs text-muted-foreground">{student.email}</p>
                                                </div>
                                                <Button size="sm" variant="ghost" onClick={() => handleEnroll(student.id)}>
                                                    <Plus className="h-4 w-4" />
                                                </Button>
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </div>

                            <div className="border-t pt-4">
                                <Label>Nuvarande studenter</Label>
                                <Table>
                                    <TableHeader>
                                        <TableRow>
                                            <TableHead>Namn</TableHead>
                                            <TableHead>E-post</TableHead>
                                            <TableHead className="text-right">Åtgärd</TableHead>
                                        </TableRow>
                                    </TableHeader>
                                    <TableBody>
                                        {enrollments.filter(e => e.classRole === 'STUDENT').map((e) => (
                                            <TableRow key={e.id}>
                                                <TableCell className="font-medium">{e.user?.username}</TableCell>
                                                <TableCell>{e.user?.email}</TableCell>
                                                <TableCell className="text-right">
                                                    <Button variant="ghost" size="icon" onClick={() => handleRemove(e.user?.id)}>
                                                        <Trash2 className="h-4 w-4 text-destructive" />
                                                    </Button>
                                                </TableCell>
                                            </TableRow>
                                        ))}
                                        {enrollments.filter(e => e.classRole === 'STUDENT').length === 0 && (
                                            <TableRow>
                                                <TableCell colSpan={3} className="text-center py-4 text-muted-foreground italic">
                                                    Inga studenter i den här klassen än.
                                                </TableCell>
                                            </TableRow>
                                        )}
                                    </TableBody>
                                </Table>
                            </div>
                        </CardContent>
                    </Card>
                </TabsContent>
            </Tabs>
        </div>
    );
}
