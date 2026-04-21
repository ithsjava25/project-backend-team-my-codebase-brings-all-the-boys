import {useState, useEffect} from 'react';
import {useParams, useNavigate} from 'react-router-dom';
import {schoolClassApi} from '@/api/schoolClasses';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Button} from '@/components/ui/button';
import {ArrowLeft, Users, BookOpen} from 'lucide-react';
import {Tabs, TabsContent, TabsList, TabsTrigger} from '@/components/ui/tabs';
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from '@/components/ui/table';

export default function SchoolClassDetailPage() {
    const {id} = useParams();
    const navigate = useNavigate();
    const [sc, setSc] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchDetails = async () => {
            try {
                setLoading(true);
                const data = await schoolClassApi.getSchoolClassById(id);
                setSc(data);
            } catch (err) {
                setError(err.response?.data?.message || 'Kunde inte hämta klassdetaljer.');
            } finally {
                setLoading(false);
            }
        };
        fetchDetails();
    }, [id]);

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
                                                <TableCell>{course.leadTeacher?.username || '-'}</TableCell>
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
                                                <TableCell className="font-medium">{t.username}</TableCell>
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
                                                <TableCell className="font-medium">{s.username}</TableCell>
                                                <TableCell className="text-right text-muted-foreground">{s.email}</TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </CardContent>
                        </Card>
                    </div>
                </TabsContent>
            </Tabs>
        </div>
    );
}
