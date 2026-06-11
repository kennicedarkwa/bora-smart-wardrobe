FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

COPY src src
COPY scripts scripts

RUN ./gradlew clean bootJar --no-daemon


FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update && \
    apt-get install -y python3 python3-pip && \
    rm -rf /var/lib/apt/lists/*

COPY requirements.txt .

RUN pip3 install --break-system-packages -r requirements.txt
COPY --from=build /app/build/libs/*.jar app.jar
COPY --from=build /app/scripts /app/scripts

RUN mkdir -p /app/uploads

EXPOSE 1531

CMD ["java", "-jar", "app.jar"]