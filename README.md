# convertor-app
Reactive REST endpoints to handle currency conversion.

## Getting Started:

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
See testing part for notes on how to conduct testing system.


### Prerequisites:

- java 11
- maven 3.5+

### Installing:

1. Git clone current repository:  *git clone ...*
2. Go inside work folder: *cd convertor-app*
2. Build application: *mvn clean install*
3. Run application: *./mvnw spring-boot:run (on windows: mvnw spring-boot)*
4. Check is application is running: *curl http://localhost:8080*

### Rest endpoints:

POST: http://localhost:8080/currency/converter
```
{
    "from": "USD",
    "to": "EUR",
    "amount": 10
}
```
List of supported bases: USD, EUR, CAD, PLN, GBP *(in case need more, this enum [Bases.class](https://github.com/temporaryusernamedeveloper/convertor-app/blob/master/src/main/java/com/uss/convertorapp/enums/Bases.java) take care of it.)*.

### How to write tests:

As testing the code is not mandatory, here only covered unit testing of rest controller.(Run unit tests: mvn test).
Regarding that must be done:

1. Unit testing of rest API, to make sure, that client can't send bad data.(negative values, empty values, not existing currency etc.)
2. Unit testing of services which responsible for conversion: (positive scenarious(when number:0, negative, Scaling and rounding number, super big), and negative (when providers return null, or empty values)).
3. Unit testing of provider by itself based on mock response(Also test avaibility, and switching to another providers: test scenariou, which was done in task).
4. Integration tests of providers to make sure that API is not changed(As I understand we don't have any SLA with external services).
5. Integration tests of REST endpoint of app by itself.


### Notes
As extra task, there were implemented internal memory caching. (If we have one application instance it's enough,
but if need scaling we have to move to external caching).

### IMPORTANT:
Everything is impleemnted due task description.
But I propose another solution, which will have another system characteristics: availability, distributive, and don't overload external system(As we don't have any SLA from external providers).
