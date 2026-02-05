export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  endpoints: {
    artists: '/v1/artist',
    albums: '/v1/album',
    auth: {
      login: '/auth/login',
      refresh: '/auth/refresh'
    }
  }
};

