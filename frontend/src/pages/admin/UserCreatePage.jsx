import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { userApi } from '@/api/users';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue, SelectValueLabel, SelectLabel, SelectGroup, SelectTrigger } from '@/components/ui/select'; // Assuming shadcn/ui Select component is available

export function UserCreatePage() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [roleName, setRoleName] = useState('ROLE_STUDENT'); // Default role
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      const userData = { username, email, password, roleName };
      await userApi.createUser(userData);
      alert('Användare skapad!');
      navigate('/admin/users'); // Redirect to user list
    } catch (err) {
      console.error('Failed to create user:', err);
      setError(err.response?.data?.error || err.message || 'Ett oväntat fel uppstod.');
    }
  };

  return (
    <div className="p-8">
      <h1 className="text-3xl font-bold mb-6">Skapa Ny Användare</h1>
      <Card>
        <CardHeader>
          <CardTitle>Användarinformation</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <Label htmlFor="username">Användarnamn</Label>
              <Input
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>
            <div>
              <Label htmlFor="email">E-post</Label>
              <Input
                id="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
            <div>
              <Label htmlFor="password">Lösenord</Label>
              <Input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            <div>
              <Label htmlFor="role">Roll</Label>
              <Select value={roleName} onValueChange={setRoleName}>
                <SelectTrigger>
                  <SelectValue placeholder="Välj roll" />
                </SelectTrigger>
                <SelectContent>
                  <SelectGroup>
                    <SelectLabel>Tillgängliga roller</SelectLabel>
                    <SelectItem value="ROLE_ADMIN">Admin</SelectItem>
                    <SelectItem value="ROLE_TEACHER">Lärare</SelectItem>
                    <SelectItem value="ROLE_STUDENT">Student</SelectItem>
                  </SelectGroup>
                </SelectContent>
              </Select>
            </div>
            {error && <p className="text-destructive">Fel: {error}</p>}
            <div className="flex justify-end gap-2">
              <Button type="submit">Skapa Användare</Button>
              <Button type="button" variant="outline" onClick={() => navigate('/admin/users')}>Avbryt</Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
