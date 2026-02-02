package com.dungeonboard.handler;

import com.dungeonboard.dto.GameEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(GameWebSocketHandler.class);
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToRoomMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("WebSocket connection established: {}", session.getId());
        sessions.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        logger.info("Received message: {}", payload);

        try {
            Map<String, Object> messageData = objectMapper.readValue(payload, Map.class);
            String type = (String) messageData.get("type");
            String roomId = (String) messageData.get("roomId");

            // Associate session with room
            if (roomId != null) {
                sessionToRoomMap.put(session.getId(), roomId);
                roomSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(session.getId(), session);
            }

            // Handle different message types
            switch (type) {
                case "JOIN_ROOM" -> handleJoinRoom(session, roomId);
                case "LEAVE_ROOM" -> handleLeaveRoom(session, roomId);
                case "ROLL_DICE" -> handleDiceRoll(session, roomId, messageData);
                case "REROLL" -> handleReroll(session, roomId, messageData);
                case "USE_ABILITY" -> handleUseAbility(session, roomId, messageData);
            }
        } catch (Exception e) {
            logger.error("Error handling message", e);
            sendError(session, "Error processing message: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("WebSocket connection closed: {}", session.getId());

        String roomId = sessionToRoomMap.remove(session.getId());
        if (roomId != null) {
            Map<String, WebSocketSession> room = roomSessions.get(roomId);
            if (room != null) {
                room.remove(session.getId());
                if (room.isEmpty()) {
                    roomSessions.remove(roomId);
                }
            }
        }

        sessions.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("WebSocket transport error", exception);
        session.close();
    }

    private void handleJoinRoom(WebSocketSession session, String roomId) {
        logger.info("Session {} joined room {}", session.getId(), roomId);
        sendToRoom(roomId, GameEvent.playerJoined(null));
    }

    private void handleLeaveRoom(WebSocketSession session, String roomId) {
        logger.info("Session {} left room {}", session.getId(), roomId);
    }

    private void handleDiceRoll(WebSocketSession session, String roomId, Map<String, Object> data) {
        logger.info("Dice roll request from session {} in room {}", session.getId(), roomId);
        // This will be handled by GameService
    }

    private void handleReroll(WebSocketSession session, String roomId, Map<String, Object> data) {
        logger.info("Reroll request from session {} in room {}", session.getId(), roomId);
        // This will be handled by GameService
    }

    private void handleUseAbility(WebSocketSession session, String roomId, Map<String, Object> data) {
        logger.info("Ability use request from session {} in room {}", session.getId(), roomId);
        // This will be handled by GameService
    }

    public void sendToRoom(String roomId, GameEvent event) {
        Map<String, WebSocketSession> room = roomSessions.get(roomId);
        if (room != null) {
            String message;
            try {
                message = objectMapper.writeValueAsString(event);
                TextMessage textMessage = new TextMessage(message);

                for (WebSocketSession session : room.values()) {
                    if (session.isOpen()) {
                        try {
                            session.sendMessage(textMessage);
                        } catch (IOException e) {
                            logger.error("Error sending message to session {}", session.getId(), e);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error serializing game event", e);
            }
        }
    }

    public void sendToSession(WebSocketSession session, GameEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            logger.error("Error sending message to session {}", session.getId(), e);
        }
    }

    private void sendError(WebSocketSession session, String errorMessage) {
        try {
            GameEvent errorEvent = GameEvent.error(errorMessage);
            String message = objectMapper.writeValueAsString(errorEvent);
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            logger.error("Error sending error message", e);
        }
    }

    public int getSessionCountInRoom(String roomId) {
        Map<String, WebSocketSession> room = roomSessions.get(roomId);
        return room != null ? room.size() : 0;
    }

    public boolean isPlayerInRoom(String roomId, String sessionId) {
        Map<String, WebSocketSession> room = roomSessions.get(roomId);
        return room != null && room.containsKey(sessionId);
    }
}
