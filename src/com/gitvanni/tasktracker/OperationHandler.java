package com.gitvanni.tasktracker;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OperationHandler {

    //get the next available id. to use when adding a new task
    static int getNextID(){
        int nextID = 1;

        List<Integer> presentIDs = getPresentIDs();

        if(presentIDs.isEmpty())
            return nextID;

        nextID++;

        for (int next : presentIDs) {
            if (next == nextID) {
                nextID++;
                continue;
            }
            break;
        }

        return nextID;

    }

    static int extractID(String toExtract){
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(toExtract);
        boolean found = matcher.find();
        if(found){
            return Integer.parseInt(matcher.group());
        }
        return -1;
    }

    static int getNumOfTasks(){
        Pattern pattern = Pattern.compile("id");
        String fileName = getFileName();
        Path filePath = Paths.get(fileName);
        int numOfTasks = 0;
        try(Scanner scanner = new Scanner(filePath)) {
            while(scanner.hasNextLine()){
                String next = scanner.nextLine();
                Matcher matcher = pattern.matcher(next);
                if(matcher.find())
                    numOfTasks++;
            }
        }catch (Exception e) {
            System.out.println("BufferedReaderError");
        }
        return numOfTasks;
    }


    static List<Task> reconstructTasks(int numOfTasks){
        Pattern pattern = Pattern.compile(":\\s[a-zA-Z0-9\\s.:\"-]+");
        String fileName = getFileName();
        Path filePath = Paths.get(fileName);
        List<Task> tasks = new ArrayList<>();
        try(Scanner scanner = new Scanner(filePath)) {
            int taskCounter = 0;
            while(taskCounter<numOfTasks){
                int attributeCounter = 0;
                List<String> propertyList = new ArrayList<>();
                while(attributeCounter<5){
                    String next = scanner.nextLine();
                    Matcher matcher = pattern.matcher(next);
                    boolean found = matcher.find();
                    if(found){
                        String property = cleanProperty(matcher.group());
                        propertyList.add(property);
                        attributeCounter++;
                    }
                }
                Task task = reconstructTask(propertyList);
                tasks.add(task);
                taskCounter++;
            }
        } catch (Exception e) {
            System.out.println("BufferedReaderError");
        }
        return tasks;
    }

    private static String cleanProperty(String property) {
        return property.substring(3,property.length()-1);
    }

    private static Task reconstructTask(List<String> propertyList) {
        int id = Integer.parseInt(propertyList.get(0));
        String description = propertyList.get(1);
        TaskStatus status = TaskStatus.valueOf(propertyList.get(2));
        LocalDateTime createdAt = LocalDateTime.parse(propertyList.get(3));
        LocalDateTime updatedAt = LocalDateTime.parse(propertyList.get(4));
        return new Task(id,description,status,createdAt,updatedAt);
    }


    static List<Integer> getPresentIDs(){
        String fileName = getFileName();
        List<String> existingIDs = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            existingIDs = reader.lines().filter(t -> t.contains("\"id\"")).toList();
            reader.close();

        } catch (Exception e) {
            System.out.println("BufferedReaderError");
        }
        List<Integer> presentIDs = new ArrayList<>();

        if(existingIDs == null)
            return presentIDs;

//TODO: method is hardcoded
        for (String s : existingIDs) {
            int usedID = Integer.parseInt(s.substring(15).split("\"")[0]);
            presentIDs.add(usedID);
        }
        return presentIDs;
    }


    static String getFileName(){
        String currentDir = Paths.get("")
                .toAbsolutePath()
                .toString();

        return currentDir + "\\taskRepo.json";
    }


    static boolean isIDPresent(int id) {
        if(getPresentIDs().isEmpty())
            return false;
        return getPresentIDs().contains(id);
    }

    static void addArrayIndent(){
        //open file
        String fileName = getFileName();
        try {
            FileWriter writer = new FileWriter(fileName,false);
            writer.write("[\n");
            writer.flush();
            writer.close();
        }
        catch (IOException e){
            System.out.println("Error with writing to file!");
        }
    }

    static void addClosingIndent(){
        //open file
        String fileName = getFileName();
        try {
            FileWriter writer = new FileWriter(fileName,true);
            writer.append("]");
            writer.flush();
            writer.close();
        }
        catch (IOException e){
            System.out.println("Error with writing to file!");
        }
    }

    static void addComma(){
        //open file
        String fileName = getFileName();
        try {
            RandomAccessFile writer = new RandomAccessFile(fileName,"rw");
            String line = writer.readLine();
            while(!line.equals("    }")){
                line =writer.readLine();
            }
            writer.seek(writer.getFilePointer()-1);
            writer.writeBytes(",\n");
            writer.close();
        }
        catch (IOException e){
            System.out.println("Error with writing to file!");
        }
    }

    static void rewriteExistingContent(){
        //TODO il problema attuale è che riscrivo la stringa e non la task, quindi mancano gli spazi e gli indent
        String fileName = getFileName();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            List<String> lines = reader.lines().toList();
            List<String> existingContent = lines.subList(0,lines.size()-1);
            reader.close();

            FileWriter writer = new FileWriter(fileName,false);
            for(String s : existingContent){
                writer.append(s);
                writer.flush();
            }
            writer.close();

        } catch (Exception e) {
            System.out.println("BufferedReaderError");
        }

    }

    static boolean isFileEmpty(){
        String fileName = getFileName();
        File file = new File(fileName);
        return file.length()==0;
    }


    static void writeTask(String description, int id){

        Task toAdd = new Task(id, description);
        String fileName = getFileName();

        try {
            FileWriter writer = new FileWriter(fileName,true);
            writer.append(toAdd.toString());
            writer.flush();
            writer.close();

        }
        catch (Exception e){
            System.out.println("test exception");
        }
    }

    static void addTask(String description) {
        int id = getNextID();
        //TODO: validate string

        if(isFileEmpty()){
            addArrayIndent();
        }

        else{
            addComma();
        }

        writeTask(description,id);
        addClosingIndent();
    }
//TODO: This method uses the equals method of Task to look for the position within the list of the task to edit. Given so, I implemented the equals method as to compare only the id. Not sure if this is only a shortcut..
    //TODO: the call to write task creates a new task, thus it is wrong as it cancels the former creation time. I  need to create another writeTask method that directly takes a task
    static boolean updateTask(int id, String description){
        //check if task id is present
        if(!isIDPresent(id))
            return false;
        //check if description is valid
        List<Task> tasks = getTasks();
        if(tasks == null)
            return false;

        Task toCompare = new Task(id,description);

        int index = tasks.indexOf(toCompare);

        //Edit the task and readd it to the list
        Task toEdit = tasks.remove(index);
        toEdit.setDescription(description);
        toEdit.setUpdatedAt(LocalDateTime.now());
        tasks.add(index,toEdit);


        //Empty the file and rewrite all the tasks to it
        addArrayIndent();
        for(int i = 0; i<tasks.size()-1; i++){
            writeTask(tasks.get(i).getDescription(),tasks.get(i).getId());
            addComma();
        }
        writeTask(tasks.getLast().getDescription(),tasks.getLast().getId());
        addClosingIndent();


        return true;

    }

    static boolean deleteTask(int id){
        if(isIDPresent(id)){
            //delete task from JSON
        }

        return false;
    }


    static Task findTaskByID(int id){
        return null;
    }
    static boolean updateTaskStatus(int id, TaskStatus status){
        //check if id exists

        //update task

        return true;
    }

    static void listAllTasks(){
       int numOfTasks = getNumOfTasks();
       if(numOfTasks == 0)
           System.out.println("There are no tasks!");
       else{
           List<Task> tasks = reconstructTasks(numOfTasks);
           for(Task t : tasks){
               System.out.println(t);
           }
       }
    }

    //returns all the tasks starting with that index
    static List<Task> getTasks(){
        int numOfTasks = getNumOfTasks();
        if(numOfTasks == 0)
            return null;
        else{
            return reconstructTasks(numOfTasks);
        }
    }

    static void listTaskPerStatus(TaskStatus status){
        int numOfTasks = getNumOfTasks();
        if(numOfTasks == 0)
            System.out.println("There are no tasks!");
        else{
            List<Task> tasks = reconstructTasks(numOfTasks);
            List<Task> tasksToReturn = tasks.stream().filter(t -> t.getStatus() == status).toList();
            if(tasksToReturn.isEmpty()){
                System.out.println("There are no tasks with such status");
                return;
            }
            else{
                for(Task t : tasksToReturn){
                    System.out.println(t);
                }
            }
        }
    }

}
