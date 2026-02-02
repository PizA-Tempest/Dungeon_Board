<template>
  <div class="min-h-screen flex items-center justify-center">
    <div class="card max-w-md w-full">
      <div class="text-center mb-8">
        <h1 class="text-4xl font-bold text-dungeon-accent mb-2">⚔️ Dungeon Board</h1>
        <p class="text-gray-400">A turn-based fantasy board game RPG</p>
      </div>

      <form @submit.prevent="handleLogin" class="space-y-6">
        <div>
          <label class="block text-sm font-medium mb-2">Username</label>
          <input
            v-model="username"
            type="text"
            class="input-field"
            placeholder="Enter your username"
            required
            autocomplete="username"
          />
        </div>

        <div>
          <label class="block text-sm font-medium mb-2">Password</label>
          <input
            v-model="password"
            type="password"
            class="input-field"
            placeholder="Enter your password"
            required
            autocomplete="current-password"
          />
        </div>

        <div v-if="error" class="text-red-400 text-sm">
          {{ error }}
        </div>

        <button type="submit" class="btn-primary w-full" :disabled="loading">
          {{ loading ? 'Logging in...' : 'Login' }}
        </button>
      </form>

      <div class="mt-6 text-center">
        <p class="text-gray-400">
          Don't have an account?
          <router-link to="/register" class="text-dungeon-accent hover:underline">
            Register here
          </router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useGameStore } from '@/stores/game'

const router = useRouter()
const gameStore = useGameStore()

const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function handleLogin() {
  error.value = ''
  loading.value = true

  try {
    await gameStore.login(username.value, password.value)
    router.push('/lobby')
  } catch (err) {
    error.value = err.message || 'Login failed. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>
