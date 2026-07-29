export const API_URL = {
  auth: {
    login: '/auth/login',
    refresh: '/auth/refresh'
  },
  words: {
    base: '/words',
    byId: (id: number) => `/words/${id}`,
    bySubdomain: (subdomain: string) => `/words/by-subdomain/${subdomain}`,
    search: '/words/search'
  },
  paras: {
    base: '/paras',
    byId: (id: number) => `/paras/${id}`,
    byWordId: (wordId: number) => `/paras/by-word/${wordId}`,
    bySubdomain: (subdomain: string) => `/paras/by-subdomain/${subdomain}`,
    byWordIdAndSubdomain: (wordId: number, subdomain: string) => `/paras/by-word/${wordId}/subdomain/${subdomain}`
  },
  divisions: {
    base: '/divisions',
    byId: (id: number) => `/divisions/${id}`
  },
  districts: {
    base: '/districts',
    byId: (id: number) => `/districts/${id}`,
    byDivision: (divisionId: number) => `/districts/by-division/${divisionId}`
  },
  pourashavas: {
    base: '/pourashavas',
    byId: (id: number) => `/pourashavas/${id}`,
    bySubdomain: (subdomain: string) => `/pourashavas/by-subdomain/${subdomain}`
  }
};
