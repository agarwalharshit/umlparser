
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
				System.out.println("Exception ocurred"+e.getMessage());
			}
		
		}else{
			SequenceDiagramParser sdp = new SequenceDiagramParser(args[1], args[4]);
			try {
				sdp.generateSequenceDiagram(args[2], args[3]);
			} catch (Exception e) {
				System.out.println("Exception ocurred"+e.getMessage());
			}		
		}
	}
}
