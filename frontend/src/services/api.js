const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

class ApiService {
  constructor() {
    this.token = localStorage.getItem('token')
  }

  setToken(token) {
    this.token = token
    if (token) {
      localStorage.setItem('token', token)
    } else {
      localStorage.removeItem('token')
    }
  }

  getHeaders() {
    const headers = {
      'Content-Type': 'application/json'
    }
    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`
    }
    return headers
  }

  async request(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`
    const config = {
      ...options,
      headers: {
        ...this.getHeaders(),
        ...options.headers
      }
    }

    try {
      const response = await fetch(url, config)
      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.message || data.error || 'Request failed')
      }

      return data
    } catch (error) {
      console.error('API request failed:', error)
      throw error
    }
  }

  // Auth endpoints
  login(username, password) {
    return this.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    })
  }

  register(username, password) {
    return this.request('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    })
  }

  getCurrentUser() {
    return this.request('/auth/me')
  }

  logout() {
    return this.request('/auth/logout', { method: 'POST' })
  }

  // Room endpoints
  createRoom(roomData) {
    return this.request('/room/create', {
      method: 'POST',
      body: JSON.stringify(roomData)
    })
  }

  joinRoom(roomId) {
    return this.request(`/room/${roomId}/join`, { method: 'POST' })
  }

  leaveRoom(roomId) {
    return this.request(`/room/${roomId}/leave`, { method: 'POST' })
  }

  getRoom(roomId) {
    return this.request(`/room/${roomId}`)
  }

  getRooms() {
    return this.request('/room/list')
  }

  startGame(roomId) {
    return this.request(`/room/${roomId}/start`, { method: 'POST' })
  }

  getGameState(roomId) {
    return this.request(`/room/${roomId}/state`)
  }

  addBot(roomId) {
    return this.request(`/room/${roomId}/add-bot`, { method: 'POST' })
  }

  // Game endpoints
  rollDice(roomId) {
    return this.request(`/game/${roomId}/roll`, { method: 'POST' })
  }

  reroll(roomId) {
    return this.request(`/game/${roomId}/reroll`, { method: 'POST' })
  }

  selectCharacter(roomId, classId, raceId) {
    return this.request(`/game/${roomId}/character`, {
      method: 'POST',
      body: JSON.stringify({ classId, raceId })
    })
  }

  useAbility(roomId) {
    return this.request(`/game/${roomId}/ability`, { method: 'POST' })
  }

  getClasses() {
    return this.request('/game/classes')
  }

  getRaces() {
    return this.request('/game/races')
  }
}

export const api = new ApiService()
