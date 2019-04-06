# geoip-api
https://www.spigotmc.org/resources/api-geoip.28015/

**MaxMind GeoLite Legacy databases were discontinued on January 2, 2019. This fork aims to maintain utmost compatibility with plugins that utilize GeoipAPI, while using GeoLite2.**

---
#### How does this fork differ from the original?

1. Re-written the majority of the codebase.
	* Cleaned up unused code.
	* Use more optimized methods overall.
	* Prevented multiple NPEs in the process.
2. The "DMA Code" and "Area Code" fields are not supported as they are not provided by GeoLite2.
3. The local database copy is automatically updated every 7 days.
4. More descriptive console output.

In 90% of cases this will work as a drop-in replacement for the old GeoipAPI without the need to make adjustments to dependent code. No packages, classes, or methods have been re-named and the plugin will register with the same name.

---

**What does this do?**

With this API you can fetch the location of a player's IP address from the GeoLite2 database provided by MaxMind at https://dev.maxmind.com/geoip/geoip2/geolite2/.


**How do I use it?**

Add the following to your plugin.yml:
```yaml
depend: [GeoipAPI]
```
---

* Get the IP address of a player:
```java
InetAddress address = player.getAddress();
```

* Make sure that the IP address is not null:
```java
if(address == null) {
	...
}
```

* Check if the IP is a local address:
```java
if(address.isAnyLocalAddress() || address.isLoopbackAddress()) {
	...
}
```

* Get the location data of the address:
```java
GeoIP addressLocationData = new GeoIP(address);
addressLocationData.countryName;
addressLocationData.city;
```