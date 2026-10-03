## Flight Dispatcher (Quarkus)

Before each departure, an airline dispatcher decides whether a flight can be **released** (GO / NO-GO).
They check that the crew, the destination weather and the aircraft limits allow the flight. Then they build the
**fuel plan** and check that the loaded aircraft does not exceed its maximum takeoff weight.

This sample is a Quarkus application: the only framework dependency is the starter

```xml
<dependency>
    <groupId>com.fit.bru</groupId>
    <artifactId>quarkus-starter</artifactId>
    <version>1.1.0</version>
</dependency>
```

The dispatch rule flow (`FlightDispatchDefinition`) is an `@ApplicationScoped` bean, so the starter registers it in the
`BusinessRuleEngine`, which `DispatchService` uses to dispatch each flight.

> The figures below are simplified, illustrative values. Do not use them for real flight operations.

# Dispatch rules

The rules are executed in this order. Every safety check is run, so all the reasons for a NO-GO are reported.
If the flight fails one of them, no fuel plan is calculated.

### 1. Aircraft limits ([aircraft-limits.md](src/main/resources/aircraft-limits.md))

| Aircraft | Operating empty weight (kg) | Max takeoff weight (kg) | Max crosswind (kt) | Taxi fuel (kg) | Final reserve fuel (kg) |
|:---------|:----------------------------|:------------------------|:-------------------|:---------------|:------------------------|
| A320     | 42600                       | 78000                   | 38                 | 200            | 1100                    |
| B737     | 41400                       | 79000                   | 33                 | 200            | 1150                    |
| A350     | 142400                      | 283000                  | 35                 | 500            | 2700                    |
| B787     | 128850                      | 254000                  | 40                 | 450            | 2500                    |

### 2. Crew duty ([crew-duty.md](src/main/resources/crew-duty.md))

| Duty hours | Rest hours | Rejection reason                         |
|:-----------|:-----------|:-----------------------------------------|
| > 13       |            | Crew flight duty period exceeds 13 hours |
|            | < 10       | Crew rest is below the 10 hours minimum  |

### 3. Destination weather ([destination-weather.md](src/main/resources/destination-weather.md))

Every matching row is applied, and the most restrictive category wins (`VMC` when no row matches).

| Ceiling (ft) | Visibility (m) | Weather category |
|:-------------|:---------------|:-----------------|
| < 200        |                | BELOW_MINIMUMS   |
|              | < 550          | BELOW_MINIMUMS   |
| < 1000       |                | MARGINAL         |
|              | < 5000         | MARGINAL         |

### 4. Weather minimums

A destination `BELOW_MINIMUMS` is a NO-GO: *Destination weather is below landing minimums*.

### 5. Crosswind

A crosswind stronger than the aircraft limit is a NO-GO, e.g. *Crosswind of 42 kt exceeds the A320 limit of 38 kt*.

### 6. Fuel plan

| Component     | Quantity                                                                              |
|:--------------|:--------------------------------------------------------------------------------------|
| Taxi          | from the aircraft limits                                                              |
| Trip          | from the flight plan                                                                  |
| Contingency   | share of the trip fuel ([contingency-fuel.md](src/main/resources/contingency-fuel.md)), rounded up |
| Alternate     | fuel to divert to the alternate airport, only when the destination weather is `MARGINAL` |
| Final reserve | 30 minutes of holding, from the aircraft limits                                       |

| Flight time (min) | Contingency rate |
|:------------------|:-----------------|
| < 360             | 5%               |
| >= 360            | 3%               |

`block fuel = taxi + trip + contingency + alternate + final reserve`

### 7. Takeoff weight

`takeoff weight = operating empty weight + payload + block fuel - taxi fuel`, with
`payload = passengers × 100 kg + cargo`. A takeoff weight above the MTOW is a NO-GO.

# Exercise 1

Flight **FD101**: an A320 with 150 passengers and 2000 kg of cargo, 2 hours and 5000 kg of trip fuel. The destination
is clear (3000 ft, 10 km), with a crosswind of 15 kt. The crew has been on duty for 8 hours, after 12 hours of rest.

```
Taxi             200
Trip            5000
Contingency      250   (5% of 5000)
Final reserve   1100
Block fuel      6550

Takeoff weight 65950   = 42600 + 17000 + 6350  (MTOW 78000)

DISPATCHED
```

# Exercise 2

Flight **FD202**: a B787 with 280 passengers and 15000 kg of cargo, 10 hours and 60000 kg of trip fuel. The destination
is marginal (800 ft, 3 km), so an alternate is planned (4000 kg). Crosswind 20 kt, 12 hours of duty, 14 hours of rest.

```
Taxi             450
Trip           60000
Contingency     1800   (3% of 60000)
Alternate       4000
Final reserve   2500
Block fuel     68750

Takeoff weight 240150  = 128850 + 43000 + 68300  (MTOW 254000)

DISPATCHED
```

# Exercise 3

Flight **FD303**: a B737 with 189 passengers and 4000 kg of cargo, 5.5 hours and 18000 kg of trip fuel, in good weather.

```
Block fuel     20250   = 200 + 18000 + 900 + 1150
Takeoff weight 84350   = 41400 + 22900 + 20050

NOT_DISPATCHED: Takeoff weight of 84350 kg exceeds the MTOW of 79000 kg
```

# Exercise 4

Flight **FD404**: an A320 with a crosswind of 42 kt, a destination ceiling of 150 ft, and a crew on duty for 14 hours
after 8 hours of rest.

```
NOT_DISPATCHED:
- Crew flight duty period exceeds 13 hours
- Crew rest is below the 10 hours minimum
- Destination weather is below landing minimums
- Crosswind of 42 kt exceeds the A320 limit of 38 kt
```

# Run the application

```shell
mvn quarkus:dev
```

```shell
curl -X POST http://localhost:8080/api/dispatch -H "Content-Type: application/json" -d '{
  "flightNumber": "FD101",
  "aircraftType": "A320",
  "passengers": 150,
  "cargoKg": 2000,
  "tripFuelKg": 5000,
  "alternateFuelKg": 1500,
  "flightTimeMinutes": 120,
  "destinationCeilingFeet": 3000,
  "destinationVisibilityMeters": 10000,
  "crosswindKnots": 15,
  "crewDutyHours": 8,
  "crewRestHours": 12
}'
```

```json
{
  "flightNumber": "FD101",
  "status": "DISPATCHED",
  "rejectionReasons": [],
  "weatherCategory": "VMC",
  "alternateRequired": false,
  "fuel": {
    "TAXI": 200,
    "TRIP": 5000,
    "CONTINGENCY": 250,
    "FINAL_RESERVE": 1100
  },
  "blockFuel": 6550,
  "takeoffWeight": 65950,
  "maxTakeoffWeight": 78000
}
```

> The framework and the starter must be installed in the local repository first: `mvn install` from the project root.
