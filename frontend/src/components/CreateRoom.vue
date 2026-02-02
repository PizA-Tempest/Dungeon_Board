<template>
  <div class="max-w-2xl mx-auto">
    <div class="card">
      <h2 class="text-3xl font-bold text-dungeon-accent mb-6">Create a Room</h2>

      <form @submit.prevent="handleCreateRoom" class="space-y-6">
        <div>
          <label class="block text-sm font-medium mb-2">Room Name</label>
          <input
            v-model="roomName"
            type="text"
            class="input-field"
            placeholder="Enter room name"
            required
            minlength="3"
            maxlength="30"
          />
        </div>

        <div>
          <label class="block text-sm font-medium mb-2">Max Players</label>
          <select v-model="maxPlayers" class="input-field">
            <option :value="2">2 Players</option>
            <option :value="3">3 Players</option>
            <option :value="4">4 Players</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium mb-2">Number of Bots</label>
          <select v-model="botCount" class="input-field">
            <option :value="0">No Bots</option>
            <option :value="1">1 Bot</option>
            <option :value="2">2 Bots</option>
            <option :value="3">3 Bots</option>
          </select>
        </div>

        <div class="flex items-center">
          <input
            v-model="isPrivate"
            type="checkbox"
            id="private"
            class="w-4 h-4 rounded"
          />
          <label for="private" class="ml-2 text-sm">Private Room</label>
        </div>

        <div v-if="error" class="text-red-400 text-sm">
          {{ error }}
        </div>

        <div class="flex gap-4">
          <button type="submit" class="btn-primary flex-1" :disabled="loading">
            {{ loading ? 'Creating...' : 'Create Room' }}
          </button>
          <router-link to="/lobby" class="btn-secondary flex-1 text-center">
            Cancel
          </router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useGameStore } from '@/stores/game'

const router = useRouter()
const gameStore = useGameStore()

const roomName = ref('')
const maxPlayers = ref(4)
const botCount = ref(0)
const isPrivate = ref(false)
const error = ref('')
const loading = ref(false)

async function handleCreateRoom() {
  error.value = ''
  loading.value = true

  try {
    const room = await gameStore.createRoom({
      name: roomName.value,
      maxPlayers: maxPlayers.value,
      botCount: botCount.value,
      isPrivate: isPrivate.value
    })

    // Load character data
    await gameStore.loadClassesAndRaces()

    router.push(`/room/${room.roomId}/character`)
  } catch (err) {
    error.value = err.message || 'Failed to create room'
  } finally {
    loading.value = false
  }
}
</script>
