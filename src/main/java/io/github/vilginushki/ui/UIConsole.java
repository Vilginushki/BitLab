package io.github.vilginushki.ui;

import io.github.vilginushki.enums.UserCommandMap;

import java.util.Scanner;


public class UIConsole {
    private static final Scanner keyboard = new Scanner(System.in);

    public static String read() {

        while (true) {
            //wait 1 second
            try {
                Thread.sleep(100); // Not the best solution, but it makes console output prettier
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.print("\n> ");
            Command command = new Command(keyboard.nextLine());

            if (command.isComplete()) {
                if (command.getParam().isPresent()) {
                    if (command.getParam().get().equals("-h") || command.getParam().get().equals("help"))
                        printToUI(UserCommandMap.getInfo(command.getCommand()));
                    else printToUI("Unknown parameter.");
                } else
                    return command.getCommand();
            } else {
                printToUI(command.getHints().toString());
            }
        }
    }

    public static void printToUI(String message) {
        System.out.println(message);
    }
}
