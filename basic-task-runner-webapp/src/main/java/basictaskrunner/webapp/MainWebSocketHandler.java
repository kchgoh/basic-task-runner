package basictaskrunner.webapp;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Just does a minimum parse of the incoming message then hands off to {@link CommandDispatcher}
 *
 */
public class MainWebSocketHandler extends TextWebSocketHandler {
	private static final Logger LOG = LoggerFactory.getLogger(MainWebSocketHandler.class);
	
	private final CommandDispatcher dispatcher;
	
	public MainWebSocketHandler(int maxConcurrentTasks) {
		dispatcher = new CommandDispatcher(maxConcurrentTasks);
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		LOG.info("Client connected {}", session.getId());
	}
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		LOG.info(message.getPayload());
		
		Map<String, String> actionData;
		try {
			actionData = new ObjectMapper().readValue(message.getPayload(), new TypeReference<HashMap<String, String>>(){});
		} catch(Exception e) {
			LOG.error("Malformed request", e);
			session.sendMessage(new TextMessage("Malformed request"));
			return;
		}
		
		dispatcher.dispatch(session, actionData);
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		LOG.info("Client disconnected {}", session.getId());
		dispatcher.ensureCancel(session);
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable e) throws Exception {
		LOG.error("transport error", e);
	}
}