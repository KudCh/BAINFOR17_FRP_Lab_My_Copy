# Team Abandoned Dashboard
Reactive Programming project for BAINFOR17.

## Structure
* `pom.xml` contains the dependencies and project definition.
* `src/main/java/App` binds together the dashboard.
* `src/main/java/ClockFeature` contains the necessary components for the dashboards clock and date.
* `src/main/java/CryptoFeature` contains the necessary components for the dashboards' section about cryptocurrencies.
* `src/main/java/FillBarGameFeature` contains the necessary components for a game where a user can repeatedly click a button to fill a progressbar, but the progress decays over time. 
* `src/main/java/Weather` contains the Weather Class.
* `src/main/java/WeatherFeature` contains the necessary components for the dashboards weather display feature.
### Features
The App contains the following features:
1. Digital clock and Date: continuously refreshed.
2. Cryptocurrencies: fetches and displays information about some cryptocurrencies.
3. Weather feature: weather information about users location.
4. Fill the Bar game: click fast enough to fill the progressbar to 100 %.

## Run
`mvn clean javafx:run`
