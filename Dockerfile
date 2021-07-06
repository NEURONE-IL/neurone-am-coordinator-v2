FROM maven:3.6.1-jdk-11 as build

COPY  . /home/maven/src

WORKDIR /home/maven/src

RUN mvn clean package

FROM openjdk:11-jdk-slim

RUN mkdir /app

COPY --from=build /home/maven/src/target/websocket-0.0.1-SNAPSHOT.jar /app/target/websocket-0.0.1-SNAPSHOT.jar
WORKDIR /app
RUN ls
CMD ["java","-jar","target/websocket-0.0.1-SNAPSHOT.jar"]
