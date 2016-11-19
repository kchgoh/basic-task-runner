package basictaskrunner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatchService {

	public static void run(OutputSink status, String regex, String input) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		
		while(m.find()) {
			for(int i=0; i <= m.groupCount(); ++i)
				status.onUpdate("Match " + i + ":" + m.group(i));
		}
	}
}
