# quarkus-tcpserver

## Testing quarkus prometheus metrics zeroed after 3-4 hours

```
# Start tcpserver with.

mvn clean compile quarkus:dev

# then download and install ncat.exe utility

http://nmap.org/dist/ncat-portable-5.59BETA1.zip

# Run ncat.exe with HELLO message from echo.txt in the bash directory multiple times.
# This will update some timers metrics and one counter metric. Also, it echoes back to you the
# HELLO message. You might watch the console log of server to see the received message.

ncat.exe -C localhost 8080 < bash/echo.txt

# in the browser look at updated prometheus metrics

http://localhost:18080/q/metrics

# examples of timers
# tcp_request_standardperiod_seconds
# tcp_request_period_seconds

# for a counter
tcp_requests_count_total  

# then let the tcpserver to be on its own without any further requests for 3-4 hours. Latter, call again 
# ncat.exe as above multiple times. The timers' metrics will be sill zeroed and 
# the conter metric gets updated.
```
