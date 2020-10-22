package model;

public class CodeBookEntry {

    public String code;
    public String value;
    public String codeClass;
    
    public CodeBookEntry (String code, String value, String codeClass) {
    	this.code = code;
    	this.value = value;
    	this.codeClass = codeClass;
    }
    
    public String getCode() {
    	return code;
    }
    
    public void setCode(String code) {
    	this.code = code;
    }
    
    public String getValue() {
    	return value;
    }
    
    public void setValue(String value) {
    	this.value = value;
    }
    
    public String getCodeClass() {
    	return codeClass;
    }
    
    public void setCodeClass(String codeClass) {
    	this.codeClass = codeClass;
    }
    
}
