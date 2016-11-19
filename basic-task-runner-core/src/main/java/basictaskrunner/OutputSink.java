package basictaskrunner;
import java.util.function.Consumer;

public interface OutputSink {
	void onUpdate(String message);
	void onCompleted();
	
	public static OutputSink create(
			Consumer<String> onUpdate, Runnable onCompleted) {
		return new OutputSink() {

			@Override
			public void onUpdate(String message) {
				onUpdate.accept(message);
			}

			@Override
			public void onCompleted() {
				onCompleted.run();
			}
			
		};
	}
}
