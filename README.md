Steps to create uml parser:-
Step 1) Earlier I wrote a code to read java files from a folder.
Step 2) Then I wrote code to covert tese files into compilation unit and save into list.
Step 3) Now I am working on how to parse these files. I have decided to use javaparser to parse these files. Fillowing are the links which i am using to gather deep understand about javaparser:-
http://javaparser.org/#parse
http://javaparser.org/#transform
http://javaparser.org/#generate

Sequence Diagram:

Sample Command to Run: -
java -jar sequence.jar seq "/Users/Harshit/Desktop/Race/uml-sequence-test" Main main SeqImage

Parameters:
1)seq/umlparser: - For sequence diagram give seq.
2)Input file path: - It reads the input java Files from this path.
3)Class Name: -This starting class of our sequence diagram. In demo, starting class is Main.
4)Method Name: -This method to be run. In demo, method is main.
5)Output Image name: - Name of the output file.

Note:- Output file will be generated on same path as Input folder in .png format


