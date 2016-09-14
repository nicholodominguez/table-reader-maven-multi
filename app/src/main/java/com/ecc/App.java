package com.ecc;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import com.ecc.Model;
import com.ecc.KeyValue;
import com.ecc.TableGenerator;
import com.ecc.InputValidator;

public class App{
    public static void main(String[] args)throws IOException{
        String filename;
        TableGenerator tg = new TableGenerator();
        Model model = new Model();
        KeyValue replacement = new KeyValue();
        
        filename = tg.ifFileExists(args);
        model = tg.setFile(model, filename);
	    int choice = 3; 
	    int stringLen = replacement.getMaxKeyLen();

	    do{                
            switch(choice){
                case 1:
                    String search = InputValidator.getInputStr("Search keyword: ", stringLen*2);
                    model = tg.searchTable(model, search);
                    
                    break;
                case 2:
                    System.out.println("Enter cell dimension to be edited");
                    
                    int row = InputValidator.getInputInt("Enter table row: ", false);        
                    int column = InputValidator.getInputInt("Enter table column: ", false);
                    
                    System.out.println();
                    System.out.println("------------");
                    System.out.println("[1] Edit key");
                    System.out.println("[2] Edit value");
                    System.out.println("[3] Edit both (Enter value as <key><value>. No spaces.)");
                    System.out.println("------------");
                    
                    int size, input;
                    String inputString, data = model.getTableCell(row,column);
                    
                    do{
                        input = InputValidator.getInputInt("Option: ", true);
                        if(input < 1 || input > 3) System.out.println("Input too high or too low.");
                    }while(input < 1 || input > 3); 
                    
                    size = (input>2) ? stringLen*2 : stringLen;
                      
                    inputString = InputValidator.getInputStr("Replace cell data with: ", stringLen*2, size);
                    
                    switch(input){
                        case 1:
                            replacement.setKey(inputString);
                            replacement.setValue(data.substring(stringLen, size*2));
                            break;
                        case 2:
                            replacement.setKey(data.substring(0, stringLen));
                            replacement.setValue(inputString);
                            break;
                        case 3:
                            replacement.setKeyValue(inputString.substring(0, stringLen), inputString.substring(stringLen, size));
                            break;
                    }
                    
                    model = tg.editTable(model, row, column, replacement);
                    break;
                case 3:
                    break;
                case 4:        
                    System.out.println("Define table dimensions");
                    int editRow = InputValidator.getInputInt("Enter table row: ", true);        
                    int editColumn = InputValidator.getInputInt("Enter table column: ", true);
                    
                    model = tg.resetTable(model, editRow, editColumn);
                    break;
                case 5:
                    int sortRow;
                    int asc;
                    
                    sortRow = InputValidator.getInputInt("Enter table row: ", false);
                    
                    System.out.println();
                    System.out.println("------------");
                    System.out.println("[1] Ascending");
                    System.out.println("[2] Descending");
                    System.out.println("------------");
                    System.out.println();
                    
                    do{
                        asc = InputValidator.getInputInt("Option: ", true);
                        if(asc < 1 || asc > 2) System.out.println("Input too high or too low.");
                    }while(asc < 1 || asc > 2); 
                    
                    model = tg.sortRow(model, sortRow, asc);
                    break;
                case 6:
                    model = tg.addNewRow(model);
                    break;
                case 7:
                    break;
                default:
                    System.out.println("Invalid choice");
            }
            
            tg.saveTable(model);
            model = tg.printMenu(model);
            
            do{
                choice = InputValidator.getInputInt("Option: ", true);
                if (choice < 1 || choice > 7) {
                    System.out.println("Input too high or too low.");
                }
            }while (choice < 1 || choice > 7);	
        }while (choice != 7);
    } 
}
