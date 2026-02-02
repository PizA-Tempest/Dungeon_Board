<template>
  <div class="max-w-4xl mx-auto">
    <div class="flex justify-between items-center mb-8">
      <h2 class="text-3xl font-bold text-dungeon-accent">🏰 Game Lobby</h2>
      <router-link to="/room/create" class="btn-primary">
        + Create Room
      </router-link>
    </div>

    <div class="grid gap-6">
      <!-- Room List -->
      <div class="card">
        <h3 class="text-xl font-bold mb-4">Available Rooms</h3>

        <div v-if="loading" class="text-center py-8 text-gray-400">
          Loading rooms...
        </div>

        <div v-else-if="rooms.length === 0" class="text-center py-8 text-gray-400">
          No rooms available. Create one to start playing!
        </div>

        <div v-else class="space-y-3">
          <div
            v-for="room in rooms"
            :key="room.roomId"
            class="flex items-center justify-between p-4 bg-dungeon-blue rounded-lg hover:bg-blue-900 transition-colors"
          >
            <div>
              <h4 class="font-bold text-lg">{{ room.name }}</h4>
              <p class="text-sm text-gray-400">
                {{ room.players?.length || 0 }} / {{ room.maxPlayers }} players
              </p>
            </div>
            <button
              @click="joinRoom(room.roomId)"
              class="btn-secondary"
              :disabled="room.isFull"
            >
              {{ room.isFull ? 'Full' : 'Join Room' }}
            </button>
          </div>
        </div>
      </div>

      <!-- Instructions -->
      <div class="card">
        <h3 class="text-xl font-bold mb-4">📜 How to Play</h3>
        <ul class="space-y-2 text-gray-300">
          <li>• Create or join a room with 2-4 players</li>
          <li>• Select your character class and race</li>
          <li>• Roll dice to move around the board</li>
          <li>• Encounter monsters, find treasure, trigger events</li>
          <li>• After 10 rounds, the player with the highest score wins!</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useGameStore } from '@/stores/game'

const router = useRouter()
const gameStore = useGameStore()

const rooms = ref([])
const loading = ref(false)

async function loadRooms() {
  loading.value = true
  try {
    rooms.value = await gameStore.getRooms()
  } catch (error) {
    console.error('Failed to load rooms:', error)
  } finally {
    loading.value = false
  }
}

async function joinRoom(roomId) {
  try {
    await gameStore.joinRoom(roomId)

    // Load character data
    await gameStore.loadClassesAndRaces()

    router.push(`/room/${roomId}/character`)
  } catch (error) {
    alert('Failed to join room: ' + error.message)
  }
}

onMounted(() => {
  loadRooms()
  // Refresh rooms every 5 seconds
  const interval = setInterval(loadRooms, 5000)
  return () => clearInterval(interval)
})
</script>
