splat: 	cleaner  
	javac *.java
	jar cmf mainClass splat.jar *.class 

clean:	
	-rm *.class  

cleaner:	clean  	
	-rm splat.jar
	-rm splat.zip

run: 
	java -jar splat.jar

distribution:	splat
	zip splat.zip splat.jar data/*.txt data/*.html data/*.png data/options.* help/*.* Demo1/*
