package basictaskrunner.webapp;

import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.socket.WebSocketSession;

import basictaskrunner.OutputSink;
import basictaskrunner.webapp.commands.ICommand;
import basictaskrunner.webapp.commands.ICommandRegistry;

public class CommandDispatcherTest {

	@Test
	public void test() throws Exception {
		WebSocketSession session = mock(WebSocketSession.class);
		ICommandRegistry registry = mock(ICommandRegistry.class);
		
		final int numTasks = 2;
		CountDownLatch latch = new CountDownLatch(numTasks);
		
		final String testCommandName = "test";
		
		ICommand testCommand = new ICommand() {
			@Override
			public void start(Map<String, String> args, OutputSink status) {
				latch.countDown();
			}
			@Override
			public void stop() {
			}
		};
		
		when(registry.createCommandInstance(testCommandName)).thenReturn(testCommand);
		when(registry.isValidCommand(testCommandName)).thenReturn(true);
		
		Map<String, String> actionData = new HashMap<>();
		actionData.put("command", testCommandName);
		
		final int maxConcurrentTasks = 2;
		CommandDispatcher sut = new CommandDispatcher(maxConcurrentTasks, registry);
		
		sut.dispatch(session, actionData);
		sut.dispatch(session, actionData);
		
		if(!latch.await(5, TimeUnit.SECONDS)) {
			Assert.fail("Timed out"); 
		}
	}

}
