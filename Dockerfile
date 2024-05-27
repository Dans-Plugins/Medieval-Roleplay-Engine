FROM maven:3.8-eclipse-temurin-21-alpine

# Install dependencies g`
RUN apk add --no-cache git wget

# Build server
WORKDIR /testmcserver-build
RUN wget -O BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
RUN git config --global --unset core.autocrlf || :
RUN java -jar BuildTools.jar --rev 1.20.6

# Build plugin
WORKDIR /mre-build
COPY . .
RUN mvn package

# Copy resources and make post-create.sh executable
COPY ./.testcontainer /resources
RUN chmod +x /resources/post-create.sh

# Run server
WORKDIR /testmcserver
EXPOSE 25565
ENTRYPOINT /resources/post-create.sh
