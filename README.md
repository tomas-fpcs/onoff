# OnOff

**NOTE! This application is under development, there are known bugs**

## Author

- Tomas Faal Petersson
- tomas@fpcs.se
- [LinkedIn Profile](https://www.linkedin.com/in/tomasfaalpetersson/)

## Description

I heat my home with a automated wood pellet furnace. Due to the current high priceForHour of wood pellets it
is sometimes cheaper to heat using electricity. This application tells me when to turn on the electric heater.
To do this I use an Android device with a relay, and I have written a code to call this application
to know if it should be on or off, hence the name OnOff. (Sorry, couldn't come up with a better name...)

Since I have 25 years of Java experience, writing this application was much easier than trying to implement the
same logic in the Arduino itself. In the Arduino code there is just a loop that calls this application
thru http like once every five minutes to see if it should turn on or off the relay controlling the heater.

**NOTE 1: in the current implementation I only support Sweden.**

**NOTE 2: All prices are in swedish öre (100 öre per 1 krona)**

## Configuration

The MongoDB connection string is configured using the environment variable MONGODB_CONNECTION_STRING

## Usage

https://onoff-511383166465.europe-north1.run.app/api/v1/onoff?price_zone=SE3&markup_percent=10&max_price=40

price_zone: SE1/SE2/SE3/SE4 (If you don't know which your zone you are in, check https://www.svk.se/)
markup_percent: the percentage added by your supplier to the spot price
max_price: the maximum price where you want to use electricity for heating (or cooling)

Response:
```
{
"on": true,
"max-price": 40,
"price-spot": 31,
"price-supplier": 34,
"user-name": "DevUser"
}
```

Optional parameters:

- output_type: JSON/TEXT/MINIMAL
- provider: elprisetjustnu (defaults to "elprisetjustnu", more providers may be added in the future)

## API Documentation

- /v3/api-docs
- /swagger-ui/index.html

## Database

Data is stored in [MongoDB Cloud](https://cloud.mongodb.com/)

See Configuration section above.

The application will create a cluster name "Elpris0".
