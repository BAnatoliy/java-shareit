#FROM amazoncorretto:11
FROM amazoncorretto:11-alpine-jdk
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9091
#ENV TZ="Asia/Vladivostok"
#RUN date
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]