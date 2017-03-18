import java.io.File;
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
	  
	  
	  public void readFileFromFolder(){
			String location="/Users/Harshit/LECTURES/202/umlparser/TestClass";
			File files= new File(location);
			for(File file:files.listFiles()){
				System.out.println(file);
				
			}
		}
	  
	  
	
}
