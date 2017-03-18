
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
	  private void buildMap(ArrayList<CompilationUnit> cuArray) {
	        for (CompilationUnit cu : cuArray) {
	            List<TypeDeclaration> cl = cu.getTypes();
	            for (Node n : cl) {
	                ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) n;
	                map.put(coi.getName(), coi.isInterface()); 
	            }
	        }
	    }
}
