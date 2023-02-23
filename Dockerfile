FROM maven:latest as maven_builder
RUN mkdir -p /
WORKDIR /
COPY pom.xml /
RUN mvn -B dependency:resolve dependency:resolve-plugins
COPY src /src
RUN mvn package


#FROM tomcat:latest
#
#EXPOSE 8082
#WORKDIR /app
#COPY --from=maven_builder /app/target/spring-mvc-app1.war /usr/local/tomcat/webapps/ROOT.war
#
#ENV POSTGRES_USER postgres
#ENV POSTGRES_PASSWORD qwerty
#ENV POSTGRES_DB project1
