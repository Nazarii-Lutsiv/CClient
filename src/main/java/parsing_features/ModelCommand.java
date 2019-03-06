package parsing_features;

import java.util.Objects;

public class ModelCommand {
    private String nameCommand;
    private String argCommand;

    public ModelCommand(String nameCommand, String argCommand) {
        this.nameCommand = nameCommand;
        this.argCommand = argCommand;
    }

    public String getNameCommand() {
        return nameCommand;
    }

    public void setNameCommand(String nameCommand) {
        this.nameCommand = nameCommand;
    }

    public String getArgCommand() {
        return argCommand;
    }

    public void setArgCommand(String argCommand) {
        this.argCommand = argCommand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelCommand that = (ModelCommand) o;
        return Objects.equals(nameCommand, that.nameCommand) &&
                Objects.equals(argCommand, that.argCommand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameCommand, argCommand);
    }

    @Override
    public String toString() {
        return "ModelCommand{" +
                "nameCommand='" + nameCommand + '\'' +
                ", argCommand='" + argCommand + '\'' +
                '}';
    }
}
