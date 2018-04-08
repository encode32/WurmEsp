package net.encode.wurmesp;

import java.util.HashMap;


public class CronoManager {
	
	private long timelapse;
	private long time;
	private long remaining;
	private long future;
	private long last;
	
	public enum CronoDataType {
		DAYS, HOURS, MINUTES, SECONDS
	}
	
	public CronoManager(long timelapse)
	{
		this.timelapse = timelapse;
		this.time = System.currentTimeMillis();
		this.future = time + this.timelapse;
		this.remaining = (this.future - System.currentTimeMillis()) /1000;
		this.last = this.remaining;
	}
	
	public void restart(long timelapse)
	{
		this.timelapse = timelapse;
		this.time = System.currentTimeMillis();
		this.future = time + this.timelapse;
		this.remaining = (this.future - System.currentTimeMillis()) /1000;
		this.last = this.remaining;
	}
	
	public HashMap<CronoDataType, Long> getTime()
	{
		this.remaining = (this.future - System.currentTimeMillis()) /1000;
		this.last = this.remaining;
		long days = (long) Math.floor(this.remaining / 86400);
		long hours = (long) Math.floor((this.remaining % 86400) / 3600);
		long minutes = (long) Math.floor(((this.remaining % 86400) % 3600) / 60);
		long seconds = (long) Math.floor(((this.remaining % 86400) % 3600) % 60);
		
		HashMap<CronoDataType, Long> time = new HashMap<CronoDataType, Long>();
		time.put(CronoDataType.DAYS, days);
		time.put(CronoDataType.HOURS, hours);
		time.put(CronoDataType.MINUTES, minutes);
		time.put(CronoDataType.SECONDS, seconds);
		
		return time;
	}
	
	public int getDays()
	{
		this.remaining = (this.future - System.currentTimeMillis()) /1000;
		this.last = this.remaining;
		long days = (long) Math.floor(this.remaining / 86400);
		return (int)days;
	}
	
	public int getHours()
	{
		this.remaining = (this.future - System.currentTimeMillis()) /1000;
		this.last = this.remaining;
		long hours = (long) Math.floor((this.remaining % 86400) / 3600);
		return (int)hours;
	}
	
	public int getMinutes()
	{
		this.remaining = (this.future - System.currentTimeMillis()) /1000;
		this.last = this.remaining;
		long minutes = (long) Math.floor(((this.remaining % 86400) % 3600) / 60);
		return (int)minutes;
	}
	
	public int getSeconds()
	{
		this.remaining = (this.future - System.currentTimeMillis()) /1000;
		this.last = this.remaining;
		long seconds = (long) Math.floor(((this.remaining % 86400) % 3600) % 60);
		return (int)seconds;
	}
	
	public boolean hasNext()
	{
		this.remaining = (this.future - System.currentTimeMillis()) /1000;
		return (this.remaining < this.last) ? true : false;
	}
	
	public boolean hasEnded()
	{
		return (System.currentTimeMillis() > this.future) ? true : false;
	}
}