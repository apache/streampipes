FROM nginx

COPY img/ /usr/share/nginx/html/img/
COPY lib/ /usr/share/nginx/html/lib/
COPY css/ /usr/share/nginx/html/css/
COPY index.html /usr/share/nginx/html/
COPY bundle.js /usr/share/nginx/html/
COPY app/ /usr/share/nginx/html/app/
COPY templates/ /usr/share/nginx/html/templates

COPY site/ /usr/share/nginx/html/docs
COPY javadoc/ /usr/share/nginx/html/javadoc

COPY nginx_config/nginx.conf /etc/nginx/nginx.conf
COPY nginx_config/default.conf /etc/nginx/conf.d/default.conf

RUN chown -R nginx:nginx /usr/share/nginx/html/
