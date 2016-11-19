package basictaskrunner.webapp;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import basictaskrunner.OutputSink;
import basictaskrunner.webapp.commands.CommandRegistry;
import basictaskrunner.webapp.commands.ICommand;
import basictaskrunner.webapp.commands.ICommandRegistry;

class CommandDispatcher {
	private static final Logger LOG = LoggerFactory.getLogger(CommandDispatcher.class);
	
	/**
	 * The key for the web socket session attribute map for storing a reference to the command instance.
	 */
	private static final String CMD_OBJ_KEY = "handler";
	private final ICommandRegistry commandRegistry;
	
	public final int maxConcurrentTasks;
	private int activeCount = 0;
	private final Object countLock = new Object();
	/**
	 * Size of this doesn't really matter. Use {@link #activeCount} to control how many tasks to take.
	 */
	private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(1);
	private final ExecutorService newTaskPool;
	/**
	 * Assumption is cancel task is very quick to do, so just use the default unbounded queue with 1 thread
	 */
	private final ExecutorService cancelTaskPool = Executors.newFixedThreadPool(1);

	public CommandDispatcher(int maxConcurrentTasks) {
		this(maxConcurrentTasks, new CommandRegistry());
	}
	
	public CommandDispatcher(int maxConcurrentTasks, ICommandRegistry commandRegistry) {
		this.maxConcurrentTasks = maxConcurrentTasks;
		newTaskPool = new ThreadPoolExecutor(
				maxConcurrentTasks, maxConcurrentTasks, 0L,
				TimeUnit.MILLISECONDS, taskQueue);
		this.commandRegistry = commandRegistry;
	}
	
	private void trySendToSession(WebSocketSession session, String text) {
		try {
			if(session.isOpen()) {
				session.sendMessage(new TextMessage(text));
			} else {
				LOG.info("Session closed, not sent to client: {}", text);
			}
		} catch(IOException e) {
			LOG.error("Failed to send message '" + text + "' to client", e);
		}
	}
	
	public void dispatch(WebSocketSession session, Map<String, String> actionData) {
		if(actionData.containsKey("cancel")) {
			if(!session.getAttributes().containsKey(CMD_OBJ_KEY)) {
				trySendToSession(session, "No request to cancel");
				return;
			}
			cancelTaskPool.submit(() -> cancelTask(session));
			return;
		}
		
		// GUI should block sending new request when one is already running,
		// so this shouldn't really happen, but add this for safety.
		if(session.getAttributes().containsKey(CMD_OBJ_KEY)) {
			trySendToSession(session, "Request already running. Please cancel first before submit");
			return;
		}
		
		synchronized(countLock) {
			if(activeCount == maxConcurrentTasks) {
				trySendToSession(session, "Server busy. Please retry later");
				return;
			}
			++activeCount;
		}

		
		newTaskPool.submit(() -> {
			try {
				doTask(session, actionData);
			} finally {
				synchronized(countLock) {
					--activeCount;
				}
			}
		});
	}
	
	private void doTask(WebSocketSession session, Map<String, String> actionData) {
		String commandName = actionData.get("command");
		
		if(!commandRegistry.isValidCommand(commandName)) {
			LOG.error("Invalid command: " + commandName);
			trySendToSession(session, "Invalid command: " + commandName);
			return;
		}
		
		ICommand command = commandRegistry.createCommandInstance(commandName);
		session.getAttributes().put(CMD_OBJ_KEY, command);
		try {
			command.start(actionData, OutputSink.create(
					message -> trySendToSession(session, message),
					() -> {}
			));
		} catch(Exception e) {
			session.getAttributes().remove(CMD_OBJ_KEY);
			LOG.error("Task error", e);
			trySendToSession(session, "Error handling task. Check server log.");
			return;
		}
		// because current design is command is block until complete, so by the time
		// we get here, it is done, and we can remove
		session.getAttributes().remove(CMD_OBJ_KEY);
		trySendToSession(session, "Done");
	}
	
	/**
	 * If no task running on that session then just return.
	 * Otherwise cancel the currently running for that session.
	 * @param session
	 */
	public void ensureCancel(WebSocketSession session) {
		if(!session.getAttributes().containsKey(CMD_OBJ_KEY)) {
			return;
		}
		
		cancelTaskPool.submit(() -> cancelTask(session));
	}
	
	private void cancelTask(WebSocketSession session) {
		ICommand command = (ICommand)session.getAttributes().get(CMD_OBJ_KEY);
		try {
			// TODO have to think what best to do if stop fails - should we purge the task anyway, 
			// but have the risk of the task still keep on running
			command.stop();
		} catch(Exception e) {
			LOG.error("Task error", e);
			trySendToSession(session, "Error handling task. Check server log.");
			return;
		}
		session.getAttributes().remove(CMD_OBJ_KEY);
		LOG.info("Command cancelled");
		trySendToSession(session, "Command cancelled");
	}
}
