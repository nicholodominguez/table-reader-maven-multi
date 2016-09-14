package com.ecc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import com.ecc.Table;

class Model{
    
    private Table table;
    private Path path;
    
    public Model(){}

    public Model(Table table, Path path){
        this.table = table;
        this.path = path;
    }
    
    public Table getTable(){
        return this.table;
    } 
    
    public Path getPath(){
        return this.path;
    }
    
    public void setTable(Table table){
        this.table = table;
    }
    
    public void setPath(Path path){
        this.path = path;
    }
    
    public long getTableLastUpdated(){
        return this.table.getLastUpdated();
    }
    
    public void editTableCell(int row, int column, String replacement){
        this.table.get(row).replace(column, replacement); 
    }

    public String getTableCell(int row, int column){
        return this.table.get(row).get(column); 
    }
}
