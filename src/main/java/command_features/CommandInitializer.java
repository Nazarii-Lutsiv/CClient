package command_features;

public interface CommandInitializer {
    CommandModel ping(String regex);

    CommandModel echo(String regex);

    CommandModel login(String regex);

    CommandModel list(String regex);

}
