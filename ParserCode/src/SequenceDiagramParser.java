import java.io.*;
import java.util.*;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;

import net.sourceforge.plantuml.SourceStringReader;
public class SequenceDiagramParser {
	 String pumlCode;
	    String inPath="";
	    String outPath="";
	    String inFuncName="";
	    String inClassName="";

	    HashMap<String, String> mapMethodClass;
	    ArrayList<CompilationUnit> cuArray;
	    HashMap<String, ArrayList<MethodCallExpr>> mapMethodCalls;

	    SequenceDiagramParser(String inPath, String inClassName, String inFuncName,String outFile) {
	        this.inPath = inPath;
	        this.outPath = inPath + "/" + outFile + ".png";
	        this.inClassName = inClassName;
	        this.inFuncName = inFuncName;
	        mapMethodClass = new HashMap<String, String>();
	        mapMethodCalls = new HashMap<String, ArrayList<MethodCallExpr>>();
	        pumlCode = "@startuml\n";
	    }

	    public void start() throws Exception {
	        cuArray = getCuArray(inPath);
	        buildMaps();
	        pumlCode += "actor user #black\n";
	        pumlCode += "user" + " -> " + inClassName + " : " + inFuncName + "\n";
	        pumlCode += "activate " + mapMethodClass.get(inFuncName) + "\n";
	        parse(inFuncName);
	        pumlCode += "@enduml";
	        generateDiagram(pumlCode);
	        System.out.println("Plant UML Code:\n" + pumlCode);
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
	        for (CompilationUnit cu : cuArray) {
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
