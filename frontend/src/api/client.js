// Axios client for API calls
import axios from 'axios';

const client = axios.create({
    baseURL: '/api',
    withCredentials: true,   // send session-cookie with every call
    headers: {
        'Content-Type': 'application/json'
    }
});

export default client;