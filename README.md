To install this application you might sure that on your machine was
installed last version of java and maven.  
Open folder with project from terminal and enter command bellow:
mvn clean compile assembly:single 

Than you can rebase produced jar file to another folder and put inputs__1_.zip
file on the same folder.

To start application use command:
java -jar vnovakovskyi-jav02-1.0-SNAPSHOT-jar-with-dependencies.jar

Finally, you can find produced inputsv2.zip file in the same folder.
In the root of .zip file you can find 2 .txt files with sorted unique phone numbers
and email addresses.