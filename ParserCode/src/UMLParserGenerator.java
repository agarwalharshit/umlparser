import java.io.File;
import java.io.FileInputStream;
import java.util.Set;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.github.javaparser.ast.body.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import com.github.javaparser.ast.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import com.github.javaparser.*;





public class UMLParserGenerator {
	ArrayList<CompilationUnit> cUnit;
	String inputPath="";
	String outputFile="";
	HashMap<String,Boolean> classAndInterfaceMap;
	ArrayList<String> makeFieldPublic = new ArrayList<String>();
	HashMap<String, String> connMap;
	String yumlInput="";

	UMLParserGenerator(String inputPath, String outputFileName){
		this.inputPath=inputPath;
		connMap= new HashMap<String, String>();
		cUnit=new ArrayList<CompilationUnit>();
		this.outputFile=outputFileName + ".png";
		classAndInterfaceMap= new HashMap<String,Boolean>();
		 
		
	}

	//Entry point of the code
	void createClassDiagram() throws Exception {
		readAndParseJavaFile();
		createClassAndInterfaceMap();
		for(CompilationUnit compilationUnit: cUnit) yumlInput=yumlInput+parserUNIT(compilationUnit);
		yumlInput+=dependentGenerator();
		yumlInput=uniqueCode(yumlInput);
		generateClassDiagram(yumlInput);
		System.out.println(yumlInput);
	}
	
	
	public String uniqueCode(String output) {
	        String[] code = output.split(",");
	        HashSet<String> hs=new HashSet<String>();
	        for(String s:code) hs.add(s); 
	        String[] entryUnique=new String[hs.size()];
	        int i=0;
	        for(String s:hs){
	        	entryUnique[i]=s;
	        	i++;
	        } 
	        String joinedString = String.join(",", entryUnique);
	        return joinedString;
	    }
	
    public String dependentGenerator() {
    	 Set<String> keySet;
        String outputStr = "";
        keySet = connMap.keySet();
 
        for (String key : keySet) {       	
            String[] elements = key.split("-");
            if (classAndInterfaceMap.get(elements[0])) outputStr += "[<<interface>>;" + elements[0] + "]";
            else outputStr += "[" + elements[0] + "]";
            outputStr += connMap.get(key);
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
		  File[] files=new File(inputPath).listFiles();
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
	    	String cName = "[";
	    	String dependencies = ",";
	        String output = "";
	        String classMethods = "";
	        String shClsName = "";
	        String classFields = "";
	        boolean next = false;
	        
	        
	 
	        List<TypeDeclaration> typeDeclarationList = cUnit.getTypes();
	       // Node classNode = ltd.get(0); // assuming no nested classes
	        ClassOrInterfaceDeclaration classOrInterface= (ClassOrInterfaceDeclaration) typeDeclarationList.get(0);
	        if(classOrInterface.isInterface())  cName+="<<interface>>;";
	        cName+=classOrInterface.getName();
	        shClsName = classOrInterface.getName();
	        List<BodyDeclaration> listBodyDeclaration= ((TypeDeclaration) typeDeclarationList.get(0)).getMembers();
	        
	        if(classOrInterface.isInterface()){
	        	
	        }else{
//	        if(!classOrInterface.isInterface()){
	        	TypeDeclaration typeDeclaration=((TypeDeclaration) typeDeclarationList.get(0));
	        	
	        	for (BodyDeclaration bodyDeclaration : typeDeclaration.getMembers()) {
	        		if(bodyDeclaration instanceof  ConstructorDeclaration){
	        			ConstructorDeclaration constructorDeclaration = ((ConstructorDeclaration) bodyDeclaration);
	        				if(ModifierSet.isPublic(constructorDeclaration.getModifiers())){
		        			if(!classMethods.equalsIgnoreCase("")) classMethods += ";";
		        			//classMethods += "+ " + constructorDeclaration.getName() + "(";
		        			classMethods += "+ ";
		        			classMethods +=constructorDeclaration.getName();
		        			classMethods +="(";
		        			List<Parameter> parameterList=constructorDeclaration.getParameters();
		        			if(parameterList==null){
		        			}else{
		        			for(Parameter parameter: parameterList){
		        				 String parameterClass = parameter.getType().toString();
		                            String parameterName = parameter.getChildrenNodes().get(0).toString();
		                           // classMethods += parameterName + " : " + parameterClass;
		                            classMethods += parameterName;
		                            classMethods += " : "; 
		                            classMethods +=parameterClass;
		                            
		                            
		                            //if(classAndInterfaceMap.containsKey(parameterClass) && !classOrInterface.isInterface()){
		                            	if(classAndInterfaceMap.containsKey(parameterClass)){
		                            	dependencies =dependencies+ "[" ;
		                            	dependencies =dependencies+ shClsName;
		                            	dependencies =dependencies+ "] uses -.->";
		                            	if (classAndInterfaceMap.get(parameterClass)){
		                            		dependencies = dependencies+"[<<interface>>;";
		                            				dependencies=dependencies+ parameterClass;
		                            				dependencies=dependencies+"]";
		                            	}
		                                else{
		                                	dependencies += dependencies+"[";
		                                	dependencies=dependencies+ parameterClass;
		                                	dependencies=dependencies+"]";
		                                }
		                            }
		                            	dependencies =dependencies+ ",";}}
		        			classMethods =classMethods+ ")";	}}}}
	        
	     	 next = false;
	        	for(BodyDeclaration bodyDeclaration:listBodyDeclaration){
	        		if(bodyDeclaration instanceof   FieldDeclaration){
	        			FieldDeclaration fieldDeclaration = ((FieldDeclaration) bodyDeclaration);
	        			String scopeString=bodyDeclaration.toStringWithoutComments();
	        			scopeString=scopeString.substring(0,scopeString.indexOf(" "));
	        			
	                    scopeString = resolveScope(scopeString);
	                    
	                    
	                    String clF = fieldDeclaration.getType().toString();
	                    clF=replaceBrack(clF);
	                    
	                    String fNa = fieldDeclaration.getChildrenNodes().get(1).toString();
	                    
	                    if (fNa.contains("=")){
	                    	String childName=fieldDeclaration.getChildrenNodes().get(1).toString();
	                    	int toIndex=childName.indexOf("=") - 1;
	                    	fNa = childName.substring(0, toIndex);
	                    }
	                    if (scopeString.equalsIgnoreCase("-") && makeFieldPublic.contains(fNa.toLowerCase())){
	                    	scopeString = "+";
	                    }
	                    
	                    String getDependency; 
	                    getDependency= "";
	                    boolean hasMiltipleDependency;
	                    hasMiltipleDependency= false;
	                    if (clF.contains("(")) {
	                    	int ind1=clF.indexOf("(");
	                    	int ind2=clF.indexOf(")");
	                    	getDependency = clF.substring(ind1 + 1,ind2);
	                    	hasMiltipleDependency = true;
	                    } else if (classAndInterfaceMap.containsKey(clF)) getDependency = clF;
	                    
	                    if (getDependency!=null && getDependency.length() > 0 && classAndInterfaceMap.containsKey(getDependency)) {
	                        String con;
	                        con= "-";
	                        String key=getDependency ;
	                        		key+= "-" ;
	                        		key+= shClsName;
	                        		
	                        if (connMap.containsKey(key)) {
	                        	con = connMap.get(key);
	                            if (hasMiltipleDependency) con = "*" + con;
	                            connMap.put(key,con);
	                        } else {
	                            if (hasMiltipleDependency) con += "*";
	                            connMap.put(shClsName + "-" + getDependency,con);
	                        }}
	                    
	                    if (scopeString == "+" || scopeString == "-") {
	                        if (next) classFields += "; ";
	                        classFields += scopeString;
	                        classFields += " ";
	                        classFields += fNa;
	                        classFields += " : ";
	                        classFields += clF;
	                        next = true;
	                    }
	                    
	        		}}	
	        
	        
	        
	        for(BodyDeclaration bodyDeclaration:listBodyDeclaration){
	        		if(bodyDeclaration instanceof  MethodDeclaration){
	        			MethodDeclaration methodDeclaration = ((MethodDeclaration) bodyDeclaration);
	        			if(ModifierSet.isPublic(methodDeclaration.getModifiers())){
	        			if (methodDeclaration.getName().startsWith("get") || methodDeclaration.getName().startsWith("set")) {
	        				String methDec=methodDeclaration.getName();
	                        String nameOfVariable = methDec.substring(3);
	                        makeFieldPublic.add(nameOfVariable.toLowerCase());
	                    } else {
	                        if (!classMethods.equalsIgnoreCase("")) classMethods =classMethods+ ";";
	                        classMethods=classMethods+ "+ ";
	                        classMethods=classMethods+ methodDeclaration.getName();
	                        classMethods=classMethods+ "(";
	                        List<Parameter> paramList=methodDeclaration.getParameters();
	                        
	                        for (Object childNodeObject : methodDeclaration.getChildrenNodes()) {
	                            if (childNodeObject instanceof Parameter) {
	                                Parameter parameter = (Parameter) childNodeObject;
	                                
	                                String classParameter = parameter.getType().toString();
		                            String nameOfParameter = parameter.getChildrenNodes().get(0).toString();
		                            classMethods =classMethods+ nameOfParameter;
		                            classMethods =classMethods+ " : ";
		                            classMethods =classMethods+= classParameter;
		                            if(classAndInterfaceMap.containsKey(classParameter) && !classOrInterface.isInterface()){
		                            	dependencies =dependencies+ "[" ;
		                            	dependencies =dependencies+ shClsName;
		                            	dependencies =dependencies+ "] uses -.->";
		                            	if (classAndInterfaceMap.get(classParameter)){
		                            		dependencies =dependencies+ "[<<interface>>;";
		                            		dependencies =dependencies+ classParameter+ "]";
		                            	}
		                                else{
		                                	dependencies =dependencies+ "[";
		                                	dependencies =dependencies+ classParameter; 
		                                	dependencies =dependencies+"]";
		                                }
		                            }
		                            dependencies =dependencies+ ",";
	                            } else {
	                            	String childNodeString=childNodeObject.toString();
	                            	String childNodeArray[] = childNodeString.split(" ");
	                                for (String childBody : childNodeArray) {
	                                	
	                                	 if(classAndInterfaceMap.containsKey(childBody) && !classOrInterface.isInterface()){
	                                		 
	 		                            	dependencies =dependencies+ "[";
	 		                            	dependencies =dependencies+ shClsName;
	 		                            	dependencies =dependencies+ "] uses -.->";
	 		                            	if (classAndInterfaceMap.get(childBody)){
	 		                            		dependencies =dependencies+ "[<<interface>>;";
	 		                            		dependencies =dependencies+ childBody;
	 		                            		dependencies =dependencies+"]";
	 		                            	}
	 		                                else{
	 		                                	dependencies =dependencies+"[";
	 		                                	dependencies =dependencies+ childBody;
	 		                                	dependencies =dependencies+ "]";
	 		                                }
	 		                            	dependencies =dependencies+ ",";
	 		                            }}}}
	                        classMethods =classMethods+ ") : "; 
	                        classMethods =classMethods+methodDeclaration.getType();
	        		}}}}
	      
	        	
	        	
	        if (classOrInterface.getExtends() != null) {
	        	dependencies =dependencies+ "[";
	        	dependencies =dependencies+ shClsName;
	        	dependencies =dependencies+ "] ";
	        	dependencies =dependencies+ "-^ ";
	        	dependencies =dependencies+ classOrInterface.getExtends();
	        	dependencies =dependencies+ ",";
	        }
	        if (classOrInterface.getImplements() != null) {
	            List<ClassOrInterfaceType> interfaceList = (List<ClassOrInterfaceType>) classOrInterface.getImplements();
	            for (ClassOrInterfaceType interface1 : interfaceList) {
	            	dependencies =dependencies+ "[";
	            	dependencies =dependencies+ shClsName;
	            	dependencies =dependencies+ "] ";
	            	dependencies =dependencies+ "-.-^ ";
	            	dependencies =dependencies+ "[";
	            	dependencies =dependencies+ "<<interface>>;";
	            	dependencies =dependencies+ interface1;
	            	dependencies =dependencies+ "]";
	            	dependencies =dependencies+ ",";
	            }}

	        output =output+ cName;
	        if (!classFields.isEmpty()){
	        	output =output+ "|";
	        	output =output+ replaceBrack(classFields);
	        }
	        
	        if (!classMethods.isEmpty()){
	        	output =output+ "|";
	        	output =output+ replaceBrack(classMethods);
	        }
	        output =output+ "]";
	        output =output+dependencies;
	        return output;
	   
	    }
	    
	    private String replaceBrack(String inputString) {
	    	inputString = inputString.replace("[", "(");
	    	inputString = inputString.replace(">", ")");
	    	inputString = inputString.replace("<", "(");
	    	inputString = inputString.replace("]", ")");
	        return inputString;
	    }  
	    
	    public void generateClassDiagram(String outputDiagramString) {
	    	OutputStream outputStream=null;
	    	HttpsURLConnection httpsURLConnection=null;
	        try {
	            String yumlLink = "https://yuml.me/diagram/plain/class/";
	            yumlLink+= outputDiagramString;
	            yumlLink+=".png";
	            
	            URL url = new URL(yumlLink);
	            httpsURLConnection = (HttpsURLConnection) url.openConnection();
	            if (httpsURLConnection.getResponseCode() == 200) {
	              File outputFileImage= new File(outputFile);
	            outputStream = new FileOutputStream(outputFileImage);
	            
	            byte[] bytesOfImage = new byte[1024];
	            int read = 0;
	            while ((read = httpsURLConnection.getInputStream().read(bytesOfImage)) != -1) outputStream.write(bytesOfImage, 0, read);
	            
	            }else{  
	            	 System.out.println("Request to generate class diagram failed with errorcode  : " + httpsURLConnection.getResponseCode());
	            }
            
	        } catch (Exception e) {
	           System.out.println(e.getMessage());
	        }finally{
	        	if(outputStream!=null)
					try {
						outputStream.close();
					} catch (IOException e) {
					}
	        	if(httpsURLConnection!=null) httpsURLConnection.disconnect();
	        }
	    }   
    
}
