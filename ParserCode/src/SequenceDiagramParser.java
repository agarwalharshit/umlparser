import java.util.*;
import com.github.javaparser.ast.stmt.*;
import java.io.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.*;
import net.sourceforge.plantuml.SourceStringReader;

public class SequenceDiagramParser {
		String inputPath="";
		String seqDiagCode="@startuml\n";
	    String outputPath="";
	    String inputClassName="";
	    String inputFunctionName="";
	   
	    

	    ArrayList<CompilationUnit> cUnit= new ArrayList<CompilationUnit>();
	    HashMap<String, String> methodClassMapping=new HashMap<String, String>();
	    HashMap<String, ArrayList<MethodCallExpr>> methodCallStack= new HashMap<String, ArrayList<MethodCallExpr>>();

	    
	    SequenceDiagramParser(String inputPath,String outputFile) {
	        this.inputPath = inputPath;
	        this.outputPath = inputPath + "/" + outputFile + ".png";
	        
	    }
	    public void generateSequenceDiagram(String inputClassName, String inputFunctionName) throws Exception {
	    	this.inputClassName=inputClassName;
	    	this.inputFunctionName=inputFunctionName;
	    	 readAndParseJavaFile();
	    	 preCompileClasses();
	    	 seqDiagCode = seqDiagCode+ "actor user #black\n";
	    	 seqDiagCode = seqDiagCode+ "user" + " -> " + inputClassName + " : " + inputFunctionName + "\n";
	    	 seqDiagCode = seqDiagCode+ "activate " + methodClassMapping.get(inputFunctionName) + "\n";
	    	 generateSequenceCode(inputFunctionName);
		     seqDiagCode = seqDiagCode+ "@enduml";
		     sequenceDiagramOutputGeneration();
	    }



	    private void generateSequenceCode(String callerFunc) {

	        for (MethodCallExpr mce : methodCallStack.get(callerFunc)) {
	            String callerClass = methodClassMapping.get(callerFunc);
	            String calleeFunc = mce.getName();
	            String calleeClass = methodClassMapping.get(calleeFunc);
	            if (methodClassMapping.containsKey(calleeFunc)) {
	            	seqDiagCode += callerClass + " -> " + calleeClass + " : "+ mce.toStringWithoutComments() + "\n";
	            	seqDiagCode += "activate " + calleeClass + "\n";
	            	generateSequenceCode(calleeFunc);
	                seqDiagCode += calleeClass + " -->> " + callerClass + "\n";
	                seqDiagCode += "deactivate " + calleeClass + "\n";
	            }
	        }
	    }
	    
	    private void preCompileClasses() {
	        for (CompilationUnit compilationUnit : cUnit) {
	        	ClassOrInterfaceDeclaration classOrInterfaceDeclaration=null;
	            List<TypeDeclaration> typeDeclarationList = compilationUnit.getTypes();
	            for (Node node : typeDeclarationList) {
	                classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) node;
	                for (BodyDeclaration bodyDeclaration : ((TypeDeclaration) classOrInterfaceDeclaration).getMembers()) {
	                	MethodDeclaration methodDeclaration;
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
	                                    	
	                                        if ((es1).getExpression() instanceof MethodCallExpr) {
	                                        	MethodCallExpr methodCallExpr=(MethodCallExpr) es1.getExpression();
	                                        	methodCallExprList.add(methodCallExpr);
	                                        } }}}
	                        }
	                        methodCallStack.put(methodDeclaration.getName(), methodCallExprList);
	                        methodClassMapping.put(methodDeclaration.getName(), classOrInterfaceDeclaration.getName());
	                    }
	                }
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
  
	    private void sequenceDiagramOutputGeneration() {
	        OutputStream outputImage;
			try {
				outputImage = new FileOutputStream(outputPath);
			
	        SourceStringReader sourceStringReader = new SourceStringReader(seqDiagCode);
	      
				sourceStringReader.generateImage(outputImage);
			}  catch (FileNotFoundException e) {
				
				System.out.println("FileNotFoundException ocurred"+e.getMessage());
			}	catch (IOException e) {
				System.out.println("IOException ocurred"+e.getMessage());
			}
	    }



	}
