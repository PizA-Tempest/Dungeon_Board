<template>
  <div id="app" class="min-h-screen bg-dungeon-dark">
    <div v-if="gameStore.isAuthenticated" class="container mx-auto px-4 py-4">
      <header class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold text-dungeon-accent">🎲 Dungeon Board</h1>
        <div class="flex items-center gap-4">
          <span class="text-gray-300">Welcome, {{ gameStore.user?.username }}</span>
          <span v-if="gameStore.isConnected" class="flex items-center gap-2 text-green-400">
            <span class="w-2 h-2 bg-green-400 rounded-full animate-pulse"></span>
            Connected
          </span>
          <span v-else class="flex items-center gap-2 text-red-400">
            <span class="w-2 h-2 bg-red-400 rounded-full"></span>
            Disconnected
          </span>
          <button @click="handleLogout" class="btn-secondary">Logout</button>
        </div>
      </header>
    </div>

    <main class="container mx-auto px-4">
      <router-view />
    </main>

    <!-- Global notification toast -->
    <div
      v-if="notification.show"
      :class="[
        'fixed bottom-4 right-4 px-6 py-3 rounded-lg shadow-lg transition-all duration-300',
        notification.type === 'error' ? 'bg-red-600' : 'bg-dungeon-accent'
      ]"
    >
      {{ notification.message }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useGameStore } from '@/stores/game'
import { api } from '@/services/api'

const router = useRouter()
const gameStore = useGameStore()

const notification = ref({
  show: false,
  message: '',
  type: 'info'
})

function showNotification(message, type = 'info') {
  notification.value = { show: true, message, type }
  setTimeout(() => {
    notification.value.show = false
  }, 3000)
}

function handleLogout() {
  gameStore.logout()
  router.push('/login')
  showNotification('Logged out successfully')
}

onMounted(() => {
  // Check for existing token
  const token = localStorage.getItem('token')
  if (token) {
    gameStore.token = token
    api.setToken(token)
  }
})
</script>
