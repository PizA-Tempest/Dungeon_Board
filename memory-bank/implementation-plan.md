# Implementation Plan — Dungeon Board

## Phase 1: Project Foundation
**Goal**: Working Spring Boot project with basic structure

### Backend Setup
1. Initialize Spring Boot project with Spring Initializr
2. Configure Maven (pom.xml) with dependencies
3. Create package structure
4. Set up application.properties
5. Verify project runs with basic "Hello World" endpoint

**Files Created:**
- `backend/pom.xml`
- `backend/src/main/java/com/dungeonboard/DungeonBoardApplication.java`
- `backend/src/main/resources/application.properties`

**Duration**: 1-2 hours

---

## Phase 2: Authentication & User System
**Goal**: Users can register and log in

### Backend
1. Create User entity with username, password, email
2. Set up Spring Security with JWT
3. Implement UserService with password hashing (BCrypt)
4. Create AuthController with register/login endpoints
5. Add JWT utility class
6. Configure security filters

### Frontend Setup
1. Initialize Vue.js/React project with Vite
2. Configure Tailwind CSS
3. Set up Axios for HTTP requests
4. Create Login and Register components
5. Implement JWT storage (localStorage/cookie)
6. Add authentication guard (route protection)

**Files Created:**
- `backend/src/main/java/com/dungeonboard/model/User.java`
- `backend/src/main/java/com/dungeonboard/repository/UserRepository.java`
- `backend/src/main/java/com/dungeonboard/service/AuthService.java`
- `backend/src/main/java/com/dungeonboard/controller/AuthController.java`
- `backend/src/main/java/com/dungeonboard/config/SecurityConfig.java`
- `backend/src/main/java/com/dungeonboard/config/JwtUtil.java`
- `frontend/src/components/Login.vue`
- `frontend/src/components/Register.vue`
- `frontend/src/services/api.js`

**Acceptance**: Users can register, log in, receive JWT token, access protected routes

**Duration**: 4-6 hours

---

## Phase 3: Room System
**Goal**: Users can create and join rooms

### Backend
1. Create Room entity (roomId, players[], maxPlayers, status)
2. Implement RoomService with create/join/leave logic
3. Add RoomController with REST endpoints
4. Set up WebSocket configuration
5. Create WebSocket handler for real-time updates

### Frontend
1. Create Lobby component (room list)
2. Create CreateRoom component
3. Implement WebSocket composable (connect/disconnect/message handling)
4. Add room state management (Pinia/Zustand)
5. Create real-time player list updates

**Files Created:**
- `backend/src/main/java/com/dungeonboard/model/Room.java`
- `backend/src/main/java/com/dungeonboard/repository/RoomRepository.java`
- `backend/src/main/java/com/dungeonboard/service/RoomService.java`
- `backend/src/main/java/com/dungeonboard/controller/RoomController.java`
- `backend/src/main/java/com/dungeonboard/config/WebSocketConfig.java`
- `backend/src/main/java/com/dungeonboard/handler/GameWebSocketHandler.java`
- `frontend/src/components/Lobby.vue`
- `frontend/src/components/CreateRoom.vue`
- `frontend/src/composables/useWebSocket.js`
- `frontend/src/stores/room.js`

**Acceptance**: Users can create rooms, see room list, join rooms, see other players in real-time

**Duration**: 6-8 hours

---

## Phase 4: Character System
**Goal**: Players can select from 8 classes × 8 races

### Backend
1. Define Class enum (8 classes with abilities)
2. Define Race enum (8 races with passives)
3. Create Character entity combining class + race
4. Implement CharacterFactory
5. Add character selection endpoints
6. Calculate starting stats based on class + race

### Frontend
1. Create CharacterSelect component
2. Display all 8 classes with descriptions
3. Display all 8 races with descriptions
4. Show combined stats preview
5. Implement character selection UI
6. Add ability/passive descriptions

**Files Created:**
- `backend/src/main/java/com/dungeonboard/model/character/Class.java`
- `backend/src/main/java/com/dungeonboard/model/character/Race.java`
- `backend/src/main/java/com/dungeonboard/model/character/Character.java`
- `backend/src/main/java/com/dungeonboard/service/CharacterService.java`
- `frontend/src/components/CharacterSelect.vue`

**Classes (8):**
Warrior, Mage, Rogue, Cleric, Ranger, Paladin, Bard, Necromancer

**Races (8):**
Human, Elf, Dwarf, Orc, Halfling, Tiefling, Dragonborn, Goblin

**Acceptance**: All 64 class+race combinations are selectable and display correct stats

**Duration**: 6-8 hours

---

## Phase 5: Board & Movement System
**Goal**: Basic board with dice-based movement

### Backend
1. Create Board entity with tile layout (24 tiles)
2. Define Tile enum (NORMAL, MONSTER, TREASURE, TRAP, PORTAL, EVENT)
3. Implement DiceService (1-6 roll)
4. Create MovementService (validate moves, update position)
5. Add game state management
6. Broadcast position updates via WebSocket

### Frontend
1. Create GameBoard component
2. Render board layout (circular or linear)
3. Display player tokens on correct tiles
4. Create DiceRoll component (animation)
5. Show movement animation
6. Display current turn indicator

**Files Created:**
- `backend/src/main/java/com/dungeonboard/model/board/Board.java`
- `backend/src/main/java/com/dungeonboard/model/board/Tile.java`
- `backend/src/main/java/com/dungeonboard/service/DiceService.java`
- `backend/src/main/java/com/dungeonboard/service/MovementService.java`
- `backend/src/main/java/com/dungeonboard/model/GameState.java`
- `frontend/src/components/GameBoard.vue`
- `frontend/src/components/DiceRoll.vue`

**Acceptance**: Players roll dice, move correct number of tiles, see position update in real-time

**Duration**: 8-10 hours

---

## Phase 6: Tile Events & Combat System
**Goal**: Tiles trigger events (combat, loot, traps)

### Backend
1. Define Monster entities with HP, damage, gold reward
2. Implement CombatService (player attack vs monster)
3. Create LootService (treasure tables)
4. Implement TrapService (damage/effects)
5. Add EventCard system (chaos/bless cards)
6. Process tile events when player lands

### Frontend
1. Create CombatModal (show battle result)
2. Create LootNotification (show treasure found)
3. Create TrapNotification (show damage)
4. Create EventLog (game history)
5. Add sound effects (optional)
6. Add visual feedback for events

**Files Created:**
- `backend/src/main/java/com/dungeonboard/model/monster/Monster.java`
- `backend/src/main/java/com/dungeonboard/service/CombatService.java`
- `backend/src/main/java/com/dungeonboard/service/LootService.java`
- `backend/src/main/java/com/dungeonboard/model/event/EventCard.java`
- `frontend/src/components/modals/CombatModal.vue`
- `frontend/src/components/EventLog.vue`

**Acceptance**: Landing on monster tile triggers combat, treasure gives gold, traps deal damage

**Duration**: 10-12 hours

---

## Phase 7: Turn Management & Scoring
**Goal**: Complete game loop with rounds and scoring

### Backend
1. Implement TurnManager (track current turn, cycle players)
2. Add round counter (10 rounds per game)
3. Create ScoringService (calculate scores)
4. Implement win condition (after 10 rounds, highest score wins)
5. Add GameManager to orchestrate flow
6. Handle player disconnect/reconnect

### Frontend
1. Display round counter
2. Display current turn indicator
3. Show player scores
4. Create GameOver modal (winner announcement)
5. Add play again button

**Files Created:**
- `backend/src/main/java/com/dungeonboard/service/TurnManager.java`
- `backend/src/main/java/com/dungeonboard/service/ScoringService.java`
- `backend/src/main/java/com/dungeonboard/service/GameManager.java`
- `frontend/src/components/modals/GameOver.vue`
- `frontend/src/components/PlayerCard.vue` (with score display)

**Acceptance**: Game progresses through turns and rounds, ends after 10 rounds, shows winner

**Duration**: 6-8 hours

---

## Phase 8: Smart Bot AI
**Goal**: Bots play strategically with abilities

### Backend
1. Design utility-based AI system
2. Implement decision scoring function
3. Add BotAIService with strategy logic
4. Implement ability usage logic (when to use which ability)
5. Add bot difficulty levels (optional)
6. Integrate bots into game flow

**AI Strategy:**
- Evaluate each possible action (move, ability usage)
- Score based on: gold gain, HP preservation, position advantage
- Use abilities when score impact is significant
- Consider opponent positions and threats
- Adapt to game state (early/mid/late game)

**Files Created:**
- `backend/src/main/java/com/dungeonboard/service/BotAIService.java`
- `backend/src/main/java/com/dungeonboard/model/bot/BotDecision.java`

**Acceptance**: Bots make smart moves, use abilities effectively, provide challenging gameplay

**Duration**: 12-16 hours

---

## Phase 9: Polish & Testing
**Goal**: Fun, bug-free experience

### Tasks
1. Balance all 8 classes and 8 races
2. Adjust combat math (damage/HP ratios)
3. Tune loot tables
4. Add visual polish (animations, transitions)
5. Extensive playtesting (solo, multiplayer, bots)
6. Bug fixes and optimization
7. Add loading states
8. Improve error handling
9. Add tutorial/help tooltips
10. Performance testing

**Duration**: 8-12 hours

---

## Total Estimated Duration

**Backend Development**: 50-65 hours
**Frontend Development**: 40-50 hours
**Testing & Polish**: 10-15 hours

**Total**: **100-130 hours** (~3-4 weeks for solo developer)

---

## Dependencies Between Phases

Phase 1 → All phases
Phase 2 → Phase 3, 4, 5, 6, 7, 8
Phase 3 → Phase 4, 5, 6, 7, 8
Phase 4 → Phase 5, 6, 7, 8
Phase 5 → Phase 6, 7, 8
Phase 6 → Phase 7, 8
Phase 7 → Phase 8
Phase 8 → Phase 9

---

This document is part of the Memory Bank.
Update as implementation progresses or plans change.
