FROM amazoncorretto:17
RUN yum update -y && \
    yum install -y python3 && \
    yum clean all
COPY build/libs/app.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]