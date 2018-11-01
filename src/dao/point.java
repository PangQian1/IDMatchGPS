package dao;

public class point {
	private double x;//¾­¶È
	private double y;//Î³¶È
	private String time;
	private String v;
	private String direction;
	public boolean isVisit;
	private int cluster;
	private boolean isNoised;
	private static final double EARTH_RADIUS = 6378137;  
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public point(double x,double y){
		this.x=x;
		this.y=y;
		this.time="";
		this.isVisit=false;
		this.cluster=0;
		this.isNoised=false;
	}
	public point(double x,double y,String time){
		this.x=x;
		this.y=y;
		this.time=time;
		this.isVisit=false;
		this.cluster=0;
		this.isNoised=false;
	}
	public point(double x,double y,String time,String v,String direction){
		this.x=x;
		this.y=y;
		this.time=time;
		this.isVisit=false;
		this.cluster=0;
		this.isNoised=false;
		this.v=v;
		this.direction=direction;
	}
	private double rad(double d){  
	    return (d * Math.PI / 180.0);  
	}  
	public double getDistance(point p){
		double lng1=x;
		double lat1=y;
		double lng2=p.x;
		double lat2=p.y;
		double radLat1 = rad(lat1);  
		double radLat2 = rad(lat2);  
		double a = radLat1 - radLat2;  
		double b = rad(lng1) - rad(lng2);  
		double s = (2 * Math.asin(  
	        Math.sqrt(  
	            Math.pow(Math.sin(a/2),2)   
	            + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)  
	        )  
        ));  
        s =(s * EARTH_RADIUS);  
        s = Math.round(s * 10000) / 10000;  
        return s;  
	}
	public boolean isVisit() {
		return isVisit;
	}
	public void setVisit(boolean isVisit) {
		this.isVisit = isVisit;
	}
	public int getCluster() {
		return cluster;
	}
	public void setCluster(int cluster) {
		this.cluster = cluster;
	}
	public boolean isNoised() {
		return isNoised;
	}
	public void setNoised(boolean isNoised) {
		this.isNoised = isNoised;
	}
	public String toString(){
		return y+","+x+","+cluster+","+(isNoised?1:0)+","+time;
	}
	public String getWrite(){
		return y+","+x+","+cluster+","+(isNoised?1:0)+","+time+","+v+","+direction;
	}
	
	public String getV() {
		return v;
	}
	public void setV(String v) {
		this.v = v;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
}
