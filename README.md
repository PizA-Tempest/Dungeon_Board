# Dungeon Board

A turn-based fantasy board game RPG inspired by Dungeons & Dragons, Monopoly, and party games. Players move around a dungeon-themed board, roll dice, face monsters, trigger events, and grow stronger through classes, races, items, and choices.

## 🚀 Quick Deploy

Want to play online? Deploy your own instance:

- **Backend**: Deploy to [Render](https://render.com) (Free tier available) - See [DEPLOYMENT.md](DEPLOYMENT.md) for instructions
- **Frontend**: Deploy to [Vercel](https://vercel.com) (Free hosting) - See [DEPLOYMENT.md](DEPLOYMENT.md) for instructions

📖 **Full deployment guide**: [DEPLOYMENT.md](DEPLOYMENT.md)

## Features

- **User Authentication**: JWT-based authentication system
- **Room System**: Create or join private rooms with friends
- **Multiplayer Support**: 1-4 players per room (humans or smart AI bots)
- **8 Character Classes**: Warrior, Mage, Rogue, Cleric, Ranger, Paladin, Bard, Necromancer
- **8 Character Races**: Human, Elf, Dwarf, Orc, Halfling, Tiefling, Dragonborn, Goblin
- **Board-Based Movement**: 24-tile circular board with dice rolls
- **Tile Events**: Combat, loot, traps, portals, and special events
- **Smart Bot AI**: Strategic decision-making with utility-based AI
- **Real-time Updates**: WebSocket for live game state synchronization
- **Round-Based Scoring**: 10 rounds per game, highest score wins

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring WebSocket
- H2 Database (in-memory)
- Maven

### Frontend
- Vue.js 3
- Vite
- Tailwind CSS
- Pinia (State Management)
- Vue Router
- WebSocket Client

## Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- Maven (comes with most Java installations)

## Running the Project

### 1. Start the Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 2. Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will start on `http://localhost:5173`

### 3. Access the Game

Open your browser and navigate to:
```
http://localhost:5173
```

## How to Play

1. **Register/Login**: Create an account or log in
2. **Create/Join Room**: Create a new room or join an existing one
3. **Select Character**: Choose your class and race combination
4. **Start Game**: The host starts the game when all players are ready
5. **Roll Dice**: Click "Roll Dice" on your turn to move around the board
6. **Tile Events**: Land on different tiles to trigger events:
   - 👹 **Monster**: Fight monsters for gold rewards
   - 💎 **Treasure**: Find gold (Dwarves get +2 bonus)
   - ⚠️ **Trap**: Take damage (Goblins are immune)
   - 🌀 **Portal**: Teleport to a random location
   - ❓ **Event**: Random blessing or curse cards
   - 🏪 **Shop**: Buy items and upgrades
7. **Use Abilities**: Activate your class ability for strategic advantage
8. **Win**: After 10 rounds, the player with the highest score wins!

## Character Classes

| Class | Description | Stats |
|-------|-------------|-------|
| Warrior | +1 damage, shield once per match | HP: 100, ATK: 15, DEF: 10 |
| Mage | Cast fireball (3 tiles range) | HP: 80, ATK: 20, DEF: 8 |
| Rogue | Steal gold, dodge chance | HP: 70, ATK: 18, DEF: 12 |
| Cleric | Heal self, remove curses | HP: 90, ATK: 15, DEF: 10 |
| Ranger | Critical hit on 6 roll | HP: 85, ATK: 17, DEF: 11 |
| Paladin | Smite evil monsters | HP: 95, ATK: 14, DEF: 12 |
| Bard | Buff all players (chaos) | HP: 75, ATK: 16, DEF: 9 |
| Necromancer | Summon minion, drain life | HP: 70, ATK: 22, DEF: 7 |

## Character Races

| Race | Bonus |
|------|-------|
| Human | +1 to all rolls |
| Elf | +1 movement on 5-6 roll |
| Dwarf | +2 gold from treasures |
| Orc | +2 damage, -1 defense |
| Halfling | Can reroll once per turn |
| Tiefling | +50% curse effectiveness |
| Dragonborn | Breath weapon (3-tile cone) |
| Goblin | Steal bonus, trap immunity |

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Register
- `POST /api/auth/logout` - Logout
- `GET /api/auth/me` - Get current user

### Rooms
- `POST /api/room/create` - Create room
- `POST /api/room/{roomId}/join` - Join room
- `POST /api/room/{roomId}/leave` - Leave room
- `GET /api/room/{roomId}` - Get room info
- `GET /api/room/list` - List available rooms
- `POST /api/room/{roomId}/start` - Start game
- `POST /api/room/{roomId}/add-bot` - Add bot to room

### Game
- `POST /api/game/{roomId}/roll` - Roll dice
- `POST /api/game/{roomId}/reroll` - Reroll dice
- `POST /api/game/{roomId}/character` - Select character
- `POST /api/game/{roomId}/ability` - Use class ability
- `GET /api/game/{roomId}/state` - Get game state
- `GET /api/game/classes` - List all classes
- `GET /api/game/races` - List all races

## WebSocket

Connect to: `ws://localhost:8080/ws/game`

Send messages:
```json
{
  "type": "JOIN_ROOM",
  "roomId": "abc123"
}
```

Events:
- `GAME_STATE` - Game state update
- `PLAYER_JOINED` - Player joined room
- `PLAYER_LEFT` - Player left room
- `GAME_STARTED` - Game started
- `DICE_ROLLED` - Dice was rolled
- `PLAYER_MOVED` - Player moved
- `COMBAT_RESULT` - Combat result
- `TREASURE_FOUND` - Treasure found
- `TRAP_TRIGGERED` - Trap triggered
- `EVENT_CARD` - Event card drawn
- `GAME_OVER` - Game over

## Development

### Backend Structure
```
backend/src/main/java/com/dungeonboard/
├── config/          # Security, WebSocket, JWT configuration
├── controller/      # REST API controllers
├── dto/             # Data transfer objects
├── handler/         # WebSocket handler
├── model/           # Entity classes
│   ├── board/       # Board and Tile models
│   ├── character/   # Character classes
│   └── event/       # Event cards
├── repository/      # JPA repositories
└── service/         # Business logic
```

### Frontend Structure
```
frontend/src/
├── assets/styles/   # CSS styles
├── components/      # Vue components
├── composables/     # Vue composables
├── router/          # Vue Router config
├── services/        # API and WebSocket clients
└── stores/          # Pinia stores
```

## License

This project is for educational purposes.

## Credits

Developed as a greenfield project following the vibe-first, MVP-driven approach.
