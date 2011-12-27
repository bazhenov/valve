# Valve Project
Valve Project is nonblocking HTTP-proxy server written in Java specially for proxying backend web-services (REST or SOAP).

# What is nonblocking proxy?
It's just a very efficient proxy capable to serve thousands of requests per second using commodiate hardware.

# Why another proxy?
When proxying REST-based web-service typically you faced with some needs which are not solved by Nginx, Varnish, HAproxy etc. For example:

* demultiplex client request on several machines for more stable performance ("the first one is best one");
* using cache not *behind* the backend but as a *fallback* if backend doesn't respond in fixed time;
* duplicate all the traffic to the some backyard server for traffic analysis or load testing.

So, I'm not trying to write just another HTTP-proxy server. I'm trying to solve specific problems of web-services scalability.