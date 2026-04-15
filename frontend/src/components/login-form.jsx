import { useState } from "react";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Separator } from "@/components/ui/separator";
import { ThemeToggle } from "@/components/ThemeToggle";

export function LoginForm({ 
  onSubmit, 
  error, 
  isLoading = false,
  githubOAuthUrl,
  className 
}) {
  const [activeTab, setActiveTab] = useState('login');
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    await onSubmit(formData, activeTab);
  };

  const isFormValid = () => {
    if (activeTab === 'login') {
      return formData.username && formData.password;
    } else {
      return formData.username && formData.email && 
             formData.password && formData.confirmPassword &&
             formData.password === formData.confirmPassword;
    }
  };

  return (
    <div className={cn("flex flex-col gap-6", className)}>
      <Card>
        <CardHeader className="text-center">
          <div className="flex items-center justify-between">
            <CardTitle>
              {activeTab === 'login' ? 'ITBS Studentportal' : 'Create Account'}
            </CardTitle>
            <ThemeToggle />
          </div>
          <CardDescription>
            {activeTab === 'login' 
              ? 'Enter your credentials to access your account'
              : 'Fill in the form to create your account'
            }
          </CardDescription>
        </CardHeader>
        
        <CardContent>
          <Tabs value={activeTab} onValueChange={setActiveTab}>
            <TabsList className="grid w-full grid-cols-2">
              <TabsTrigger value="login">Login</TabsTrigger>
              <TabsTrigger value="register">Register</TabsTrigger>
            </TabsList>

            {error && (
              <Alert variant="destructive">
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            )}

            <TabsContent value="login">
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="login-username">Username</Label>
                  <Input
                    id="login-username"
                    type="text"
                    placeholder="Enter your username"
                    value={formData.username}
                    onChange={(e) => setFormData({...formData, username: e.target.value})}
                    required
                    disabled={isLoading}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="login-password">Password</Label>
                  <Input
                    id="login-password"
                    type="password"
                    placeholder="Enter your password"
                    value={formData.password}
                    onChange={(e) => setFormData({...formData, password: e.target.value})}
                    required
                    disabled={isLoading}
                  />
                </div>

                <Button 
                  type="submit" 
                  className="w-full"
                  disabled={!isFormValid() || isLoading}
                >
                  {isLoading ? 'Loading...' : 'Sign In'}
                </Button>
              </form>
            </TabsContent>

            <TabsContent value="register">
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="register-username">Username</Label>
                  <Input
                    id="register-username"
                    type="text"
                    placeholder="Enter your username"
                    value={formData.username}
                    onChange={(e) => setFormData({...formData, username: e.target.value})}
                    required
                    disabled={isLoading}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="register-email">Email</Label>
                  <Input
                    id="register-email"
                    type="email"
                    placeholder="m@example.com"
                    value={formData.email}
                    onChange={(e) => setFormData({...formData, email: e.target.value})}
                    required
                    disabled={isLoading}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="register-password">Password</Label>
                  <Input
                    id="register-password"
                    type="password"
                    placeholder="Enter your password"
                    value={formData.password}
                    onChange={(e) => setFormData({...formData, password: e.target.value})}
                    required
                    disabled={isLoading}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="register-confirmPassword">Confirm Password</Label>
                  <Input
                    id="register-confirmPassword"
                    type="password"
                    placeholder="Confirm your password"
                    value={formData.confirmPassword}
                    onChange={(e) => setFormData({...formData, confirmPassword: e.target.value})}
                    required
                    disabled={isLoading}
                  />
                  {formData.confirmPassword && formData.password !== formData.confirmPassword && (
                    <p className="text-sm text-red-500">
                      Passwords do not match
                    </p>
                  )}
                </div>

                <Button 
                  type="submit" 
                  className="w-full"
                  disabled={!isFormValid() || isLoading}
                >
                  {isLoading ? 'Loading...' : 'Create Account'}
                </Button>
              </form>
            </TabsContent>

            <Separator className="my-4">Or continue with</Separator>

            <Button
              variant="outline"
              className="w-full"
              asChild
              disabled={isLoading}
            >
              <a href={githubOAuthUrl}>
                GitHub
              </a>
            </Button>
          </Tabs>
        </CardContent>
      </Card>
      
      <CardDescription className="px-6 text-center">
        By clicking continue, you agree to our non-existent Terms of Service and Privacy Policy.
      </CardDescription>
    </div>
  );
}