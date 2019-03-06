package parsing_features;

public enum Command {
    PING("^ping:?(\\s([0-2]?\\d\\d?)\\.([0-2]?\\d\\d?)\\.([0-2]?\\d\\d?)\\.([0-2]?\\d\\d?))?"),
    ECHO("^echo:?\\s([A-Za-z0-9_])+$"),
    LOGIN("^login:?\\s((l|log|login)(-|\\s)([A-Za-z0-9_])+)\\s((p|pas|password)(-|\\s)([A-Za-z0-9_])+)"),
    LIST("^list:"),
    MSG("^msg:?\\s((du|destUser|destinationUser)(-|\\s)([A-Za-z0-9_])+)\\s(t|text)(-|\\s)([A-Za-z0-9_])+)"),
    FILE("^file:?\\s((du|destUser|destinationUser)(-|\\s)([A-Za-z0-9_])+)\\s(f|fileName)(-|\\s)([A-Za-z0-9_])+)"),
    EXIT("^(e|exit):");

    private final String regex;

    Command(final String regex) {
        this.regex = regex;
    }
}
