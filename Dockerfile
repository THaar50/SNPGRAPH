FROM openjdk:16-alpine

ENV DISPLAY=host.docker.internal:0.0

RUN apk --no-cache update && apk upgrade && apk add bash libxtst libxi libxrender sqlite xterm

RUN apk --no-cache add msttcorefonts-installer fontconfig && update-ms-fonts && fc-cache -f

RUN mkdir /snpgraph

WORKDIR /snpgraph

COPY . .

RUN chmod +x gradlew

RUN ./gradlew build

WORKDIR /snpgraph/build/libs

CMD xterm
#ENTRYPOINT ["java", "-jar", "snpgraph-0.1.0.jar"]
