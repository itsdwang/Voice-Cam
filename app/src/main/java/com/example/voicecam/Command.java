package com.example.voicecam;

public class Command {
    private String command;
    private String action;

    public static final String TAKE_PHOTO = "Take photo";
    public static final String OPEN_GALLERY = "Open gallery";
    public static final String TOGGLE_FLASH = "Toggle flash";

    public static String[] allActions = {Command.TAKE_PHOTO, Command.OPEN_GALLERY, Command.TOGGLE_FLASH};

    public Command(String command, String action) {
        this.command = "\"" + command + "\"";
        this.action = action;
    }

    public String getCommand() {
        return command;
    }

    public String getAction() {
        return action;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
