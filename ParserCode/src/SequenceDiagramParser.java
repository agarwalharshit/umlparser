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
