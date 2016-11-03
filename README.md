# WSO2 Microservices Framework for Java (MSF4J) Sample
This sample demonstrates how to call a SOAP service from MSF4J using the CXF dynamic WSDL client

## How to build this sample
From this directory, run

`mvn clean package`

## How to run this sample

1. Download the 3.1.8 release of Apache CXF from http://cxf.apache.org/download.html

2. Extract the downloaded Apache CXF binary and run the server in the wsdl_first_dynamic_client sample as follows;
`mvn -Dserver`
   This will start the SOAP service.

3. From this directory run;
`java -jar target/msf4j-soap-0.1-SNAPSHOT.jar`

4. Next invoke the MSF4J service as follows;
`curl http://localhost:8080/`

5. You should see the following response:
`Agent name: Orange`

