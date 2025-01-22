FROM postgres:latest AS postgres
COPY schema.sql /docker-entrypoint-initdb.d/schema.sql
EXPOSE 5432:5432