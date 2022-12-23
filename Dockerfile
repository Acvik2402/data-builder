# back
# устанавливаем самую версию JVM
FROM adoptopenjdk/openjdk8:ubi

RUN export DOCKER_BUILDKIT=0
RUN export COMPOSE_DOCKER_CLI_BUILD=0

# указываем ярлык. Например, разработчика образа и проч. Необязательный пункт.
LABEL maintainer=rabota24021993@gmail.com
# указываем точку монтирования для внешних данных внутри контейнера (как мы помним, это Линукс)
VOLUME /tmp
# указываем, где в нашем приложении лежит джарник
ARG JAR_FILE=build/libs/data-builder-0.0.1-SNAPSHOT.jar
# указываем, envirement variables

ENV DB_URL=jdbc:postgresql://data-builder-db:5432/double-dice-data
ENV DB_USERNAME=postgres
ENV DB_PASS=pg123
# добавляем джарник в образ под именем rebounder-chain-backend.jar
ADD ${JAR_FILE} data-builder.jar

# внешний порт, по которому наше приложение будет доступно извне
EXPOSE 8780
#ENTRYPOINT ["java", "-Dspring.security.oauth2.client.registration.vk-app.serviceSecret=${SERVICE_SECRET}", "-Dspring.security.oauth2.client.registration.vk-app.clientSecret=${CLIENT_SECRET}","-Dspring.security.oauth2.client.registration.vk-app.clientId=${CLIENT_ID}", "-Dspring.datasource.url=${DB_URL}", "-Dspring.datasource.password=${DB_PASS}", "-Dspring.datasource.username=${DB_USERNAME}", "-Dspring.security.oauth2.client.registration.vk-app.defaultUserName=${DEFAULT_USER_NAME}", "-Dspring.security.oauth2.client.registration.vk-app.defaultUserPass=${DEFAULT_USER_PASS}", "-Dspring.security.oauth2.client.registration.vk-app.defaultAdminName=${DEFAULT_ADMIN_NAME}", "-Dspring.security.oauth2.client.registration.vk-app.defaultAdminPass=${DEFAULT_ADMIN_PASS}","-jar", "/data-builder.jar"]
ENTRYPOINT ["java", "-Dspring.security.oauth2.client.registration.vk-app.serviceSecret=${SERVICE_SECRET}", "-Dspring.security.oauth2.client.registration.vk-app.clientSecret=${CLIENT_SECRET}","-Dspring.security.oauth2.client.registration.vk-app.clientId=${CLIENT_ID}", "-Dspring.datasource.url=${DB_URL}", "-Dspring.datasource.password=${DB_PASS}", "-Dspring.datasource.username=${DB_USERNAME}", "-jar", "/data-builder.jar"]