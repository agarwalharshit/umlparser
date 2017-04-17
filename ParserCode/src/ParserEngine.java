import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.body.ConstructorDeclaration;




public class ParserEngine {
	ArrayList<CompilationUnit> cUnit;
	String inputPath="";
	String outputFile="";
	HashMap<String,Boolean> classAndInterfaceMap;

	ParserEngine(String inputPath, String outputFileName){
		this.inputPath=inputPath;
		this.outputFile=inputPath + "/" + outputFileName + ".png";
		cUnit=new ArrayList<CompilationUnit>();
		classAndInterfaceMap= new HashMap<String,Boolean>();
	}

	public void start() throws Exception {
		readAndParseJavaFile();
		for(CompilationUnit cu: cUnit)
		parserUNIT(cu);
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
	  
	  
	  void createClassAndInterfaceMap(){
		  for(CompilationUnit cu: cUnit){
			  for(TypeDeclaration td: cu.getTypes()){
				  ClassOrInterfaceDeclaration classOrInterface= (ClassOrInterfaceDeclaration) td; 
				  classAndInterfaceMap.put(classOrInterface.getName(),classOrInterface.isInterface());
			  }   
		  }
	  }
	  
	  void readAndParseJavaFile(){
		  File folder= new File(inputPath);
		  File[] files=folder.listFiles();
		  for(File file:files){
			  if(file.getName().matches("(.)*\\.java$")){
				  try {
					cUnit.add((CompilationUnit)JavaParser.parse(file));
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			 
			  }
		  }
	  }
	    private void parserUNIT(CompilationUnit cUnit) {
	    	String className = "[";
	        String result = "";
	        String classShortName = "";
	        String methods = "";
	        String fields = "";
	        String dependencies = ",";
	        
	        //MyWORK
	        Node node= cUnit.getTypes().get(0);
	        ClassOrInterfaceDeclaration classOrInterface= (ClassOrInterfaceDeclaration) node;
	        if(classOrInterface.isInterface())  className+="<<interface>>;";
	        className+=classOrInterface.getName();
	        classShortName = classOrInterface.getName();
	        List<BodyDeclaration> listBodyDeclaration= ((TypeDeclaration) node).getMembers();
	        
	        if(!classOrInterface.isInterface()){
	        	for(BodyDeclaration bd:listBodyDeclaration){
	        		if(bd instanceof  ConstructorDeclaration){
	        			ConstructorDeclaration cd = ((ConstructorDeclaration) bd);
	   
	        			
//	        			if(cd.getDeclarationAsString().matches("^public(.*)")){
		        			if(!methods.equals(""))	methods += ";";
		        			methods += "+ " + cd.getName() + "(";
		        			
		        			List<Parameter> paramList=cd.getParameters();
		        			for(Parameter param: paramList){
		        				 String paramClass = param.getType().toString();
		                            String paramName = param.getChildrenNodes().get(0).toString();
		                            methods += paramName + " : " + paramClass;
		                            if(classAndInterfaceMap.containsKey(paramClass) && !classOrInterface.isInterface()){
		                            	dependencies += "[" + classShortName;
		                            	dependencies += "] uses -.->";
		                            	if (classAndInterfaceMap.get(paramClass)){
		                            		dependencies += "[<<interface>>;" + paramClass+ "]";
		                            	}
		                                else dependencies += "[" + paramClass + "]";
		                            }
		                            	dependencies += ",";
		        			}
		        			methods += ")";
	        			
	        			}
	        		}      	
	        	}
	        
	        
	        	for(BodyDeclaration bd:listBodyDeclaration){
	        		if(bd instanceof  MethodDeclaration){
	        			MethodDeclaration md = ((MethodDeclaration) bd);
	        		//	if(cd.getDeclarationAsString().matches("^public(.*)")){
	        			
	        			if (md.getName().startsWith("get") || md.getName().startsWith("set")) {
	                        String varName = md.getName().substring(3);
	                        //makeFieldPublic.add(varName.toLowerCase());
	                    } else {
	                        if (!methods.equals(""))
	                            methods += ";";
	                        methods += "+ " + md.getName() + "(";
	                        List<Parameter> paramList=md.getParameters();
	                        
	                        for (Object gcn : md.getChildrenNodes()) {
	                            if (gcn instanceof Parameter) {
	                                Parameter param = (Parameter) gcn;
	                                String paramClass = param.getType().toString();
		                            String paramName = param.getChildrenNodes().get(0).toString();
		                            methods += paramName + " : " + paramClass;
		                            if(classAndInterfaceMap.containsKey(paramClass) && !classOrInterface.isInterface()){
		                            	dependencies += "[" + classShortName;
		                            	dependencies += "] uses -.->";
		                            	if (classAndInterfaceMap.get(paramClass)){
		                            		dependencies += "[<<interface>>;" + paramClass+ "]";
		                            	}
		                                else dependencies += "[" + paramClass + "]";
		                            }
		                            	dependencies += ",";
		                            	
		                            	
//	                            } else {
//	                                String methodBody[] = gcn.toString().split(" ");
//	                                for (String foo : methodBody) {
//	                                    if (map.containsKey(foo)
//	                                            && !map.get(classShortName)) {
//	                                        additions += "[" + classShortName
//	                                                + "] uses -.->";
//	                                        if (map.get(foo))
//	                                            additions += "[<<interface>>;" + foo
//	                                                    + "]";
//	                                        else
//	                                            additions += "[" + foo + "]";
//	                                        additions += ",";
//	                                    }
//	                                }
//	                            }
	                        }
	                    }
	        			
	        			
	        			
	        		}
//	        	if(bd instanceof   FieldDeclaration){
//	        		FieldDeclaration fd = ((FieldDeclaration) bd);
//	        	}
	        	}
	        }

//	        ArrayList<String> makeFieldPublic = new ArrayList<String>();
//


//	        for (BodyDeclaration bd : ((TypeDeclaration) node).getMembers()) {
//	            if (bd instanceof MethodDeclaration) {
//	                MethodDeclaration md = ((MethodDeclaration) bd);
//	                // Get only public methods
//	                if (md.getDeclarationAsString().startsWith("public")
//	                        && !coi.isInterface()) {
//	                    // Identify Setters and Getters
//	                    if (md.getName().startsWith("set")
//	                            || md.getName().startsWith("get")) {
//	                        String varName = md.getName().substring(3);
//	                        makeFieldPublic.add(varName.toLowerCase());
//	                    } else {
//	                        if (nextParam)
//	                            methods += ";";
//	                        methods += "+ " + md.getName() + "(";
//	                        for (Object gcn : md.getChildrenNodes()) {
//	                            if (gcn instanceof Parameter) {
//	                                Parameter paramCast = (Parameter) gcn;
//	                                String paramClass = paramCast.getType()
//	                                        .toString();
//	                                String paramName = paramCast.getChildrenNodes()
//	                                        .get(0).toString();
//	                                methods += paramName + " : " + paramClass;
//	                                if (map.containsKey(paramClass)
//	                                        && !map.get(classShortName)) {
//	                                    additions += "[" + classShortName
//	                                            + "] uses -.->";
//	                                    if (map.get(paramClass))
//	                                        additions += "[<<interface>>;"
//	                                                + paramClass + "]";
//	                                    else
//	                                        additions += "[" + paramClass + "]";
//	                                }
//	                                additions += ",";
//	                            } else {
//	                                String methodBody[] = gcn.toString().split(" ");
//	                                for (String foo : methodBody) {
//	                                    if (map.containsKey(foo)
//	                                            && !map.get(classShortName)) {
//	                                        additions += "[" + classShortName
//	                                                + "] uses -.->";
//	                                        if (map.get(foo))
//	                                            additions += "[<<interface>>;" + foo
//	                                                    + "]";
//	                                        else
//	                                            additions += "[" + foo + "]";
//	                                        additions += ",";
//	                                    }
//	                                }
//	                            }
//	                        }
//	                        methods += ") : " + md.getType();
//	                        nextParam = true;
//	                    }
//	                }
//	            }
//	        }
//	        
//	        
//	        
//	        
//	        
//	        
//	        boolean nextField = false;
//	        for (BodyDeclaration bd : ((TypeDeclaration) node).getMembers()) {
//	            if (bd instanceof FieldDeclaration) {
//	                FieldDeclaration fd = ((FieldDeclaration) bd);
//	                String fieldScope = aToSymScope(
//	                        bd.toStringWithoutComments().substring(0,
//	                                bd.toStringWithoutComments().indexOf(" ")));
//	                String fieldClass = changeBrackets(fd.getType().toString());
//	                String fieldName = fd.getChildrenNodes().get(1).toString();
//	                if (fieldName.contains("="))
//	                    fieldName = fd.getChildrenNodes().get(1).toString()
//	                            .substring(0, fd.getChildrenNodes().get(1)
//	                                    .toString().indexOf("=") - 1);
//
//	                if (fieldScope.equals("-")
//	                        && makeFieldPublic.contains(fieldName.toLowerCase())) {
//	                    fieldScope = "+";
//	                }
//	                String getDepen = "";
//	                boolean getDepenMultiple = false;
//	                if (fieldClass.contains("(")) {
//	                    getDepen = fieldClass.substring(fieldClass.indexOf("(") + 1,
//	                            fieldClass.indexOf(")"));
//	                    getDepenMultiple = true;
//	                } else if (map.containsKey(fieldClass)) {
//	                    getDepen = fieldClass;
//	                }
//	                if (getDepen.length() > 0 && map.containsKey(getDepen)) {
//	                    String connection = "-";
//
//	                    if (mapClassConn
//	                            .containsKey(getDepen + "-" + classShortName)) {
//	                        connection = mapClassConn
//	                                .get(getDepen + "-" + classShortName);
//	                        if (getDepenMultiple)
//	                            connection = "*" + connection;
//	                        mapClassConn.put(getDepen + "-" + classShortName,
//	                                connection);
//	                    } else {
//	                        if (getDepenMultiple)
//	                            connection += "*";
//	                        mapClassConn.put(classShortName + "-" + getDepen,
//	                                connection);
//	                    }
//	                }
//	                if (fieldScope == "+" || fieldScope == "-") {
//	                    if (nextField)
//	                        fields += "; ";
//	                    fields += fieldScope + " " + fieldName + " : " + fieldClass;
//	                    nextField = true;
//	                }
//	            }
//
//	        }
//
//	        if (coi.getExtends() != null) {
//	            additions += "[" + classShortName + "] " + "-^ " + coi.getExtends();
//	            additions += ",";
//	        }
//	        if (coi.getImplements() != null) {
//	            List<ClassOrInterfaceType> interfaceList = (List<ClassOrInterfaceType>) coi
//	                    .getImplements();
//	            for (ClassOrInterfaceType intface : interfaceList) {
//	                additions += "[" + classShortName + "] " + "-.-^ " + "["
//	                        + "<<interface>>;" + intface + "]";
//	                additions += ",";
//	            }
//	        }
//
//	        result += className;
//	        if (!fields.isEmpty()) {
//	            result += "|" + changeBrackets(fields);
//	        }
//	        if (!methods.isEmpty()) {
//	            result += "|" + changeBrackets(methods);
//	        }
//	        result += "]";
//	        result += additions;
//	        return result;
	   // }
	    }
}
