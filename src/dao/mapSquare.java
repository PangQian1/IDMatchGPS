package dao;

public class mapSquare {
	private String lng;
	private String lat;
	private int stationIndex;
	
	public mapSquare(String lng,String lat,int stationIndex){
		this.lng=lng;
		this.lat=lat;
		this.stationIndex=stationIndex;
	}

	public String getLng() {
		return lng;
	}


	public void setLng(String lng) {
		this.lng = lng;
	}


	public String getLat() {
		return lat;
	}


	public void setLat(String lat) {
		this.lat = lat;
	}


	public int getStationIndex() {
		return stationIndex;
	}

	public void setStationIndex(int stationIndex) {
		this.stationIndex = stationIndex;
	}
	
	public String toString(){
		return this.lat+","+this.lng+","+this.stationIndex;
	}
}
