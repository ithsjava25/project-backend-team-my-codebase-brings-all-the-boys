import {useState, useEffect} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {userApi} from '@/api/users';
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
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        roleName: '',
        password: ''
    });

    useEffect(() => {
        const fetchUser = async () => {
            try {
                setIsLoading(true);
                const user = await userApi.getUserById(id);
                setFormData({
                    username: user.username || '',
                    email: user.email || '',
                    roleName: user.role?.name || 'ROLE_STUDENT',
                    password: '' // Don't fetch password
                });
            } catch (err) {
                alert(err.response?.data?.message || 'Kunde inte hämta användare.');
                navigate('/admin/users');
            } finally {
                setIsLoading(false);
            }
        };
        fetchUser();
    }, [id, navigate]);

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData(prev => ({...prev, [name]: value}));
    };

    const handleRoleChange = (value) => {
        setFormData(prev => ({...prev, roleName: value}));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSaving(true);
        try {
            // Backend expects UserRequest: { username, email, roleName, password? }
            await userApi.updateUser(id, formData);
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
            <Link to="/admin/users" className="inline-flex items-center text-sm text-muted-foreground hover:text-foreground">
                <ArrowLeft className="mr-2 h-4 w-4" />
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
                                        <SelectValue placeholder="Välj roll" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="ROLE_ADMIN">Administratör</SelectItem>
                                        <SelectItem value="ROLE_TEACHER">Lärare</SelectItem>
                                        <SelectItem value="ROLE_STUDENT">Student</SelectItem>
                                    </SelectContent>
                                </Select>
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