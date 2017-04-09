import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

public class ParserEngine {
	ArrayList<CompilationUnit> currentArray;
	HashMap<String, Boolean> map;
	String inputPath;
	
	String outputPath;
	ParserEngine(String inputPath, String outputPath){
		this.inputPath=inputPath;
		this.outputPath=inputPath + "\\" + outputPath + ".png";
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public void start() throws Exception {
		currentArray = readFileFromFolder(inputPath);
    System.out.println("Code Started");
    }
	
	  private String parser() {
		  String result = "";
	        String className = "";
	        
		  return " ";
	  }
	  
	  private String aToSymScope(String stringScope) {
	        switch (stringScope) {
	        case "private":
	            return "-";
	        case "public":
	            return "+";
	        default:
	            return "";
	        }
	    }
	
	  
	  public ArrayList<CompilationUnit> readFileFromFolder(String location){
		  ArrayList<CompilationUnit> allUnits= new ArrayList<CompilationUnit>();
			location="/Users/Harshit/LECTURES/202/umlparser/TestClass";
			File files= new File(location);
			for(File file:files.listFiles()){
				if(file.getName().matches("^.*\\.java$")){
					FileInputStream fis=null;
					try {
						fis = new FileInputStream(file);
					if(fis!=null){
						
						allUnits.add((CompilationUnit)JavaParser.parse(file));
					}
						} catch (ParseException e) {	
						}catch (IOException e) {
						}
					}
			}
			return allUnits;
		}
	  
	    private void buildMap(ArrayList<CompilationUnit> cuArray) {
	    	for (CompilationUnit cu : cuArray) {
	    		 List cl = cu.getTypes();
		            
	        }
	    }
	
}
