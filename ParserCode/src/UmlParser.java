
public class UmlParser {

	public static void main(String[] args) {
		System.out.println("Hello");
		
		  if (args[0].equals("class")) {
			  System.out.println(args[0]+"  "+args[1]+" "+args[2]);
	           
	        }else if (args[0].equals(("seq"))) {
	        	  System.out.println(args[0]+"  "+args[1]+" "+args[2]);
	        } 
		  else {
	            System.out.println("Invalid keyword " + args[0]);
	            System.out.println("Invalid keyword " + args[1]);
	        }
	}
	
}
