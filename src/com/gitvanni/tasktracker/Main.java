package com.gitvanni.tasktracker;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {



    static String getCommandFromUser(){
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        String command = null;
        try{
            command = inputReader.readLine();
        } catch (IOException e) {
            System.out.println("Error occured:" +e.getMessage());
        }
        return command;
    }


    static boolean isFirstTermOfCommandCorrect(String term){
        return term.equals("task-cli");
    }


    public static void main(String[] args) {



        //1) stampo messaggio di benvenuto e lancio il ciclo while

        System.out.println("Welcome to your Task Manger!");
        System.out.println("Please select your next action!");
        //Both these can be encapsulated in a logger?? It's ugly to have them here as plain text

        while(true){

            //2) l'utente inserisce l'input e segue la verifica della correttezza della sintassi

            //getting command from user, can it be encapsulated in a method perhaps?
           String command = getCommandFromUser();
            CommandSyntaxVerifier commandSyntaxVerifier = new CommandSyntaxVerifier(command);
           System.out.println(commandSyntaxVerifier.isSyntaxCorrect(command));

            if(command == null){
                System.out.println("Please enter again your next action");
                continue;
            }

            //verify first term of command syntax
            //TODO: first need to verify command syntax
            String firstTerm = command.split(" ",2)[0];
            String rest = command.split(" ",2)[1];
            if(!isFirstTermOfCommandCorrect(firstTerm)){
                System.out.println("Invalid command! Please try again");
                continue;
            }


            //creating file if not existing
            String currentDir = Paths.get("")
                    .toAbsolutePath()
                    .toString();

            String fileName = currentDir+"\\taskRepo.json";
            Path filePath = Paths.get(fileName);

            try{
                Files.createFile(filePath);
            }
            catch (IOException e){
                System.out.println("Error with File!");
            }


            //extract second term
            String secondTerm = rest.split(" ",2)[0];
            String remaining = rest.split(" ",2)[1];
            //3) Una volta verificata la corretteza, viene eseguita la dovuta operazione
            //devo estrarre add fuori
            switch(secondTerm){
                case "add":
                    OperationHandler.addTask(remaining);
                    break;
                case "update":

                    break;
                case "delete":

                    break;
                case "mark-in-progress":

                    break;
                case "mark-done":
                    OperationHandler.reconstructTasks(2);
                    break;
                case "list":
                    OperationHandler.listAllTasks();
                    break;
                default:
                    System.out.println("Invalid command! Try again!");
                    continue;
            }
            //4) Si chiede all'utente se desidera uscire, altrimenti si rinizia con il prossimo comando
            System.out.println("Do you wish to exit? Type Y to confirm");

            String wish = getCommandFromUser();
            if(wish.equals("Y"))
                break;
            else
                System.out.println("Please select your next action!");

        }

    }

}
