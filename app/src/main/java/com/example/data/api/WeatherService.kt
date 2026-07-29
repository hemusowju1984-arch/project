package com.example.data.api

data class WeatherDayForecast(
    val dayName: String,
    val condition: String,
    val iconType: String, // SUN, RAIN, CLOUDY, STORM
    val maxTemp: Int,
    val minTemp: Int,
    val rainProbPercent: Int,
    val humidityPercent: Int
)

data class AgricultureWeatherInfo(
    val locationName: String = "Kurnool Farm Zone",
    val currentTemp: Double = 29.5,
    val condition: String = "Partly Cloudy",
    val maxTemp: Int = 34,
    val minTemp: Int = 23,
    val humidityPercent: Int = 62,
    val windSpeedKmH: Double = 14.2,
    val rainProbabilityPercent: Int = 15,
    val uvIndex: Int = 7,
    val evapotranspirationMm: Double = 4.2,
    val sunriseTime: String = "05:52 AM",
    val sunsetTime: String = "06:48 PM",
    val activeAlerts: List<String> = listOf(
        "Moderate Rain Expected on Wednesday evening (+25mm)",
        "Optimal spraying window tomorrow 06:00 AM - 09:00 AM"
    ),
    val sevenDayForecast: List<WeatherDayForecast> = listOf(
        WeatherDayForecast("Today", "Partly Cloudy", "CLOUDY", 34, 23, 15, 62),
        WeatherDayForecast("Mon", "Sunny & Warm", "SUN", 35, 24, 10, 58),
        WeatherDayForecast("Tue", "Overcast", "CLOUDY", 32, 22, 35, 68),
        WeatherDayForecast("Wed", "Thunderstorm", "STORM", 29, 21, 80, 85),
        WeatherDayForecast("Thu", "Moderate Rain", "RAIN", 30, 22, 65, 78),
        WeatherDayForecast("Fri", "Clear Sky", "SUN", 33, 23, 20, 60),
        WeatherDayForecast("Sat", "Sunny", "SUN", 35, 24, 5, 55)
    )
)

object WeatherService {
    fun getCurrentAgriWeather(): AgricultureWeatherInfo = AgricultureWeatherInfo()
}
