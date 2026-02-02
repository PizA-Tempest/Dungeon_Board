import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '@/services/api'
import { ws } from '@/services/websocket'

export const useGameStore = defineStore('game', () => {
  // State
  const user = ref(null)
  const token = ref(null)
  const currentRoom = ref(null)
  const gameState = ref(null)
  const classes = ref([])
  const races = ref([])
  const isConnected = ref(false)

  // Computed
  const isAuthenticated = computed(() => !!token.value)
  const isCurrentPlayerTurn = computed(() => {
    if (!gameState.value || !user.value) return false
    const currentPlayer = gameState.value.players[gameState.value.currentPlayerIndex]
    return currentPlayer && currentPlayer.id === user.value.id
  })

  // Actions
  async function login(username, password) {
    try {
      const response = await api.login(username, password)
      token.value = response.token
      api.setToken(response.token)
      user.value = { id: response.id, username: response.username }
      return response
    } catch (error) {
      throw error
    }
  }

  async function register(username, password) {
    try {
      const response = await api.register(username, password)
      token.value = response.token
      api.setToken(response.token)
      user.value = { id: response.id, username: response.username }
      return response
    } catch (error) {
      throw error
    }
  }

  async function logout() {
    try {
      await api.logout()
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      token.value = null
      user.value = null
      currentRoom.value = null
      gameState.value = null
      api.setToken(null)
      ws.disconnect()
    }
  }

  async function createRoom(roomData) {
    try {
      const room = await api.createRoom(roomData)
      currentRoom.value = room
      return room
    } catch (error) {
      throw error
    }
  }

  async function joinRoom(roomId) {
    try {
      const room = await api.joinRoom(roomId)
      currentRoom.value = room
      await connectToRoom(roomId)
      return room
    } catch (error) {
      throw error
    }
  }

  async function leaveRoom(roomId) {
    try {
      await api.leaveRoom(roomId)
      currentRoom.value = null
      gameState.value = null
      ws.disconnect()
    } catch (error) {
      throw error
    }
  }

  async function getRooms() {
    try {
      return await api.getRooms()
    } catch (error) {
      throw error
    }
  }

  async function getRoom(roomId) {
    try {
      const room = await api.getRoom(roomId)
      currentRoom.value = room
      return room
    } catch (error) {
      throw error
    }
  }

  async function startGame(roomId) {
    try {
      const state = await api.startGame(roomId)
      gameState.value = state
      return state
    } catch (error) {
      throw error
    }
  }

  async function getGameState(roomId) {
    try {
      const state = await api.getGameState(roomId)
      gameState.value = state
      return state
    } catch (error) {
      throw error
    }
  }

  async function loadClassesAndRaces() {
    try {
      const [classesData, racesData] = await Promise.all([
        api.getClasses(),
        api.getRaces()
      ])
      classes.value = classesData
      races.value = racesData
    } catch (error) {
      console.error('Failed to load classes and races:', error)
    }
  }

  async function selectCharacter(roomId, classId, raceId) {
    try {
      await api.selectCharacter(roomId, classId, raceId)
      await getGameState(roomId)
    } catch (error) {
      throw error
    }
  }

  async function rollDice(roomId) {
    try {
      return await api.rollDice(roomId)
    } catch (error) {
      throw error
    }
  }

  async function reroll(roomId) {
    try {
      return await api.reroll(roomId)
    } catch (error) {
      throw error
    }
  }

  async function useAbility(roomId) {
    try {
      await api.useAbility(roomId)
      await getGameState(roomId)
    } catch (error) {
      throw error
    }
  }

  async function addBot(roomId) {
    try {
      const room = await api.addBot(roomId)
      currentRoom.value = room
      return room
    } catch (error) {
      throw error
    }
  }

  function connectToRoom(roomId) {
    // Use WebSocket URL from environment or fallback to localhost
    const apiBaseUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'
    const wsProtocol = apiBaseUrl.startsWith('https') ? 'wss' : 'ws'
    const wsHost = apiBaseUrl.replace('/api', '').replace(/^https?:\/\//, '')
    const wsUrl = `${wsProtocol}://${wsHost}/ws/game`

    ws.connect(wsUrl).then(() => {
      isConnected.value = true
      // Send join room message
      ws.send({ type: 'JOIN_ROOM', roomId })
    }).catch((error) => {
      console.error('Failed to connect to WebSocket:', error)
      isConnected.value = false
    })

    // Listen for game state updates
    ws.on('GAME_STATE', (data) => {
      if (data.gameState) {
        gameState.value = data.gameState
      }
    })

    ws.on('PLAYER_JOINED', (data) => {
      console.log('Player joined:', data)
    })

    ws.on('PLAYER_LEFT', (data) => {
      console.log('Player left:', data)
    })

    ws.on('GAME_STARTED', (data) => {
      console.log('Game started:', data)
    })

    ws.on('DICE_ROLLED', (data) => {
      console.log('Dice rolled:', data)
    })

    ws.on('PLAYER_MOVED', (data) => {
      console.log('Player moved:', data)
    })

    ws.on('COMBAT_RESULT', (data) => {
      console.log('Combat result:', data)
    })

    ws.on('TREASURE_FOUND', (data) => {
      console.log('Treasure found:', data)
    })

    ws.on('TRAP_TRIGGERED', (data) => {
      console.log('Trap triggered:', data)
    })

    ws.on('EVENT_CARD', (data) => {
      console.log('Event card:', data)
    })

    ws.on('GAME_OVER', (data) => {
      console.log('Game over:', data)
      if (gameState.value) {
        gameState.value.winnerId = data.data
      }
    })

    ws.on('ERROR', (data) => {
      console.error('Game error:', data.message)
    })

    ws.on('DISCONNECTED', () => {
      isConnected.value = false
    })

    ws.on('MAX_RECONNECT_REACHED', () => {
      isConnected.value = false
    })
  }

  function disconnectWebSocket() {
    ws.disconnect()
    isConnected.value = false
  }

  function getCurrentPlayer() {
    if (!gameState.value || !user.value) return null
    return gameState.value.players.find(p => p.id === user.value.id)
  }

  return {
    // State
    user,
    token,
    currentRoom,
    gameState,
    classes,
    races,
    isConnected,

    // Computed
    isAuthenticated,
    isCurrentPlayerTurn,

    // Actions
    login,
    register,
    logout,
    createRoom,
    joinRoom,
    leaveRoom,
    getRoom,
    getRooms,
    startGame,
    getGameState,
    loadClassesAndRaces,
    selectCharacter,
    rollDice,
    reroll,
    useAbility,
    addBot,
    connectToRoom,
    disconnectWebSocket,
    getCurrentPlayer
  }
})
