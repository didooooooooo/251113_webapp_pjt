FROM nginx:1.14-alpine
##default.conf 파일에는 tomcat IP 있어서 nginx가 Tomcat 내 파일 가져옴
COPY ./default.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]