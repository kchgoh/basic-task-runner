package basictaskrunner;

import static org.mockito.Mockito.*;

import org.junit.Test;

public class PatternMatchServiceTest {

	@Test
	public void test() {
		OutputSink sink = mock(OutputSink.class);

		PatternMatchService.run(sink, "(\\d+)x", "ab123xx");
		
		verify(sink).onUpdate("Match 0:123x");
		verify(sink).onUpdate("Match 1:123");
	}

}
