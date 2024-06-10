# OnOff

**NOTE: this application is still under development**

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

## Usage

### Swagger / OpenApi

- /v3/api-docs
- /swagger-ui/index.html

## Example
```
curl -G \
    -H "x-api-key: 47e4de80-9881-4914-9b78-8b3706be4235" \
    --data-urlencode "provider=elprisetjustnu" \
    --data-urlencode "price_zone=SE3" \
    --data-urlencode "markup_percent=0" \
    --data-urlencode "max_price=40" \
    --data-urlencode "output_type=JSON" \
http://localhost:8080/api/<version>/onoff
```

### Parameters

#### provider
The provider of the spot prices, currently only https://www.elprisetjustnu.se/

Valid values: "elprisetjustnu"

#### price_zone
The price zone you are in, check with your network provider or google if you don't know

Valid values: "SE1", "SE2", "SE3", "SE4"

#### markup_percent
The number of percent your provider adds on top of the spot price. I do not know the exact number 
for my provider, but I estimated it by comparing the price in their app with the spot price.

Valid values: 0 to infinity

#### max_price
This is the max price at which you want the electric heater turned on. Is compared with spot price plus markup.

Valid values: 0 to infinity

#### output_type (optional)
The format you want the response in, JSON is default

Valid values: "JSON", "TEXT", "MINIMALIST"

## Response

- For output_type TEXT and MINIMALIST the Content-Type is "text/plain"
- For output_type JSON the Content-Type is "application/json"

## Build and deploy 

docker stop onoff
mvn clean install -U  
docker build -t onoff .
docker run -p 8080:8080 onoff

## Database
### In-memory db for dev

http://localhost:8080/h2-console
