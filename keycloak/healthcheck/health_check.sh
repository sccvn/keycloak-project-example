#!/bin/bash
# The script sends an HTTP GET request to the Keycloak server and checks if the response contains the string  "HTTP/1.1 200 OK" . If the response contains this string, the script exits with a status code of  0 , which indicates that the server is healthy. If the response does not contain this string, the script exits with a status code of  1 , which indicates that the server is unhealthy.
# The script can be executed from the command line to check the health of the Keycloak server.
# $ ./keycloak.sh

# Define the host and port
HOST="localhost"
PORT="8080"
URL="/auth/health"

# Send the HTTP request and store the response
RESPONSE=$(exec 3<>/dev/tcp/$HOST/$PORT && echo -e "GET $URL HTTP/1.1\r\nHost: $HOST\r\nConnection: close\r\n\r\n" >&3 && cat <&3)

# Check if the response contains "HTTP/1.1 200 OK"
if echo "$RESPONSE" | grep -q "HTTP/1.1 200 OK"; then
  exit 0
else
  exit 1
fi

