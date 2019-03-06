package parsing_features;

import java.util.Map;

public interface CommandParser {
    ModelCommand parsCommand(String regex);
}
