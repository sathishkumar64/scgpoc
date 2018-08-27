FROM openjdk:alpine

MAINTAINER sathish vasu 

VOLUME /tmp

ADD ./build/libs/product-0.0.1-SNAPSHOT.jar product-0.0.1-SNAPSHOT.jar

RUN sh -c 'touch /product-0.0.1-SNAPSHOT.jar'

ENTRYPOINT ["java","-jar","/product-0.0.1-SNAPSHOT.jar"]