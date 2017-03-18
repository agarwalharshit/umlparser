import java.util.ArrayList;

public class ParserEngine {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public void start() throws Exception {
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
	  
	  
	  private ArrayList<> getCuArray(String inPath)
	            throws Exception {
	        File folder = new File(inPath);
	        ArrayList<CompilationUnit> cuArray = new ArrayList<CompilationUnit>();
	        for (final File f : folder.listFiles()) {
	            if (f.isFile() && f.getName().endsWith(".java")) {
	                FileInputStream in = new FileInputStream(f);
	                CompilationUnit cu;
	                try {
	                    cu = JavaParser.parse(in);
	                    cuArray.add(cu);
	                } finally {
	                    in.close();
	                }
	            }
	        }
	        return cuArray;
	    }
	  
	  
	
}
