.PHONY: prepare start

prepare:
	mvn clean install

start:
	mvn spring-boot:run
