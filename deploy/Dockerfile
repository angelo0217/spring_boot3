FROM gradle:7.6-jdk17 as cache
ENV DEBIAN_FRONTEND=noninteractive
ENV SERVICE_NAME="spring_boot3"

RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home

WORKDIR /workspace/${SERVICE_NAME}/

COPY ../build.gradle /workspace/${SERVICE_NAME}/

RUN gradle dependencies

FROM gradle:7.6-jdk17 as build

ENV DEBIAN_FRONTEND=noninteractive
ENV SERVICE_NAME="spring_boot3"

COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle

COPY ../ /workspace/${SERVICE_NAME}/
WORKDIR /workspace/${SERVICE_NAME}

RUN gradle bootJar -i --stacktrace -x test --no-daemon


FROM azul/zulu-openjdk-debian:17

ENV DEBIAN_FRONTEND=noninteractive
ENV SERVICE_NAME="spring_boot3"

RUN apt-get update && \
    apt-get install --yes --no-install-recommends tzdata && \
    ln -fs /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    dpkg-reconfigure -f noninteractive tzdata


WORKDIR /workspace/${SERVICE_NAME}
COPY --from=build /workspace/spring_boot3/build/libs/*.jar ./app.jar
ENTRYPOINT exec java -Xmx2048M -Xms1024M -jar app.jar