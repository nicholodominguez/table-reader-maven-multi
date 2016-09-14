package com.ecc;

class KeyValue{
    
    private final int MAX_KEY_LEN = 3;
    private final int MAX_VALUE_LEN = 3;
    private String key;
    private String value;
    
    public KeyValue(){
        setKeyValue("","");
    }

    public String getKey() {
        return this.key;
    }
    
    public String getValue() {
        return this.value;        
    }
    
    public String getKeyValue(){
        return this.key + this.value;
    }
    
    public int getMaxKeyLen(){
        return this.MAX_KEY_LEN;
    }
    
    public int getMaxValueLen(){    
        return this.MAX_VALUE_LEN;
    }
    
    public int getMaxKeyValueLen(){    
        return this.MAX_KEY_LEN + this.MAX_VALUE_LEN;
    }
    
    public void setKey(String key){
        this.key = key;
    }
    
    public void setValue(String value){
        this.value = value;
    }
    
    public void setKeyValue(String key, String value){
        setKey(key);
        setValue(value);
    }
    
    public void resetKeyValue(){
        setKeyValue("","");
    }
    
}
