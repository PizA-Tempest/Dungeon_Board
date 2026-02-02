<template>
  <div class="min-h-screen flex items-center justify-center">
    <div class="card max-w-md w-full">
      <div class="text-center mb-8">
        <h1 class="text-4xl font-bold text-dungeon-accent mb-2">⚔️ Join Dungeon Board</h1>
        <p class="text-gray-400">Create your account and start your adventure</p>
      </div>

      <form @submit.prevent="handleRegister" class="space-y-6">
        <div>
          <label class="block text-sm font-medium mb-2">Username</label>
          <input
            v-model="username"
            type="text"
            class="input-field"
            placeholder="Choose a username"
            required
            minlength="3"
            maxlength="20"
            autocomplete="username"
          />
          <p class="text-xs text-gray-500 mt-1">3-20 characters</p>
        </div>

        <div>
          <label class="block text-sm font-medium mb-2">Password</label>
          <input
            v-model="password"
            type="password"
            class="input-field"
            placeholder="Choose a password"
            required
            minlength="6"
            maxlength="40"
            autocomplete="new-password"
          />
          <p class="text-xs text-gray-500 mt-1">At least 6 characters</p>
        </div>

        <div>
          <label class="block text-sm font-medium mb-2">Confirm Password</label>
          <input
            v-model="confirmPassword"
            type="password"
            class="input-field"
            placeholder="Confirm your password"
            required
            autocomplete="new-password"
          />
        </div>

        <div v-if="error" class="text-red-400 text-sm">
          {{ error }}
        </div>

        <button type="submit" class="btn-primary w-full" :disabled="loading">
          {{ loading ? 'Creating account...' : 'Register' }}
        </button>
      </form>

      <div class="mt-6 text-center">
        <p class="text-gray-400">
          Already have an account?
          <router-link to="/login" class="text-dungeon-accent hover:underline">
            Login here
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
const confirmPassword = ref('')
const error = ref('')
const loading = ref(false)

async function handleRegister() {
  error.value = ''

  if (password.value !== confirmPassword.value) {
    error.value = 'Passwords do not match'
    return
  }

  loading.value = true

  try {
    await gameStore.register(username.value, password.value)
    router.push('/lobby')
  } catch (err) {
    error.value = err.message || 'Registration failed. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>
