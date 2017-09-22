FROM nginx

#COPY streampipes-ui /usr/share/nginx/html

COPY img/ /usr/share/nginx/html/
COPY css/ /usr/share/nginx/html/
COPY index.html /usr/share/nginx/html/
COPY bundle.js /usr/share/nginx/html/
COPY app/ /usr/share/nginx/html/
COPY templates/ /usr/share/nginx/html/

COPY site/ /usr/share/nginx/html/docs

COPY nginx_config/nginx.conf /etc/nginx/nginx.conf
COPY nginx_config/default.conf /etc/nginx/conf.d/default.conf

RUN chown -R nginx:nginx /usr/share/nginx/html/
