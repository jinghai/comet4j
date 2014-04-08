set extDocJarFile="ext-doc.jar"  
set extDocXMLFile="comet4j-js-doc.xml"  
set extDocTemplate="template/ext/template.xml"  
set outputFolder="../docs"  
java -jar -Dfile.encoding=UTF-8 %extDocJarFile% -p %extDocXMLFile% -o %outputFolder% -t %extDocTemplate%  -verbose

echo. & pause