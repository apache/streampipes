# Use an official base image
FROM ubuntu:latest

# Install curl and jq
RUN apt-get update \
    && apt-get install -y curl jq

# Copy the Bash scripts into the image
COPY upload.sh /usr/local/bin/upload.sh
COPY wait-for-it.sh /usr/local/bin/wait-for-it.sh

# Make the scripts executable
RUN chmod +x /usr/local/bin/upload.sh
RUN chmod +x /usr/local/bin/wait-for-it.sh

# Run the script
CMD ["/usr/local/bin/wait-for-it.sh", "backend:8030", "--", "/usr/local/bin/upload.sh"]
