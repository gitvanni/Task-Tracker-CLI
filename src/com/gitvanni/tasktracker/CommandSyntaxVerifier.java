package com.gitvanni.tasktracker;

public class CommandSyntaxVerifier {

    private final String command;

    public CommandSyntaxVerifier(String command){
        this.command = command;
    }

    public boolean isSyntaxCorrect(String command){

        if(command == null)
            return false;

        String[] firstTermSplit = command.split(" ",2);
        String firstTerm = firstTermSplit[0];


        if(!isFirstTermCorrect(firstTerm))
            return false;

        if(firstTermSplit.length<2)
            return false;

        String remainingAfterFirstTerm = firstTermSplit[1];


        String[] secondTermSplit = remainingAfterFirstTerm.split(" ",2);
        String secondTerm = secondTermSplit[0];

        if(!isSecondTermCorrect(secondTerm))
            return false;

        //If there is no third term and the command is NOT list, then the syntax is wrong
        if(secondTermSplit.length<2 && !secondTerm.equals("list"))
            return false;

        String remainingAfterSecondTerm = secondTermSplit[1];

        switch(secondTerm){
            case "add":
                return isDescription(remainingAfterSecondTerm);
            case "update":
                String[] thirdSplit = remainingAfterSecondTerm.split(" ",2);
                String thirdTerm = thirdSplit[0];
                if(thirdSplit.length<2)
                    return false;
                if(!isID(thirdTerm))
                    return false;
                String fourthTerm = thirdSplit[1];
                return isDescription(fourthTerm);
            case "delete":
            case "mark-in-progress":
            case "mark-done":
                return isID(remainingAfterSecondTerm);
            case "list":
                return isProperTaskStatus(remainingAfterSecondTerm);
            default:
                return false;
        }
    }



    private boolean isFirstTermCorrect(String firstTerm){
        return firstTerm.equals("task-cli");
    }

    private boolean isSecondTermCorrect(String secondTerm){
        return secondTerm.equals("add") ||
                secondTerm.equals("update") ||
                secondTerm.equals("delete") ||
                secondTerm.equals("mark-in-progress") ||
                secondTerm.equals("mark-done") ||
                secondTerm.equals("list");
    }

    private boolean isDescription(String text) {
        return text.startsWith("\"") && text.endsWith("\"");
    }

    private boolean isID(String id){
        return id.matches("\\d+");
    }

    private boolean isProperTaskStatus(String status){
        return status.equals("done") || status.equals("todo") || status.equals("in-progress");
    }

}
