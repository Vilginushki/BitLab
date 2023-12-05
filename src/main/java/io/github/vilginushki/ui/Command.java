package io.github.vilginushki.ui;

import io.github.vilginushki.enums.UserCommandMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Getter
@Setter
@Slf4j
public class Command {
    private String rawCommand;
    private Map<Index, String> commandMap;

    public Command(String command) {
        this.init(command);
    }

    public List<String> getHints() {

        log.warn("Unknown command list of available commands below:");

        return Arrays.stream(UserCommandMap.values())
                .map(UserCommandMap::toString)
                .toList();
    }


    private void init(String command) {
        if (command == null) throw new IllegalStateException("Empty command.");
        this.rawCommand = command;

        String[] data = this.rawCommand.split(" ");
        this.commandMap = new HashMap<>();
        for (int i = 0; i < data.length; i++) {
            try {
                commandMap.put(Index.values()[i], data[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                log.warn("Input can have only 2 words maximum.");
                rawCommand = "";
                break;
            }

        }
    }

    private Optional<String> get() {
        return this.commandMap.get(Index.PARAM) != null ?
                Optional.of(this.commandMap.get(Index.PARAM)) :
                Optional.empty();
    }

    public String getCommand() {
        return this.commandMap.get(Index.COMMAND).toLowerCase();
    }

    public Optional<String> getParam() {
        return this.get();
    }

    public boolean isComplete() {
        return Arrays.stream(UserCommandMap.values())
                .anyMatch(c -> c.toString().toLowerCase().equals(this.getCommand()));
    }

    private enum Index {
        COMMAND, PARAM
    }
}
