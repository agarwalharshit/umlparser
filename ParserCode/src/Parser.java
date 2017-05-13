
public class Parser {

	public static void main(String[] args) {
		if(args.length<3){
			System.out.println("Invalid Input parameters");
			return;
		}
		if(args[0].equalsIgnoreCase("umlparser")){
			UMLParserGenerator parserEngine= new UMLParserGenerator(args[1],args[2]);
			try {
				parserEngine.createClassDiagram();
			} catch (Exception e) {
				System.out.println("Exception ocurred"+e.getMessage());
			}
		}else{
			
			if(args.length<4){
				System.out.println("Invalid Input parameters");
				return;
			}
			
			SequenceDiagramGenerator sequenceDiagramParser = new SequenceDiagramGenerator(args[1], args[4]);
			try {
				sequenceDiagramParser.sequenceDiagramCreate(args[2], args[3]);
			} catch (Exception e) {
				System.out.println("Exception ocurred"+e.getMessage());
			}		
		}
	}
}
