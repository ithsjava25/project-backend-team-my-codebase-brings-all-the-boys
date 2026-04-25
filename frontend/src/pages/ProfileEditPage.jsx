import {useState, useEffect} from 'react';
import {useNavigate} from 'react-router-dom';
import {userApi} from '@/api/users';
import {useAuthContext} from '@/context/AuthContext';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Input} from '@/components/ui/input';
import {Label} from '@/components/ui/label';
import {Alert, AlertDescription} from '@/components/ui/alert';
import {ArrowLeft, Save} from 'lucide-react';

export default function ProfileEditPage() {
    const {user: currentUser, refreshUser} = useAuthContext();
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '' // Optional for profile update
    });

    useEffect(() => {
        if (currentUser) {
            setFormData({
                username: currentUser.username || '',
                email: currentUser.email || '',
                password: ''
            });
            setIsLoading(false);
        }
    }, [currentUser]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);
        setError(null);
        setSuccess(null);

        try {
            // In a real app, we might have a dedicated /api/users/profile endpoint
            // For now, we'll assume admins use the admin update, and users might need a specific one
            // If there's no dedicated profile update, we might need to use the admin one but that requires ROLE_ADMIN
            // Let's check if there's a more appropriate endpoint or if we need to add one.
            
            // Assuming for now we use the general updateUser if permitted or a future profile endpoint
            await userApi.updateUserProfile(formData);
            
            setSuccess('Din profil har uppdaterats!');
            if (refreshUser) await refreshUser();
        } catch (err) {
            console.error('Failed to update profile:', err);
            setError(err.response?.data?.message || 'Kunde inte uppdatera profilen.');
        } finally {
            setIsSubmitting(false);
        }
    };

    if (isLoading) return <div className="p-8 text-center">Laddar...</div>;

    return (
        <div className="max-w-2xl mx-auto space-y-6">
            <div className="flex items-center justify-between">
                <Button variant="ghost" onClick={() => navigate(-1)} className="gap-2">
                    <ArrowLeft className="h-4 w-4" />
                    Tillbaka
                </Button>
                <h1 className="text-3xl font-bold">Redigera din profil</h1>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>Profilinformation</CardTitle>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit} className="space-y-6">
                        {error && (
                            <Alert variant="destructive">
                                <AlertDescription>{error}</AlertDescription>
                            </Alert>
                        )}
                        {success && (
                            <Alert className="bg-green-50 border-green-200 text-green-800">
                                <AlertDescription>{success}</AlertDescription>
                            </Alert>
                        )}

                        <div className="space-y-2">
                            <Label htmlFor="username">Användarnamn</Label>
                            <Input
                                id="username"
                                value={formData.username}
                                onChange={e => setFormData({...formData, username: e.target.value})}
                                required
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="email">E-post</Label>
                            <Input
                                id="email"
                                type="email"
                                value={formData.email}
                                onChange={e => setFormData({...formData, email: e.target.value})}
                                required
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="password">Lösenord (lämna tomt för att behålla nuvarande)</Label>
                            <Input
                                id="password"
                                type="password"
                                value={formData.password}
                                onChange={e => setFormData({...formData, password: e.target.value})}
                                placeholder="Nytt lösenord..."
                            />
                        </div>

                        <div className="flex justify-end gap-3">
                            <Button
                                type="button"
                                variant="outline"
                                onClick={() => navigate(-1)}
                            >
                                Avbryt
                            </Button>
                            <Button type="submit" disabled={isSubmitting} className="gap-2">
                                <Save className="h-4 w-4" />
                                {isSubmitting ? 'Sparar...' : 'Spara ändringar'}
                            </Button>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
}
