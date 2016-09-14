package com.ecc;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Collections;
import java.util.Scanner;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.lang.NumberFormatException;

import com.ecc.MergeSorter;
import com.ecc.Model;
import com.ecc.KeyValue;
import com.ecc.Table;
import org.apache.commons.lang3.StringUtils;

class TableGenerator{
    private Scanner sc = new Scanner(System.in);

    public Model setFile(Model model, String filename) throws IOException {
        Path path;
        
        path = Paths.get(filename);
        model.setPath(path);
        
        if (!Files.isReadable(path) || !Files.isWritable(path)) {
            System.out.println("Unable to read or write file");
            System.exit(0);
        }
        else {    
            return loadTable(model);
        }
        
        return null;
    }
    
    public String ifFileExists(String[] args) {    
        if (args.length < 1) {
            System.out.println("Please specify a file. Run program as: java Program [filename/filepath]");
            System.exit(0);           
        }
        else if (args.length > 1) {
            System.out.println("Multiple files detected.");
            System.exit(0);  
        }
        else if (!Files.exists(Paths.get(args[0]))) {
            System.out.println("File not found.");
            System.exit(0);  
        }
        
        return args[0];
    }
    
    public void saveTable(Model model) throws IOException {
        Path path = model.getPath();
        Table table = model.getTable();
                
        Files.write(path, Integer.toString(table.getRowCount()).concat("\n").getBytes());
        Files.write(path, Integer.toString(table.getMaxColumn()).concat("\n").getBytes(), StandardOpenOption.APPEND);
        table.forEach((row,data) ->  {
            data.forEach((column,keyvalue) -> {
                String input = String.join("", String.join("",row.toString(),",",column.toString()), ":", keyvalue, "\n");
                try { 
                    Files.write(path, input.getBytes(), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    System.out.println("File not found");
                }
            });
        });                
    }
    
    public Model loadTable(Model model) throws IOException {
        List<String> load, temp;
        Path path = model.getPath();
        Table table;
        int rowCount, columnCount;
        int row, column, lineNumber = 3;
        Pattern linePattern = Pattern.compile("\\d+,\\d+:.{6}");                        //regex for the whole line
        Matcher matcher;
        
        load = Files.readAllLines(path);
        
        rowCount = Integer.parseInt(load.get(0));
        columnCount = Integer.parseInt(load.get(1));
        
        table = new Table(columnCount);
        table.setLastUpdated(Files.getLastModifiedTime(path).toMillis());
        
        for (int i = 2, rowCounter = 0; i < load.size(); i += columnCount, rowCounter++) {                  //loop that divides that list into rows
            temp = load.subList(i, i+columnCount);
            String index, data;
            String[]  indices;
            ArrayList<String> keyValueList = new ArrayList<>();
            
            for (int columnCounter = 0; columnCounter < columnCount; columnCounter++) {
                matcher = linePattern.matcher(temp.get(columnCounter));                                   
                boolean hasError = false;
                
                if (matcher.matches()) {
                    Pattern indexPattern = Pattern.compile("\\d+,\\d+:");               //regex for the index
                    Matcher m = indexPattern.matcher(temp.get(columnCounter));
                    
                    while (m.find()) {
                        index = m.group();
                        data = m.replaceFirst("");
                        indices = index.split("[,:]");
                        
                        row = Integer.parseInt(indices[0]);
                        column = Integer.parseInt(indices[1]);
                        if (row != rowCounter || column != columnCounter) {                                     //if index row doesnt match with group
                            hasError = true;
                            break;
                        }
                        keyValueList.add(data);
                    }
                }
                
                else {
                    hasError = true;    
                }
                
                if (hasError) {
                    lineNumber += (rowCounter * columnCount) + columnCounter;
                    System.out.println("Invalid line in file on line "+lineNumber);
                    System.exit(0);
                }
            }
            
            table = addRow(table, rowCounter, columnCount, keyValueList);
        }
        
        model.setTable(table);
        return model;           
    }
    
    
    public Model resetTable(Model model, int row, int column) throws IOException{
        HashMap<Integer, String> m;
        Path path = model.getPath();

        Table table = new Table(column);
        
        for(int i = 0; i < row; i++){
            this.addRow(table, i, column);
        }

        this.saveTable(model);
        table.setLastUpdated(Files.getLastModifiedTime(path).toMillis());
        model.setTable(table);
        return model;
    }
    
    public Table addRow(Table table, int rowCount, int column){
        HashMap<Integer, String> row = new HashMap<Integer, String>();
        String keyValue;
        int randomLetterAsciiInt;
        Random r = new Random();
                
        for(int columnCounter = 0; columnCounter < column; columnCounter++){  
            keyValue = new String();
            for(int keyValueIndex = 0; keyValueIndex < table.getStringLen()*2; keyValueIndex++){
                randomLetterAsciiInt = r.nextInt(table.getAsciiMax() - table.getAsciiMin()) + table.getAsciiMin();
                keyValue = String.join("", keyValue, String.valueOf((char) randomLetterAsciiInt));
            }
            row.put(columnCounter, keyValue.toString());                            
        }
        
        table.put(rowCount, row);
        table.incrementRowCount();
        return table;   
    }
    
    public Table addRow(Table table, int rowCount, int column, ArrayList<String> data){
        HashMap<Integer, String> row = new HashMap<Integer, String>();
                
        for(int columnCounter = 0; columnCounter < column; columnCounter++){ 
            //System.out.println(data.get(j)+" "+data.get(j).length());
            row.put(columnCounter, data.get(columnCounter));                            
        }
        
        table.put(rowCount, row);
        table.incrementRowCount();
        return table;
    }
    
    public Model addNewRow(Model model) throws IOException{
        if(this.hasUpdate(model)){
            model = this.loadTable(model);
            System.out.println();
            System.out.println("Changes in the file detected. Table updated");
            System.out.println();
        }
        
        Table table = model.getTable();
        
        table = this.addRow(table, table.getRowCount(), table.getMaxColumn());
        System.out.println("New row added.");
        model.setTable(table);
        this.saveTable(model);
        
        return model;
    }
    
    public Model printTable(Model model) throws IOException{
        if(this.hasUpdate(model)){
            model = this.loadTable(model);
            System.out.println();
            System.out.println("Changes in the file detected. Table updated");
            System.out.println();
        }
        
        Table table = model.getTable();
        int maxColumn = table.getMaxColumn();
        int stringLen = table.getStringLen();
        
        System.out.println();
        
        table.forEach( (row, data) -> {
            System.out.print("|  ");
            data.forEach( (column, keyValue) -> {
               System.out.print(keyValue.toString().substring(0, stringLen) + " : " + keyValue.toString().substring(stringLen) + "  |  ");
            });
            System.out.println();
        });        
        System.out.println();
        
        return model;
    }
    
    public Model printMenu(Model model) throws IOException{
        model = this.printTable(model);
        System.out.println("------------");
        System.out.println("[1] Search");
        System.out.println("[2] Edit");
        System.out.println("[3] Print");
        System.out.println("[4] Reset");
        System.out.println("[5] Sort");
        System.out.println("[6] Add row");
        System.out.println("[7] Exit");
        System.out.println("------------");
        
        return model;
    }

    public Model searchTable(Model model, String input) throws IOException{
        if(this.hasUpdate(model)){
            model = this.loadTable(model);
            System.out.println();
            System.out.println("Changes in the file detected. Table updated");
            System.out.println();
        }
        
        Table table = model.getTable();
        String content;
        int searchLen = input.length();

        table.forEach( (row, data) -> {
            data.forEach( (column, keyValue) -> {
                int counter = 0;
                
                counter = StringUtils.countMatches(StringUtils.lowerCase(keyValue), input) + StringUtils.countMatches(StringUtils.upperCase(keyValue), input);
                
                if (counter > 0) {
                    System.out.println("(" + Integer.toString(row) + "," + Integer.toString(column) + ")" + " - " + Integer.toString(counter));
                }
            });
        }); 
        
        return model;
              
    }

    public Model editTable(Model model, int row, int column, KeyValue input) throws IOException{
        if(this.hasUpdate(model)){
            model = this.loadTable(model);
            System.out.println();
            System.out.println("Changes in the file detected. Table updated");
            System.out.println();
            model = this.printTable(model);
        }
        Table table = model.getTable();
        table.get(row).replace(column, input.getKeyValue());
        model.setTable(table);   
        this.saveTable(model);
        return model;         
    }
    
    public Model sortRow(Model model, int row, int type) throws IOException{
        if(this.hasUpdate(model)){
            model = this.loadTable(model);
            System.out.println();
            System.out.println("Changes in the file detected. Table updated");
            System.out.println();
        }        
        
        Table table = this.sort(model.getTable(), row, type);
        model.setTable(table);
        this.saveTable(model);  
        return model;       
    }
    
    public Table sort(Table table, int row, int asc) throws IOException{
        ArrayList<String> rowValues = new ArrayList<String>();        
        HashMap<Integer, String> map = new HashMap<>();
        
        for(int i = 0; i < table.getMaxColumn(); i++){
            rowValues.add(table.get(row).get(i));
        }
        
        MergeSorter ms = new MergeSorter(rowValues, asc==1?true:false);
        rowValues = ms.sort();
        
        for(int i = 0; i < table.getMaxColumn(); i++){
            map.put(i, rowValues.get(i));
        }
        
        table.replace(row, map);
        return table;
    }    
    
    public boolean hasUpdate(Model model) throws IOException{
        long fileUpdateTime = Files.getLastModifiedTime(model.getPath()).toMillis();
        long tableUpdateTime = model.getTableLastUpdated();
        
        return fileUpdateTime!=tableUpdateTime?true:false;
    }
}


