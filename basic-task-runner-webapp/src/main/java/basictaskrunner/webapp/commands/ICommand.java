package basictaskrunner.webapp.commands;

import java.util.Map;

import basictaskrunner.OutputSink;

public interface ICommand {
	void start(Map<String, String> args, OutputSink status);
	
	void stop();
}
