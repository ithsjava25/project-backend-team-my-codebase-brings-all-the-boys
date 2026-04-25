import {useState, useEffect} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {userApi} from '@/api/users';
import {schoolClassApi} from '@/api/schoolClasses';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Input} from '@/components/ui/input';
import {Label} from '@/components/ui/label';
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from '@/components/ui/select';
import {ArrowLeft} from 'lucide-react';
import {Link} from 'react-router-dom';

export default function UserEditPage() {
    const {id} = useParams();
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(true);
    const [isSaving, setIsSaving] = useState(false);
    const [allClasses, setAllClasses] = useState([]);
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        roleName: '',
        password: '',
        schoolClassIds: []
    });

    useEffect(() => {
        const controller = new AbortController();
        const fetchData = async () => {
            try {
                setIsLoading(true);
                const [user, classes, profile] = await Promise.all([
                    userApi.getUserById(id, controller.signal),
                    schoolClassApi.getAllSchoolClasses(controller.signal),
                    userApi.getUserProfile(id, controller.signal)
                ]);
                
                if (controller.signal.aborted) return;

                // Handle Spring Data Page object or array
                const classesContent = classes.content !== undefined ? classes.content : classes;
                setAllClasses(Array.isArray(classesContent) ? classesContent : []);
                
                const currentClassIds = profile.classes?.map(c => c.id) || [];

                setFormData({
                    username: user.username || '',
                    email: user.email || '',
                    roleName: user.role?.name || 'ROLE_STUDENT',
                    password: '',
                    schoolClassIds: currentClassIds
                });
            } catch (err) {
                if (controller.signal.aborted) return;
                console.error('Failed to fetch data:', err);
                alert(err.response?.data?.message || 'Kunde inte hämta data.');
                navigate('/admin/users');
            } finally {
                if (!controller.signal.aborted) {
                    setIsLoading(false);
                }
            }
        };
        fetchData();
        return () => controller.abort();
    }, [id, navigate]);

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData(prev => ({...prev, [name]: value}));
    };

    const handleRoleChange = (value) => {
        setFormData(prev => ({...prev, roleName: value}));
    };

    const handleClassToggle = (classId) => {
        setFormData(prev => {
            const ids = [...prev.schoolClassIds];
            if (ids.includes(classId)) {
                return {...prev, schoolClassIds: ids.filter(id => id !== classId)};
            } else {
                return {...prev, schoolClassIds: [...ids, classId]};
            }
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSaving(true);
        try {
            const payload = {...formData};
            if (!payload.password?.trim()) {
                delete payload.password;
            }
            await userApi.updateUser(id, payload);
            navigate('/admin/users');
        } catch (err) {
            alert(err.response?.data?.message || 'Kunde inte uppdatera användare.');
        } finally {
            setIsSaving(false);
        }
    };

    if (isLoading) return <div className="p-8 text-center">Laddar användardata...</div>;

    return (
        <div className="max-w-2xl mx-auto space-y-6">
            <Link to="/admin/users"
                  className="inline-flex items-center text-sm text-muted-foreground hover:text-foreground">
                <ArrowLeft className="mr-2 h-4 w-4"/>
                Tillbaka till användarlista
            </Link>

            <Card>
                <CardHeader>
                    <CardTitle>Redigera användare</CardTitle>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div className="space-y-4">
                            <div className="space-y-2">
                                <Label htmlFor="username">Användarnamn</Label>
                                <Input
                                    id="username"
                                    name="username"
                                    value={formData.username}
                                    onChange={handleChange}
                                    required
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="email">E-post</Label>
                                <Input
                                    id="email"
                                    name="email"
                                    type="email"
                                    value={formData.email}
                                    onChange={handleChange}
                                    required
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="role">Roll</Label>
                                <Select value={formData.roleName} onValueChange={handleRoleChange}>
                                    <SelectTrigger>
                                        <SelectValue placeholder="Välj roll"/>
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="ROLE_ADMIN">Administratör</SelectItem>
                                        <SelectItem value="ROLE_TEACHER">Lärare</SelectItem>
                                        <SelectItem value="ROLE_STUDENT">Student</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>

                            <div className="space-y-4 border-t pt-4">
                                <Label>Klasser</Label>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    {allClasses.map((cls) => (
                                        <div key={cls.id} className="flex items-center space-x-2">
                                            <input 
                                                type="checkbox"
                                                id={`class-${cls.id}`} 
                                                className="h-4 w-4 rounded border-gray-300 text-primary focus:ring-primary"
                                                checked={formData.schoolClassIds.includes(cls.id)}
                                                onChange={() => handleClassToggle(cls.id)}
                                            />
                                            <Label 
                                                htmlFor={`class-${cls.id}`}
                                                className="text-sm font-normal cursor-pointer"
                                            >
                                                {cls.name}
                                            </Label>
                                        </div>
                                    ))}
                                </div>
                            </div>

                            <div className="space-y-2 border-t pt-4">
                                <Label htmlFor="password">Lösenord (lämna tomt för att behålla nuvarande)</Label>
                                <Input
                                    id="password"
                                    name="password"
                                    type="password"
                                    value={formData.password}
                                    onChange={handleChange}
                                    placeholder="Nytt lösenord..."
                                />
                            </div>
                        </div>

                        <div className="flex gap-4">
                            <Button type="submit" disabled={isSaving}>
                                {isSaving ? 'Sparar...' : 'Spara ändringar'}
                            </Button>
                            <Button type="button" variant="outline" onClick={() => navigate('/admin/users')}>
                                Avbryt
                            </Button>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
}