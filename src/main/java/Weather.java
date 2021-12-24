public class Weather {
    public String weather;
    public Integer temp;
    public String iconID;
    public String countryName;

    public Weather(String weather, Integer temp, String iconID, String countryName) {
        this.weather = weather;
        this.temp = temp;
        this.iconID = iconID;
        this.countryName = countryName;
    }
}