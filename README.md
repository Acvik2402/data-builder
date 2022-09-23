# data-builder
Приложения для сбора статистики по юзерам в различных группах Вконтакте
После добавления группы с базу данных в заданном интервале мониторит участников на выбывших и вступивших и создает объект аналитики
Используется API VK, PGSQL, Spring Boot, Thymeleaf

## Deployment
Deployment process as easy as possible:
Required software:
- terminal for running bash scripts
- docker
- docker-compose

to deploy application, switch to needed branch and run bash script:

$ bash start.sh ${bot_username} ${bot_token}

That's all.****
