import axios from 'axios';

// Get CSRF token from cookie
const getCsrfToken = () => {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    return match ? decodeURIComponent(match[1]) : null;
};

const client = axios.create({
    baseURL: '/api',
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json'
    }
});

// Add CSRF token to all mutation requests
client.interceptors.request.use(config => {
    const method = (config.method || '').toLowerCase();

    // Add CSRF token to POST, PUT, DELETE, PATCH
    if (['post', 'put', 'delete', 'patch'].includes(method)) {
        const token = getCsrfToken();
        if (token) {
            config.headers['X-XSRF-TOKEN'] = token;
        }
    }

    return config;
}, error => {
    return Promise.reject(error);
});

export default client;



// OLD IMPLEMEMNTATION
// // Axios client for API calls
// import axios from 'axios';
//
// const client = axios.create({
//     baseURL: '/api',
//     withCredentials: true,   // send session-cookie with every call
//     headers: {
//         'Content-Type': 'application/json'
//     }
// });
//
// export default client;