# Architecture — Dungeon Board

## System Architecture

Dungeon Board uses a **client-server architecture** with clear separation between frontend SPA and backend API.

### Technology Stack

**Backend:**
- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Authentication**: Spring Security with JWT
- **Real-time**: Spring WebSocket with STOMP messaging
- **Database**: H2 (development) / PostgreSQL (production)
- **Build**: Maven
- **Testing**: JUnit 5, Mockito

**Frontend:**
- **Framework**: Vue.js 3 (recommended) or React
- **Build Tool**: Vite
- **Styling**: Tailwind CSS
- **State Management**: Pinia (Vue) or Zustand (React)
- **Real-time**: Native WebSocket or Stomp.js
- **HTTP Client**: Axios

### Layered Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend SPA                         │
│  Vue.js/React + Tailwind + WebSocket + State Management     │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/WebSocket
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway Layer                         │
│  Controllers (REST) + WebSocket Handlers                   │
│  - Authentication (JWT)                                      │
│  - CORS Configuration                                        │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                     Service Layer                            │
│  Game Logic, Business Rules, State Management               │
│  - AuthService, RoomService, GameService                    │
│  - TurnManager, CombatService, BotAIService                 │
│  - ScoringService, CharacterService                         │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   Data Access Layer                          │
│  Entities, Repositories, DTOs                               │
│  - User, Room, GameState, Player, Board, Tile              │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      Database                                │
│  H2 (dev) / PostgreSQL (prod)                               │
└─────────────────────────────────────────────────────────────┘
```

### Key Design Decisions

**1. Frontend-Backend Separation**
- REST API for CRUD operations (auth, rooms)
- WebSocket for real-time game state synchronization
- Enables independent development and scaling

**2. State Management**
- **Server**: Source of truth for game state
- **Client**: Optimistic UI updates with server confirmation
- **WebSocket**: Broadcast state changes to all connected clients

**3. Smart Bot AI**
- Utility-based decision making
- Evaluate each possible action with scoring function
- Consider: current HP, gold, position, opponents, available abilities
- Use abilities strategically (not randomly)

**4. Round-Based Scoring**
- Server authoritative scoring
- Score = gold + bonus points (monsters defeated, treasures found)
- Round counter tracks game progression
- After 10 rounds, highest score wins

### Communication Patterns

**REST API:**
- POST `/api/auth/register` - User registration
- POST `/api/auth/login` - User login, returns JWT
- POST `/api/rooms` - Create new room
- POST `/api/rooms/{id}/join` - Join existing room
- GET `/api/rooms/{id}` - Get room state
- POST `/api/rooms/{id}/leave` - Leave room

**WebSocket:**
- Subscribe: `/topic/room/{roomId}` - Receive game state updates
- Send: `/app/room/{roomId}/action` - Send player actions
- Events: `GAME_STATE_UPDATE`, `TURN_CHANGED`, `GAME_OVER`

### Data Model Core

**GameState:**
- Room ID, round number, current turn index
- List of players (positions, HP, gold, class, race, items)
- Board layout with tiles
- Game status (WAITING, IN_PROGRESS, FINISHED)

**Player:**
- User ID, character (class + race)
- Position on board, HP, max HP
- Gold, items inventory
- Status (ready, disconnected)

**Board:**
- List of tiles (positions, types, events)
- Tile types: NORMAL, MONSTER, TREASURE, TRAP, PORTAL, EVENT

### Security

- JWT-based authentication
- Password hashing with BCrypt
- CORS configuration for frontend origin
- WebSocket authentication handshake
- Room access authorization (players only)

---

This document is part of the Memory Bank.
Update when architectural decisions change.
