package basictaskrunner;

public class StandardOutputSink implements OutputSink {
	
	public final static StandardOutputSink Instance = new StandardOutputSink();

	@Override
	public void onUpdate(String message) {
		System.out.println(message);
	}

	@Override
	public void onCompleted() {
		// TODO Auto-generated method stub
	}

}
