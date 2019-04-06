package eu.theindra.geoip.api;

import java.io.IOException;
import java.net.InetAddress;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.exception.GeoIp2Exception;

import eu.theindra.geoip.GeoipAPI;

public class GeoPlayer {
	
	public String countryCode;
	public String countryName;
	public String region;
	public String city;
	public String postalCode;
	public Double longitude;
	public Double latitude;
	public String time_zone;
	public Integer metro_code;
	
	public GeoPlayer(Player player) {
		InetAddress address = player.getAddress().getAddress();
		
		if(GeoipAPI.databaseReader != null) {
			try {
				CityResponse response = GeoipAPI.databaseReader.city(address);
				
				Country country = response.getCountry();
				this.countryCode = country.getIsoCode();
				this.countryName = country.getName();
				
				this.region = response.getMostSpecificSubdivision().getName();
				
				this.city = response.getCity().getName();
				
				this.postalCode = response.getPostal().getCode();
				
				Location location = response.getLocation();
				this.longitude = location.getLongitude();
				this.latitude = location.getLatitude();
				this.metro_code = location.getMetroCode();
				this.time_zone = location.getTimeZone();
			} catch(IOException | GeoIp2Exception ex) {
				Bukkit.getLogger().warning("Something went wrong while fetching GeoLite2 data for player " + player.getName() + " (" + address.toString() + ").");
			}
		} else Bukkit.getLogger().info("Attempt to fetch GeoLite2 data for player " + player.getName() + " (" + address.toString() + ") failed because the database has not yet been initialized.");
	}
	
}