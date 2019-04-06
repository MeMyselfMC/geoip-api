package eu.theindra.geoip;

import java.io.File;
import java.io.OutputStream;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;

import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import org.bukkit.plugin.java.JavaPlugin;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;

public class GeoipAPI extends JavaPlugin {
	
	public static DatabaseReader databaseReader = null;
	
	private File localDatabaseCopy = new File(getDataFolder(), "GeoLite2-City.mmdb");
	
	public void onEnable() {
		getLogger().info("This plugin includes GeoLite2 data created by MaxMind, available from https://www.maxmind.com.");
		
		boolean initializationFailed = false;
		try {
			if(!localDatabaseCopy.exists()) {
				getLogger().info("The GeoLite2 City database will be downloaded.");
				
				download();
			} else if((System.currentTimeMillis() - localDatabaseCopy.lastModified()) >= 604800000) {
				getLogger().info("The local GeoLite2 City database copy is more than 7 days old. The latest version will be downloaded.");
				
				download();
			}
		} catch(IOException ex) {
			getLogger().warning("Something went wrong while downloading the GeoLite2 City database. The plugin will be disabled.");
			
			initializationFailed = true;
		}
		
		if(!initializationFailed) {
			try {
				databaseReader = new DatabaseReader.Builder(localDatabaseCopy).withCache(new CHMCache()).build();
			} catch(IOException ex) {
				getLogger().warning("Something went wrong while accessing the GeoLite2 City database. The plugin will be disabled.");
				
				initializationFailed = true;
			}
		}
		
		if(initializationFailed) getServer().getPluginManager().disablePlugin(this);
		else getLogger().info("GeoipAPI v" + getDescription().getVersion() + " has been enabled!");
	}
	
	public void onDisable() {
		if(databaseReader != null) {
			try {
				databaseReader.close();
			} catch (IOException ex) {
				getLogger().warning("An error occured while closing the database reader.");
			}
		}
		
		getLogger().info("GeoipAPI v" + getDescription().getVersion() + " has been disabled.");
	}
	
	private void download() throws IOException {
		URLConnection connection = new URL("https://geolite.maxmind.com/download/geoip/database/GeoLite2-City.tar.gz").openConnection();
		connection.connect();
		
		if(!getDataFolder().exists()) getDataFolder().mkdir();
		
		try(ArchiveInputStream i = new TarArchiveInputStream(new GzipCompressorInputStream(connection.getInputStream()))) {
			ArchiveEntry entry = null;
			
			while((entry = i.getNextEntry()) != null) {
				if(!entry.isDirectory() && entry.getName().endsWith(".mmdb")) {
					File f = new File(getDataFolder(), entry.getName().substring(entry.getName().lastIndexOf("/")));
					
					try(OutputStream o = Files.newOutputStream(f.toPath())) {
						IOUtils.copy(i, o);
					}
				}
			}
		}
	}
	
}