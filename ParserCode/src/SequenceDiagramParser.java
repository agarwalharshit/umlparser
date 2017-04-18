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
//	    String inFuncName="";
//	    String inClassName="";

	    HashMap<String, String> mapMethodClass;
	    ArrayList<CompilationUnit> cUnit;
	    HashMap<String, ArrayList<MethodCallExpr>> mapMethodCalls;

	    SequenceDiagramParser(String inputPath, String inClassName, String inFuncName,String outputFile) {
	        this.inputPath = inputPath;
	        this.outputPath = inputPath + "/" + outputFile + ".png";
	        
	        mapMethodClass = new HashMap<String, String>();
	        mapMethodCalls = new HashMap<String, ArrayList<MethodCallExpr>>();
	    }
	    public void generateSequenceDiagram(String inputClassName, String inputFunctionName) throws Exception {
	    	 readAndParseJavaFile();
	    	 buildMaps();
	    	 seqDiagCode += "actor user #black\n";
	    	 seqDiagCode += "user" + " -> " + inClassName + " : " + inFuncName + "\n";
	    	 seqDiagCode += "activate " + mapMethodClass.get(inFuncName) + "\n";
		     parse(inFuncName);
		     seqDiagCode += "@enduml";
		     generateDiagram(seqDiagCode);
		     System.out.println("Plant UML Code:\n" + seqDiagCode);
	    }



	    private void parse(String callerFunc) {

	        for (MethodCallExpr mce : mapMethodCalls.get(callerFunc)) {
	            String callerClass = mapMethodClass.get(callerFunc);
	            String calleeFunc = mce.getName();
	            String calleeClass = mapMethodClass.get(calleeFunc);
	            if (mapMethodClass.containsKey(calleeFunc)) {
	                pumlCode += callerClass + " -> " + calleeClass + " : "
	                        + mce.toStringWithoutComments() + "\n";
	                pumlCode += "activate " + calleeClass + "\n";
	                parse(calleeFunc);
	                pumlCode += calleeClass + " -->> " + callerClass + "\n";
	                pumlCode += "deactivate " + calleeClass + "\n";
	            }
	        }
	    }

	    private void buildMaps() {
	        for (CompilationUnit cu : cUnit) {
	            String className = "";
	            List<TypeDeclaration> td = cu.getTypes();
	            for (Node n : td) {
	                ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) n;
	                className = coi.getName();
	                for (BodyDeclaration bd : ((TypeDeclaration) coi)
	                        .getMembers()) {
	                    if (bd instanceof MethodDeclaration) {
	                        MethodDeclaration md = (MethodDeclaration) bd;
	                        ArrayList<MethodCallExpr> mcea = new ArrayList<MethodCallExpr>();
	                        for (Object bs : md.getChildrenNodes()) {
	                            if (bs instanceof BlockStmt) {
	                                for (Object es : ((Node) bs)
	                                        .getChildrenNodes()) {
	                                    if (es instanceof ExpressionStmt) {
	                                        if (((ExpressionStmt) (es))
	                                                .getExpression() instanceof MethodCallExpr) {
	                                            mcea.add(
	                                                    (MethodCallExpr) (((ExpressionStmt) (es))
	                                                            .getExpression()));
	                                        }
	                                    }
	                                }
	                            }
	                        }
	                        mapMethodCalls.put(md.getName(), mcea);
	                        mapMethodClass.put(md.getName(), className);
	                    }
	                }
	            }
	        }
	     
	    }
//Changed
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
//
		  
		  
		  
		  
	    private String generateDiagram(String source) throws IOException {

	        OutputStream png = new FileOutputStream(outPath);
	        SourceStringReader reader = new SourceStringReader(source);
	        String desc = reader.generateImage(png);
	        return desc;
	    }



	}
