import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000
})

const TOKEN_KEY = 'slayseed_token'
const USER_KEY = 'slayseed_user'

api.interceptors.request.use(config => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    }
    return Promise.reject(error)
  }
)

export const auth = {
  login(username, password) {
    return api.post('/auth/login', { username, password }).then(res => {
      localStorage.setItem(TOKEN_KEY, res.data.token)
      localStorage.setItem(USER_KEY, JSON.stringify(res.data.user))
      return res.data
    })
  },
  register(username, password, nickname) {
    return api.post('/auth/register', { username, password, nickname }).then(res => {
      localStorage.setItem(TOKEN_KEY, res.data.token)
      localStorage.setItem(USER_KEY, JSON.stringify(res.data.user))
      return res.data
    })
  },
  logout() {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  },
  isLoggedIn() {
    return !!localStorage.getItem(TOKEN_KEY)
  },
  getCurrentUser() {
    const user = localStorage.getItem(USER_KEY)
    return user ? JSON.parse(user) : null
  }
}

export const seedsApi = {
  getSeeds(params = {}) {
    return api.get('/seeds', { params }).then(res => res.data)
  },
  searchSeeds(keyword, page = 0, size = 20) {
    return api.get('/seeds/search', { params: { keyword, page, size } }).then(res => res.data)
  },
  getSeed(id) {
    return api.get(`/seeds/${id}`).then(res => res.data)
  },
  createSeed(data) {
    return api.post('/seeds', data).then(res => res.data)
  },
  deleteSeed(id) {
    return api.delete(`/seeds/${id}`).then(res => res.data)
  },
  likeSeed(id) {
    return api.post(`/seeds/${id}/like`).then(res => res.data)
  },
  favoriteSeed(id) {
    return api.post(`/seeds/${id}/favorite`).then(res => res.data)
  }
}

export const usersApi = {
  getUser(id) {
    return api.get(`/users/${id}`).then(res => res.data)
  },
  getUserSeeds(id, page = 0, size = 20) {
    return api.get(`/users/${id}/seeds`, { params: { page, size } }).then(res => res.data)
  },
  getUserFavorites(id, page = 0, size = 20) {
    return api.get(`/users/${id}/favorites`, { params: { page, size } }).then(res => res.data)
  },
  followUser(id) {
    return api.post(`/users/${id}/follow`).then(res => res.data)
  },
  isFollowing(id) {
    return api.get(`/users/${id}/is-following`).then(res => res.data)
  }
}

export const commentsApi = {
  getComments(seedId, page = 0, size = 20) {
    return api.get(`/comments/seed/${seedId}`, { params: { page, size } }).then(res => res.data)
  },
  createComment(seedId, content) {
    return api.post(`/comments/seed/${seedId}`, { content }).then(res => res.data)
  }
}

export default api