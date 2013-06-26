deploy:
	mvn clean verify
	scp target/sfearthquakes-*.jar codahale.com:~/sfearthquakes/sfearthquakes.jar
	scp sfearthquakes.properties codahale.com:~/sfearthquakes/sfearthquakes.properties
