import {useState, useEffect} from 'react';
import {schoolClassApi} from '@/api/schoolClasses';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from '@/components/ui/table';
import {useNavigate} from 'react-router-dom';

export default function SchoolClassListView() {
    const [classes, setClasses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchClasses = async () => {
            try {
                setLoading(true);
                const data = await schoolClassApi.getAllSchoolClasses();
                setClasses(data || []);
            } catch (err) {
                setError('Kunde inte hämta klasser.');
            } finally {
                setLoading(false);
            }
        };
        fetchClasses();
    }, []);

    if (loading) return <p>Laddar klasser...</p>;
    if (error) return <p className="text-destructive">{error}</p>;

    return (
        <Card>
            <CardHeader>
                <CardTitle>Mina Klasser</CardTitle>
            </CardHeader>
            <CardContent>
                {classes.length === 0 ? (
                    <p className="text-muted-foreground">Du är inte med i några klasser än.</p>
                ) : (
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Namn</TableHead>
                                <TableHead>Beskrivning</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {classes.map((sc) => (
                                <TableRow 
                                    key={sc.id} 
                                    className="cursor-pointer hover:bg-muted/50"
                                    onClick={() => navigate(`/school-classes/${sc.id}`)}
                                >
                                    <TableCell className="font-medium">{sc.name}</TableCell>
                                    <TableCell>{sc.description || '-'}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                )}
            </CardContent>
        </Card>
    );
}
