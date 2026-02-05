export const environment = {
  production: true,
  apiUrl: 'https://api.production.com/api',
  endpoints: {
    artists: '/v1/artist',
    albums: '/v1/album',
    auth: {
      login: '/auth/login',
      refresh: '/auth/refresh'
    }
  }
};

