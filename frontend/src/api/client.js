import axios from 'axios';

// Get CSRF token from cookie
const getCsrfToken = () => {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    return match ? decodeURIComponent(match[1]) : null;
};

// Seed the XSRF-TOKEN cookie if not already set
let csrfBootstrapPromise = null;
const bootstrapCsrf = () => {
    if (getCsrfToken()) return Promise.resolve(); // Already seeded
    if (csrfBootstrapPromise) return csrfBootstrapPromise; // Already in flight

    csrfBootstrapPromise = client.get('/csrf-token')
      .catch(() => {})
      .finally(() => { csrfBootstrapPromise = null; });

    return csrfBootstrapPromise;
};

const client = axios.create({
    baseURL: '/api',
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json'
    }
});

// Add CSRF token to all mutation requests
client.interceptors.request.use(async config => {
    const method = (config.method || '').toLowerCase();

    if (['post', 'put', 'delete', 'patch'].includes(method)) {
        await bootstrapCsrf();
        const token = getCsrfToken();
        if (token) {
            config.headers['X-XSRF-TOKEN'] = token;
        }
    }

    return config;
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