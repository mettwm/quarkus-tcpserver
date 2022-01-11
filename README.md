# quarkus-tcpserver

## Testing quarkus prometheus metrics zeroed after 3-4 hours

```
# start tcpserver with

mvn clean compile quarkus:dev

# download and install ncat.exe utility

http://nmap.org/dist/ncat-portable-5.59BETA1.zip

# run ncat.exe with HELLO message from bash directory multiple times
# this will update some timers and counter metrics and echo you back HELLO message
# you can also watch the console log of server

ncat.exe -C localhost 8080 < bash/echo.txt

# in the browser look at updated metrics

http://localhost:18080/q/metrics

# examples of timers
# tcp_request_standardperiod_seconds
# tcp_request_period_seconds

# for a counter
tcp_requests_count

# then let the server to be on its own without any other requests for 3-4 hours and then call again 
# ncat.exe as above multiple times and timer metrics are not going to be sill zeroed
# the conter metric gets updated
```
