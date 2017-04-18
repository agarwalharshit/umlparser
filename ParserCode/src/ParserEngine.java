import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;




public class ParserEngine {
	ArrayList<CompilationUnit> cUnit;
	String inputPath="";
	String outputFile="";
	HashMap<String,Boolean> classAndInterfaceMap;
	ArrayList<String> makeFieldPublic = new ArrayList<String>();
	HashMap<String, String> mapClassConn;
	String yumlInput="";

	ParserEngine(String inputPath, String outputFileName){
		this.inputPath=inputPath;
		this.outputFile=inputPath + "/" + outputFileName + ".png";
		cUnit=new ArrayList<CompilationUnit>();
		classAndInterfaceMap= new HashMap<String,Boolean>();
		  mapClassConn= new HashMap<String, String>();
		
	}

	public void start() throws Exception {
		readAndParseJavaFile();
		createClassAndInterfaceMap();
		for(CompilationUnit cu: cUnit){
			yumlInput=yumlInput+parserUNIT(cu);
			}
		yumlInput=yumlInput+addDependencies();
		yumlInput=UniqueCode(yumlInput);
		generateClassDiagram(yumlInput);
		System.out.println(yumlInput);
	}
	
	
	
	
	   private String UniqueCode(String output) {
	        String[] code = output.split(",");
	        String[] uniqueCodeLines = new LinkedHashSet<String>(Arrays.asList(code)).toArray(new String[0]);
	        String result = String.join(",", uniqueCodeLines);
	        return result;
	    }
	
    private String addDependencies() {
        String outputStr = "";
        Set<String> keySet;
        keySet = mapClassConn.keySet();
        
        for (String key : keySet) {
        	
            String[] elements = key.split("-");
            if (classAndInterfaceMap.get(elements[0])) outputStr += "[<<interface>>;" + elements[0] + "]";
            else outputStr += "[" + elements[0] + "]";
            outputStr += mapClassConn.get(key);
            if (classAndInterfaceMap.get(elements[1])) outputStr += "[<<interface>>;" + elements[1] + "]";
            else outputStr += "[" + elements[1] + "]";
            outputStr += ",";
        }
        return outputStr;
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
	  
	    private String resolveScope(String scope) {
	    	if(scope.trim().equalsIgnoreCase("private")) return "-";
	    	if(scope.trim().equalsIgnoreCase("public")) return "+";
	    	if(scope.trim().equalsIgnoreCase("protected")) return "#";
	    	return "";
	    }
	  
	    private String parserUNIT(CompilationUnit cUnit) {
	    	String className = "[";
	        String result = "";
	        String classShortName = "";
	        String methods = "";
	        String fields = "";
	        String dependencies = ",";
	        
	 
	      //  Node node= cUnit.getTypes().get(0);
	        List<TypeDeclaration> ltd = cUnit.getTypes();
	        Node node = ltd.get(0); // assuming no nested classes
	        
	        ClassOrInterfaceDeclaration classOrInterface= (ClassOrInterfaceDeclaration) node;
	        if(classOrInterface.isInterface())  className+="<<interface>>;";
	        className+=classOrInterface.getName();
	        classShortName = classOrInterface.getName();
	        List<BodyDeclaration> listBodyDeclaration= ((TypeDeclaration) node).getMembers();
	        
	        if(!classOrInterface.isInterface()){
	        	//for(BodyDeclaration bd:listBodyDeclaration){
	        	for (BodyDeclaration bd : ((TypeDeclaration) node).getMembers()) {
	        		if(bd instanceof  ConstructorDeclaration){
	        			ConstructorDeclaration cd = ((ConstructorDeclaration) bd);
//	        				System.out.println(cd.getModifiers());
//	        				System.out.println(ModifierSet.isPublic(cd.getModifiers()));
//	        				System.out.println(ModifierSet.isPrivate(cd.getModifiers()));
//	        			
//	        			if(cd.getDeclarationAsString().matches("^public(.*)")){
	        				if(ModifierSet.isPublic(cd.getModifiers())){
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
	        	}
	        
	        
	        	for(BodyDeclaration bd:listBodyDeclaration){
	        		if(bd instanceof  MethodDeclaration){
	        			MethodDeclaration md = ((MethodDeclaration) bd);
	        		//	if(cd.getDeclarationAsString().matches("^public(.*)")){
	        			if(ModifierSet.isPublic(md.getModifiers())){
	        			if (md.getName().startsWith("get") || md.getName().startsWith("set")) {
	                        String varName = md.getName().substring(3);
	                        makeFieldPublic.add(varName.toLowerCase());
	                    } else {
	                        if (!methods.equals(""))
	                            methods += ";";
	                        methods += "+ " + md.getName() + "(";
	                        List<Parameter> paramList=md.getParameters();
	                        
	                        for (Object childNodeObject : md.getChildrenNodes()) {
	                            if (childNodeObject instanceof Parameter) {
	                                Parameter param = (Parameter) childNodeObject;
	                                
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
	
	                            } else {
	                            	
	                            	
	                            	
	                                String bodyArr[] = childNodeObject.toString().split(" ");
	                                
	                                for (String body : bodyArr) {
	                                	 if(classAndInterfaceMap.containsKey(body) && !classOrInterface.isInterface()){
	 		                            	dependencies += "[" + classShortName;
	 		                            	dependencies += "] uses -.->";
	 		                            	if (classAndInterfaceMap.get(body)){
	 		                            		dependencies += "[<<interface>>;" + body+ "]";
	 		                            	}
	 		                                else dependencies += "[" + body + "]";
	 		                            	dependencies += ",";
	 		                            }
	 		                            	
	                                }
	                            }
	                        }
	                        methods += ") : " + md.getType();
	                           // nextParam = true;
	        		}
	        			}}
	        }
	        	 boolean nextField = false;
	        	for(BodyDeclaration bd:listBodyDeclaration){
	        		if(bd instanceof   FieldDeclaration){
	        			FieldDeclaration fd = ((FieldDeclaration) bd);
	        			
	        			String scopeString=bd.toStringWithoutComments();
	        			scopeString=scopeString.substring(0,scopeString.indexOf(" "));
	        			
	                    scopeString = resolveScope(scopeString);
	                    
	                    
	                    String fieldClass = fd.getType().toString();
	                    fieldClass = fieldClass.replace("[", "(");
	                    fieldClass = fieldClass.replace("]", ")");
	                    fieldClass = fieldClass.replace("<", "(");
	                    fieldClass = fieldClass.replace(">", ")");
	                    
	                    String fieldName = fd.getChildrenNodes().get(1).toString();
	                    
	                    if (fieldName.contains("="))
	                        fieldName = fd.getChildrenNodes().get(1).toString().substring(0, fd.getChildrenNodes().get(1).toString().indexOf("=") - 1);
	                    
	                    if (scopeString.equals("-") && makeFieldPublic.contains(fieldName.toLowerCase())) {
	                    	scopeString = "+";
	                    }
	                    
	                    String getDepen = "";
	                    boolean getDepenMultiple = false;
	                    if (fieldClass.contains("(")) {
	                        getDepen = fieldClass.substring(fieldClass.indexOf("(") + 1,fieldClass.indexOf(")"));
	                        getDepenMultiple = true;
	                    } else if (classAndInterfaceMap.containsKey(fieldClass)) {
	                        getDepen = fieldClass;
	                    }
	                    if (getDepen.length() > 0 && classAndInterfaceMap.containsKey(getDepen)) {
	                        String connection = "-";

	                        if (mapClassConn.containsKey(getDepen + "-" + classShortName)) {
	                            connection = mapClassConn.get(getDepen + "-" + classShortName);
	                            if (getDepenMultiple)
	                                connection = "*" + connection;
	                            mapClassConn.put(getDepen + "-" + classShortName,connection);
	                        } else {
	                            if (getDepenMultiple)
	                                connection += "*";
	                            mapClassConn.put(classShortName + "-" + getDepen,connection);
	                        }
	                    }
	                    
	                    if (scopeString == "+" || scopeString == "-") {
	                        if (nextField)
	                            fields += "; ";
	                        fields += scopeString + " " + fieldName + " : " + fieldClass;
	                        nextField = true;
	                    }

	        		}
	        	}
	        	
	        	
	        if (classOrInterface.getExtends() != null) {
	        	dependencies += "[" + classShortName + "] " + "-^ " + classOrInterface.getExtends();
	        	dependencies += ",";
	        }
	        if (classOrInterface.getImplements() != null) {
	            List<ClassOrInterfaceType> interfaceList = (List<ClassOrInterfaceType>) classOrInterface.getImplements();
	            for (ClassOrInterfaceType intface : interfaceList) {
	            	dependencies += "[" + classShortName + "] " + "-.-^ " + "["
	                        + "<<interface>>;" + intface + "]";
	            	dependencies += ",";
	            }
	        }

	        result += className;
	        if (!fields.isEmpty()) {
	        	
	        	
	            result += "|" + replaceBrack(fields);
	        }
	        if (!methods.isEmpty()) {
	            result += "|" + replaceBrack(methods);
	        }
	        result += "]";
	        result += dependencies;
	        return result;
	   
	    }
	    
	    private String replaceBrack(String inputString) {
	    	inputString = inputString.replace("[", "(");
	    	inputString = inputString.replace(">", ")");
	    	inputString = inputString.replace("<", "(");
	    	inputString = inputString.replace("]", ")");
	        return inputString;
	    }  
	    
	    public Boolean generateClassDiagram(String outputDiagramString) {

	        try {
	            String yumlLink = "https://yuml.me/diagram/plain/class/" + outputDiagramString+ ".png";
	            URL url = new URL(yumlLink);
	            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
	           // int responseCode = conn.getResponseCode();
	            if (httpsURLConnection.getResponseCode() == 200) {
	              File file= new File(outputFile);
	            OutputStream outputStream = new FileOutputStream(file);
	            
	            byte[] bytes = new byte[1024];
	            int read = 0;
	            while ((read = httpsURLConnection.getInputStream().read(bytes)) != -1) {
	                outputStream.write(bytes, 0, read);
	            }
	            outputStream.close();
	            httpsURLConnection.disconnect();
	            }else{  
	            	 System.out.println("Failed : HTTP error code : " + httpsURLConnection.getResponseCode());
	            }
            
	        } catch (Exception e) {
	           System.out.println(e.getMessage());
	        }
	        return null;
	    }   
    
}
