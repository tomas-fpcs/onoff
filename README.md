# OnOff

## Author

- Tomas Faal Petersson
- tomas@fpcs.se
- [LinkedIn Profile](https://www.linkedin.com/in/tomasfaalpetersson/)

## Description

I heat my home with a automated wood pellet furnace. Due to the current high price of wood pellets it
is sometimes cheaper to heat using electricity. This application tells me when to turn on the electric heater.
To do this I use an Android device with a relay, and I have written a code to call this application
to know if it should be on or off, hence the name OnOff. (Sorry, couldn't come up with a better name...)

Since I have 25 years of Java experience, writing this application was much easier than trying to implement the
same logic in the Arduino itself. In the Arduino code there is just a loop that calls this application
thru http like once every five minutes to see if it should turn on or off the relay controlling the heater.

**NOTE 1: in the current implementation I only support Sweden.**

**NOTE 2: All prices are in swedish öre (100 öre per 1 krona)**

## API Documentation

- /v3/api-docs
- /swagger-ui/index.html

## Database
### In-memory db for dev

http://localhost:8080/h2-console
