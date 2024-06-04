package me.play.ws;

import io.quarkus.websockets.next.*;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket(path = "/chat/{username}")
public class ChatWebSocket {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocket.class);

    // Declare the type of messages that can be sent and received
    public enum MessageType {USER_JOINED, USER_LEFT, CHAT_MESSAGE}

    public record ChatMessage(MessageType type, String from, String message) {
    }

    @Inject
    WebSocketConnection connection;

    @OnOpen(broadcast = true)
    public ChatMessage onOpen(@PathParam("username") String username) {
        log.info("onOpen with username [{}]", username);
        return new ChatMessage(MessageType.USER_JOINED, username, null);
    }

    @OnClose
    public void onClose(@PathParam("username") String username) {
        ChatMessage departure = new ChatMessage(MessageType.USER_LEFT, username, null);
        connection.broadcast().sendTextAndAwait(departure);
        log.info("onClose with username [{}]", username);
    }

    @OnTextMessage(broadcast = true)
    public ChatMessage onMessage(@PathParam("username") String username, ChatMessage message) {
        log.info("onMessage with username [{}] and message [{}]", username, message);
        return message;
    }

}
