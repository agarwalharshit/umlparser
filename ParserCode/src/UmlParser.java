
public class UmlParser {

	public static void main(String[] args) {
		if(args.length<2){
			System.out.println("Invalid Input parameters");
		}
		if(args[0].equalsIgnoreCase("class")){
		ParserEngine pe= new ParserEngine(args[1],args[2]);
			try {
				pe.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}else{
			SequenceDiagramParser sdp = new SequenceDiagramParser(args[1], args[2], args[3], args[4]);
			try {
				sdp.generateSequenceDiagram();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	

	
	
}
