FROM openjdk:16-alpine

ENV DISPLAY=host.docker.internal:0.0

RUN apk --no-cache update && apk upgrade && apk add bash libxtst libxi sqlite

RUN apk --no-cache add msttcorefonts-installer fontconfig && update-ms-fonts && fc-cache -f

RUN mkdir /snpgraph

WORKDIR /snpgraph

COPY . .

RUN ./gradlew build

WORKDIR /snpgraph/build/libs

ENTRYPOINT ["java", "-jar", "snpgraph-0.1.0.jar"]
