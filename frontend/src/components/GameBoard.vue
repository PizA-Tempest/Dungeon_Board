<template>
  <div class="max-w-7xl mx-auto">
    <!-- Game Header -->
    <div class="flex justify-between items-center mb-6">
      <div>
        <h2 class="text-3xl font-bold text-dungeon-accent">🎲 Dungeon Board</h2>
        <p class="text-gray-400">
          Round {{ gameState?.currentRound || 0 }} / {{ gameState?.maxRounds || 10 }}
          <span v-if="isMyTurn" class="text-dungeon-gold ml-4">(Your Turn!)</span>
        </p>
      </div>
      <div class="flex gap-4">
        <div v-if="gameState?.status === 'FINISHED'" class="text-xl font-bold">
          <span class="text-dungeon-gold">Game Over!</span>
          <span class="ml-4">Winner: {{ winnerName }}</span>
        </div>
      </div>
    </div>

    <div class="grid lg:grid-cols-3 gap-6">
      <!-- Left Panel: Players -->
      <div class="space-y-4">
        <div class="card">
          <h3 class="text-xl font-bold mb-4">Players</h3>
          <div class="space-y-3">
            <div
              v-for="(player, index) in gameState?.players"
              :key="player.id"
              :class="[
                'p-3 rounded transition-all',
                index === gameState?.currentPlayerIndex
                  ? 'bg-dungeon-accent ring-2 ring-white'
                  : 'bg-dungeon-blue',
                player.id === gameStore.user?.id ? 'ring-2 ring-dungeon-gold' : ''
              ]"
            >
              <div class="flex justify-between items-start">
                <div>
                  <h4 class="font-bold">{{ player.username }}</h4>
                  <p class="text-sm text-gray-300">{{ player.race }} {{ player.playerClass }}</p>
                </div>
                <div v-if="index === gameState?.currentPlayerIndex" class="text-2xl">🎯</div>
              </div>
              <div class="mt-2 grid grid-cols-2 gap-2 text-sm">
                <div>❤️ {{ player.currentHp }} / {{ player.maxHp }}</div>
                <div>💰 {{ player.gold }} gold</div>
                <div>📍 Position: {{ player.position }}</div>
                <div>⭐ Score: {{ player.score }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Event Log -->
        <div class="card max-h-64 overflow-y-auto">
          <h3 class="text-xl font-bold mb-4">📜 Event Log</h3>
          <div class="space-y-1 text-sm">
            <div v-for="(log, index) in recentLogs" :key="index" class="text-gray-300">
              {{ log }}
            </div>
          </div>
        </div>
      </div>

      <!-- Center Panel: Game Board -->
      <div class="card">
        <h3 class="text-xl font-bold mb-4">Board</h3>

        <!-- Dice Roll Section -->
        <div class="mb-6 p-4 bg-dungeon-dark rounded text-center">
          <div v-if="isMyTurn && gameState?.status === 'IN_PROGRESS'">
            <p class="mb-4 text-lg">Your turn! Roll the dice to move.</p>

            <div v-if="!hasRolled" class="space-y-3">
              <button
                @click="rollDice"
                class="btn-primary text-xl px-8 py-4"
                :disabled="loading"
              >
                🎲 Roll Dice
              </button>

              <button
                v-if="canReroll"
                @click="reroll"
                class="btn-secondary"
                :disabled="loading"
              >
                🔄 Reroll (Halfling Ability)
              </button>
            </div>

            <div v-else class="text-gray-400">
              Waiting for turn to complete...
            </div>
          </div>

          <div v-else-if="gameState?.status === 'IN_PROGRESS'" class="text-gray-400">
            {{ currentPlayerName }}'s turn...
          </div>

          <div v-if="lastDiceRoll" class="mt-4 text-2xl font-bold text-dungeon-gold">
            Rolled: {{ lastDiceRoll }}
          </div>
        </div>

        <!-- Board Visualization -->
        <div class="grid grid-cols-6 gap-2">
          <div
            v-for="tile in gameState?.tiles || []"
            :key="tile.position"
            :class="[
              'aspect-square rounded flex items-center justify-center text-2xl transition-all cursor-pointer',
              getTileClass(tile.type),
              isPlayerOnTile(tile.position) ? 'ring-2 ring-dungeon-gold' : ''
            ]"
            :title="tile.description"
          >
            {{ getTileIcon(tile.type) }}
            <div v-if="isPlayerOnTile(tile.position)" class="absolute bottom-1 right-1 text-xs">
              {{ getPlayersOnTile(tile.position) }}
            </div>
          </div>
        </div>

        <!-- Legend -->
        <div class="mt-4 grid grid-cols-3 gap-2 text-xs">
          <div>⬜ Normal</div>
          <div>👹 Monster</div>
          <div>💎 Treasure</div>
          <div>⚠️ Trap</div>
          <div>🌀 Portal</div>
          <div>❓ Event</div>
          <div>🏪 Shop</div>
          <div>🏁 Start</div>
        </div>
      </div>

      <!-- Right Panel: Actions & Info -->
      <div class="space-y-4">
        <!-- Character Actions -->
        <div class="card">
          <h3 class="text-xl font-bold mb-4">Actions</h3>

          <div v-if="myPlayer" class="space-y-3">
            <div class="p-3 bg-dungeon-dark rounded">
              <h4 class="font-bold text-dungeon-gold">{{ myPlayer.race }} {{ myPlayer.playerClass }}</h4>
              <p class="text-sm text-gray-400 mt-1">
                {{ getClassDescription(myPlayer.playerClass) }}
              </p>
            </div>

            <button
              @click="useAbility"
              class="btn-secondary w-full"
              :disabled="!isMyTurn || hasUsedAbility"
            >
              ✨ Use Class Ability
              <span v-if="hasUsedAbility" class="text-xs block">(Used this turn)</span>
            </button>

            <button
              @click="addBot"
              v-if="canAddBot"
              class="btn-secondary w-full"
            >
              🤖 Add Bot to Room
            </button>
          </div>
        </div>

        <!-- Game Events -->
        <div class="card">
          <h3 class="text-xl font-bold mb-4">Recent Events</h3>
          <div v-if="gameState?.lastEvent" class="p-3 bg-dungeon-dark rounded">
            {{ gameState.lastEvent }}
          </div>
          <div v-else class="text-gray-400 text-center">
            No events yet
          </div>
        </div>

        <!-- Scores -->
        <div class="card">
          <h3 class="text-xl font-bold mb-4">Leaderboard</h3>
          <div class="space-y-2">
            <div
              v-for="(player, index) in sortedPlayers"
              :key="player.id"
              class="flex justify-between p-2 rounded"
              :class="index === 0 ? 'bg-dungeon-gold text-dungeon-dark' : 'bg-dungeon-blue'"
            >
              <span>{{ index + 1 }}. {{ player.username }}</span>
              <span class="font-bold">{{ player.score }} pts</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useGameStore } from '@/stores/game'
import { ws } from '@/services/websocket'

const router = useRouter()
const route = useRoute()
const gameStore = useGameStore()

const roomId = route.params.roomId
const loading = ref(false)
const hasRolled = ref(false)
const hasUsedAbility = ref(false)
const lastDiceRoll = ref(0)

const gameState = computed(() => gameStore.gameState)
const isMyTurn = computed(() => gameStore.isCurrentPlayerTurn)
const myPlayer = computed(() => gameStore.getCurrentPlayer())

const currentPlayerName = computed(() => {
  if (!gameState.value) return ''
  const currentPlayer = gameState.value.players[gameState.value.currentPlayerIndex]
  return currentPlayer?.username || ''
})

const winnerName = computed(() => {
  if (!gameState.value?.winnerId) return ''
  const winner = gameState.value.players.find(p => p.id.toString() === gameState.value.winnerId)
  return winner?.username || ''
})

const sortedPlayers = computed(() => {
  if (!gameState.value?.players) return []
  return [...gameState.value.players].sort((a, b) => b.score - a.score)
})

const recentLogs = computed(() => {
  // In a real app, you'd store logs in the store
  return []
})

const canReroll = computed(() => {
  return myPlayer.value?.race === 'Halfling' && hasRolled.value && !hasUsedAbility.value
})

const canAddBot = computed(() => {
  return gameStore.currentRoom?.hostId === gameStore.user?.id &&
         (gameState.value?.players?.length || 0) < 4
})

async function rollDice() {
  loading.value = true
  hasRolled.value = true

  try {
    const result = await gameStore.rollDice(roomId)
    lastDiceRoll.value = result.data
  } catch (error) {
    alert('Failed to roll dice: ' + error.message)
  } finally {
    loading.value = false
  }
}

async function reroll() {
  loading.value = true

  try {
    const result = await gameStore.reroll(roomId)
    lastDiceRoll.value = result.data
  } catch (error) {
    alert('Failed to reroll: ' + error.message)
  } finally {
    loading.value = false
  }
}

async function useAbility() {
  loading.value = true

  try {
    await gameStore.useAbility(roomId)
    hasUsedAbility.value = true
  } catch (error) {
    alert('Failed to use ability: ' + error.message)
  } finally {
    loading.value = false
  }
}

async function addBot() {
  try {
    await gameStore.addBot(roomId)
  } catch (error) {
    alert('Failed to add bot: ' + error.message)
  }
}

function getTileClass(type) {
  const classes = {
    'Normal': 'bg-gray-700',
    'Monster': 'bg-red-900',
    'Treasure': 'bg-yellow-900',
    'Trap': 'bg-orange-900',
    'Portal': 'bg-purple-900',
    'Event': 'bg-pink-900',
    'Shop': 'bg-green-900',
    'Start': 'bg-blue-900'
  }
  return classes[type] || 'bg-gray-700'
}

function getTileIcon(type) {
  const icons = {
    'Normal': '⬜',
    'Monster': '👹',
    'Treasure': '💎',
    'Trap': '⚠️',
    'Portal': '🌀',
    'Event': '❓',
    'Shop': '🏪',
    'Start': '🏁'
  }
  return icons[type] || '⬜'
}

function isPlayerOnTile(position) {
  if (!gameState.value?.players) return false
  return gameState.value.players.some(p => p.position === position)
}

function getPlayersOnTile(position) {
  if (!gameState.value?.players) return ''
  const players = gameState.value.players.filter(p => p.position === position)
  return players.map(p => p.username.charAt(0)).join('')
}

function getClassDescription(playerClass) {
  const descriptions = {
    'Warrior': '+1 damage, shield once per match',
    'Mage': 'Fireball (3 tiles range)',
    'Rogue': 'Steal gold, dodge chance',
    'Cleric': 'Heal self, remove curses',
    'Ranger': 'Critical on 6, move through enemies',
    'Paladin': 'Smite evil, protect ally',
    'Bard': 'Buff all players',
    'Necromancer': 'Summon minion, drain life'
  }
  return descriptions[playerClass] || ''
}

// Listen for WebSocket events
ws.on('GAME_STATE', (data) => {
  if (data.gameState) {
    gameStore.gameState = data.gameState
    // Reset turn state when it's our turn again
    if (gameStore.isCurrentPlayerTurn) {
      hasRolled.value = false
      hasUsedAbility.value = false
    }
  }
})

onMounted(async () => {
  try {
    // Connect to room WebSocket
    gameStore.connectToRoom(roomId)

    // Load initial game state
    await gameStore.getGameState(roomId)
  } catch (error) {
    alert('Failed to load game state: ' + error.message)
    router.push('/lobby')
  }
})

onUnmounted(() => {
  gameStore.disconnectWebSocket()
})
</script>
