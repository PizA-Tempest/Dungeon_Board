<template>
  <div class="max-w-6xl mx-auto">
    <div class="mb-6">
      <h2 class="text-3xl font-bold text-dungeon-accent mb-2">Select Your Character</h2>
      <p class="text-gray-400">Room: {{ roomName }} | Players: {{ players?.length || 0 }} / {{ maxPlayers }}</p>
    </div>

    <div class="grid lg:grid-cols-2 gap-6">
      <!-- Classes -->
      <div class="card">
        <h3 class="text-xl font-bold mb-4">⚔️ Choose Your Class</h3>
        <div class="grid grid-cols-2 gap-3">
          <button
            v-for="cls in gameStore.classes"
            :key="cls.id"
            @click="selectedClass = cls"
            :class="[
              'p-4 rounded-lg text-left transition-all',
              selectedClass?.id === cls.id
                ? 'bg-dungeon-accent ring-2 ring-white'
                : 'bg-dungeon-blue hover:bg-blue-900'
            ]"
          >
            <h4 class="font-bold">{{ cls.name }}</h4>
            <p class="text-sm text-gray-300 mt-1">{{ cls.description }}</p>
            <div class="mt-2 text-xs text-gray-400">
              HP: {{ cls.baseHp }} | ATK: {{ cls.baseAttack }} | DEF: {{ cls.baseDefense }}
            </div>
          </button>
        </div>
      </div>

      <!-- Races -->
      <div class="card">
        <h3 class="text-xl font-bold mb-4">🧬 Choose Your Race</h3>
        <div class="grid grid-cols-2 gap-3">
          <button
            v-for="race in gameStore.races"
            :key="race.id"
            @click="selectedRace = race"
            :class="[
              'p-4 rounded-lg text-left transition-all',
              selectedRace?.id === race.id
                ? 'bg-dungeon-accent ring-2 ring-white'
                : 'bg-dungeon-blue hover:bg-blue-900'
            ]"
          >
            <h4 class="font-bold">{{ race.name }}</h4>
            <p class="text-sm text-gray-300 mt-1">{{ race.description }}</p>
            <div class="mt-2 text-xs text-gray-400">
              <span v-if="race.movementBonus > 0">+{{ race.movementBonus }} Move</span>
              <span v-if="race.attackBonus > 0"> +{{ race.attackBonus }} ATK</span>
              <span v-if="race.goldBonus > 0"> +{{ race.goldBonus }} Gold</span>
            </div>
          </button>
        </div>
      </div>
    </div>

    <!-- Selected Character Preview -->
    <div v-if="selectedClass && selectedRace" class="card mt-6">
      <h3 class="text-xl font-bold mb-4">Your Character</h3>
      <div class="flex items-center gap-6">
        <div class="text-6xl">🧙‍♂️</div>
        <div>
          <h4 class="text-2xl font-bold text-dungeon-gold">
            {{ selectedRace.name }} {{ selectedClass.name }}
          </h4>
          <p class="text-gray-400 mt-2">{{ selectedClass.description }}</p>
          <div class="mt-3 flex gap-4 text-sm">
            <span>❤️ HP: {{ selectedClass.baseHp }}</span>
            <span>⚔️ ATK: {{ selectedClass.baseAttack + selectedRace.attackBonus }}</span>
            <span>🛡️ DEF: {{ selectedClass.baseDefense }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Action Buttons -->
    <div class="mt-6 flex gap-4">
      <button
        @click="confirmSelection"
        class="btn-primary flex-1"
        :disabled="!selectedClass || !selectedRace || loading"
      >
        {{ loading ? 'Confirming...' : 'Confirm Selection' }}
      </button>
      <button
        v-if="isHost && canStartGame"
        @click="startGame"
        class="btn-secondary flex-1"
        :disabled="loading"
      >
        {{ loading ? 'Starting...' : 'Start Game' }}
      </button>
      <router-link to="/lobby" class="btn-secondary flex-1 text-center">
        Back to Lobby
      </router-link>
    </div>

    <!-- Players in Room -->
    <div class="card mt-6">
      <h3 class="text-xl font-bold mb-4">Players in Room</h3>
      <div class="space-y-2">
        <div
          v-for="player in players"
          :key="player.id"
          class="flex items-center justify-between p-3 bg-dungeon-blue rounded"
        >
          <span>
            {{ player.username }} {{ player.isBot ? '(Bot)' : '' }}
          </span>
          <span v-if="player.playerClass" class="text-dungeon-gold">
            {{ player.race }} {{ player.playerClass }}
          </span>
          <span v-else class="text-gray-400">Selecting...</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useGameStore } from '@/stores/game'

const router = useRouter()
const route = useRoute()
const gameStore = useGameStore()

const roomId = route.params.roomId
const selectedClass = ref(null)
const selectedRace = ref(null)
const loading = ref(false)

const roomName = computed(() => gameStore.currentRoom?.name || '')
const maxPlayers = computed(() => gameStore.currentRoom?.maxPlayers || 4)
const players = computed(() => gameStore.currentRoom?.players || [])
const isHost = computed(() => gameStore.currentRoom?.hostId === gameStore.user?.id)

const canStartGame = computed(() => {
  return players.value.length >= 2 &&
         players.value.every(p => p.isBot || p.playerClass)
})

async function confirmSelection() {
  if (!selectedClass.value || !selectedRace.value) return

  loading.value = true
  try {
    await gameStore.selectCharacter(
      roomId,
      selectedClass.value.id,
      selectedRace.value.id
    )
    alert('Character selected! Waiting for game to start...')
  } catch (error) {
    alert('Failed to select character: ' + error.message)
  } finally {
    loading.value = false
  }
}

async function startGame() {
  loading.value = true
  try {
    await gameStore.startGame(roomId)
    router.push(`/room/${roomId}/game`)
  } catch (error) {
    alert('Failed to start game: ' + error.message)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    // Load room state
    const room = await gameStore.getRoom(roomId)
    gameStore.currentRoom = room

    // Load classes and races if not loaded
    if (gameStore.classes.length === 0 || gameStore.races.length === 0) {
      await gameStore.loadClassesAndRaces()
    }
  } catch (error) {
    alert('Failed to load room: ' + error.message)
    router.push('/lobby')
  }
})
</script>
