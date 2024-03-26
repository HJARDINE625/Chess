package ui;

//import webSocketMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage notification);
}
