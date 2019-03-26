package command_features;

public enum Command {
    CMD_PING("^#(p|ping):?"),
    CMD_ECHO("^#(e|echo):?\\s(([A-Za-z0-9_])?)+;"),
    CMD_LOGIN("^#(log|login):?\\s((l|log|login)(-|\\s)([A-Za-z0-9_])+)\\s((p|pas|password)(-|\\s)([A-Za-z0-9_])+)+;"),
    CMD_LIST("^#list:?"),
//    CMD_MSG("^#msg:?\\s((du|destUser|destinationUser)(-|\\s)([A-Za-z0-9_])+)\\s(t|text)(-|\\s)([A-Za-z0-9_])+)+;"),
    CMD_MSG("^#msg:?\\s(([A-Za-z0-9_])?)+;"),
    CMD_FILE("^#(f|file):?\\s(f|fileName)(-|\\s)(.)+;"),
    CMD_RECIVE_MSG("^#(rm|recive.msg):?"),
    CMD_RECIVE_FILE("^#(rf|recive.file):?"),
    EXIT("^#(e|exit):");

    private final String regex;

    Command(final String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}
