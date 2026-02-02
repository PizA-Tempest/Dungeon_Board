import { createRouter, createWebHistory } from 'vue-router'
import { useGameStore } from '@/stores/game'
import Login from '@/components/Login.vue'
import Register from '@/components/Register.vue'
import Lobby from '@/components/Lobby.vue'
import CreateRoom from '@/components/CreateRoom.vue'
import CharacterSelect from '@/components/CharacterSelect.vue'
import GameBoard from '@/components/GameBoard.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/lobby',
    name: 'Lobby',
    component: Lobby,
    meta: { requiresAuth: true }
  },
  {
    path: '/room/create',
    name: 'CreateRoom',
    component: CreateRoom,
    meta: { requiresAuth: true }
  },
  {
    path: '/room/:roomId/character',
    name: 'CharacterSelect',
    component: CharacterSelect,
    meta: { requiresAuth: true }
  },
  {
    path: '/room/:roomId/game',
    name: 'GameBoard',
    component: GameBoard,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const gameStore = useGameStore()

  if (to.meta.requiresAuth && !gameStore.isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router
