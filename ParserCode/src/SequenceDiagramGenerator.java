import java.util.*;
import com.github.javaparser.ast.stmt.*;
import java.io.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.*;
import com.github.javaparser.ast.expr.MethodCallExpr;

import net.sourceforge.plantuml.SourceStringReader;

public class SequenceDiagramGenerator {
		String iloc="";
		String sequenceDiagramCd="@startuml\n";
	    String oloc="";
	    String inpClass="";
	    String inpFunction="";
	   
	    

	    ArrayList<CompilationUnit> compilationUnitArray= new ArrayList<CompilationUnit>();
	    HashMap<String, String> methodClassMapping=new HashMap<String, String>();
	    HashMap<String, ArrayList<MethodCallExpr>> methodCallStack= new HashMap<String, ArrayList<MethodCallExpr>>();

	    
	    SequenceDiagramGenerator(String iloc,String olic) {
	        this.iloc += iloc;
	        this.oloc =this.oloc+ iloc;
	        this.oloc=this.oloc+ "/";
	        this.oloc=this.oloc+ olic;
	        this.oloc=this.oloc + ".png";
	        
	    }
	    public void sequenceDiagramCreate(String inpClass, String inpFunction) throws Exception {
	    	this.inpClass=inpClass;
	    	this.inpFunction=inpFunction;
	    	 readAndParseJavaFile();
	    	 beforeCompilation();
	    	 sequenceDiagramCd = sequenceDiagramCd+ "actor user #black\n";
	    	 sequenceDiagramCd = sequenceDiagramCd+ "user" + " -> " + inpClass + " : " + inpFunction + "\n";
	    	 sequenceDiagramCd = sequenceDiagramCd+ "activate " + methodClassMapping.get(inpFunction) + "\n";
	    	 generateSequenceCode(inpFunction);
		     sequenceDiagramCd = sequenceDiagramCd+ "@enduml";
		     System.out.println(sequenceDiagramCd);
		     sequenceDiagramOutputGeneration();
	    }



	    private void generateSequenceCode(String callerFunc) {

	    	String clrClass="";
	    	String cFunc="";
	    	String cleeClas ="";
	    	
	        for (MethodCallExpr methodCallExpr : methodCallStack.get(callerFunc)) {
	        	clrClass = methodClassMapping.get(callerFunc);
	            cFunc = methodCallExpr.getName();
	            cleeClas = methodClassMapping.get(cFunc);
	            if (methodClassMapping.containsKey(cFunc)) {
	            	sequenceDiagramCd =sequenceDiagramCd+ clrClass;
	            	sequenceDiagramCd =sequenceDiagramCd+ " -> ";
	            	sequenceDiagramCd =sequenceDiagramCd+ cleeClas;
	            	sequenceDiagramCd =sequenceDiagramCd+ " : ";
	            	sequenceDiagramCd =sequenceDiagramCd+ methodCallExpr.toStringWithoutComments();
	            	sequenceDiagramCd =sequenceDiagramCd+ "\n";
	            	sequenceDiagramCd =sequenceDiagramCd+ "activate ";
	            	sequenceDiagramCd =sequenceDiagramCd+ cleeClas;
	            	sequenceDiagramCd =sequenceDiagramCd+ "\n";
	            	generateSequenceCode(cFunc);
	            	sequenceDiagramCd =sequenceDiagramCd+ cleeClas;
	            	sequenceDiagramCd =sequenceDiagramCd+ " -->> ";
	            	sequenceDiagramCd =sequenceDiagramCd+ clrClass;
	            	sequenceDiagramCd =sequenceDiagramCd+ "\n";
	            	sequenceDiagramCd =sequenceDiagramCd+ "deactivate ";
	            	sequenceDiagramCd =sequenceDiagramCd+ cleeClas;
	            	sequenceDiagramCd =sequenceDiagramCd+ "\n";
	            }}}
	    
	    public void beforeCompilation() {
	        for (CompilationUnit compilationUnit : compilationUnitArray) {
	        	ClassOrInterfaceDeclaration classOrInterfaceDeclaration=null;
	        	
	            List<TypeDeclaration> typeDeclarationList = compilationUnit.getTypes();
	            for (Node node : typeDeclarationList)
	            {
	            	
	                classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) node;
	                for (BodyDeclaration bodyDeclaration : ((TypeDeclaration) classOrInterfaceDeclaration).getMembers()) {
	                	
	                	MethodDeclaration methodDeclaration=null;
	                	
	                    if (bodyDeclaration instanceof MethodDeclaration) {
	                    	
	                        methodDeclaration = (MethodDeclaration) bodyDeclaration;
	                        ArrayList<MethodCallExpr> methodCallExprList;
	                        
	                        methodCallExprList = new ArrayList<MethodCallExpr>();
	                        
	                        for (Object blockStatementObject : methodDeclaration.getChildrenNodes()) {
	                            if (blockStatementObject instanceof BlockStmt) {
	                            	Node n1=(Node) blockStatementObject;
	                            	
	                                for (Object expressionStatementObject : n1.getChildrenNodes()) {
	                                	
	                                    if (expressionStatementObject instanceof ExpressionStmt) {
	                                    	ExpressionStmt es1= (ExpressionStmt)expressionStatementObject;
	                                    	
	                                        if (((es1).getExpression()) instanceof MethodCallExpr) {
	                                        	MethodCallExpr methodCallExpr=(MethodCallExpr) es1.getExpression();
	                                        	methodCallExprList.add(methodCallExpr);
	                                        } 
	                                        }
	                                    }
	                                }
	                        }
	                        String name=methodDeclaration.getName();
	                        String ClassName=classOrInterfaceDeclaration.getName();
	                        methodCallStack.put(name, methodCallExprList);
	                        methodClassMapping.put(name, ClassName);
	                    }
	                }
	            }
	        }
	    }

		  void readAndParseJavaFile(){
			  File filesFromFolder=null;
			  filesFromFolder=new File(iloc);
			  File[] files=filesFromFolder.listFiles();
			  for(File file:files){
				  if(file.getName().matches("(.)*\\.java$")){
					  try {
						  CompilationUnit cu=(CompilationUnit)JavaParser.parse(file);
						  compilationUnitArray.add(cu);
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					  
				  }
				  }
			  }
  
		  
		  
	    private void sequenceDiagramOutputGeneration() {
	        OutputStream outputImage;
			try {
				outputImage = new FileOutputStream(oloc);
			
	        SourceStringReader sourceStringReader = new SourceStringReader(sequenceDiagramCd);
	      
				sourceStringReader.generateImage(outputImage);
			}  catch (FileNotFoundException e) {
				
				System.out.println("FileNotFoundException ocurred"+e.getMessage());
			}	catch (IOException e) {
				System.out.println("IOException ocurred"+e.getMessage());
			}
	    }



	}
