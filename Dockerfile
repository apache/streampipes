FROM nginx

#COPY streampipes-ui /usr/share/nginx/html

COPY img/** /usr/share/nginx/html/streampipes-ui/
COPY css/** /usr/share/nginx/html/streampipes-ui/
COPY index.html /usr/share/nginx/html/streampipes-ui/
COPY bundle.js /usr/share/nginx/html/streampipes-ui/
COPY app/**/*.html /usr/share/nginx/html/streampipes-ui/
COPY templates/*.html /usr/share/nginx/html/streampipes-ui/


COPY nginx_config/nginx.conf /etc/nginx/nginx.conf
COPY nginx_config/default.conf /etc/nginx/conf.d/default.conf

RUN chown -R nginx:nginx /usr/share/nginx/html/
