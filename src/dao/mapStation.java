package dao;

import java.util.ArrayList;

public class mapStation {
	private int id;
	private String stationId;
	private String stationName;
	private ArrayList<mapSquare> listSquare;
	
	public mapStation(int id,ArrayList<mapSquare> listSquare){
		this.id=id;
		this.stationName="x";
		this.listSquare=listSquare;
	}
	public mapStation(int id,String stationName,ArrayList<mapSquare> listSquare){
		this.id=id;
		this.stationName=stationName;
		this.listSquare=listSquare;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public ArrayList<mapSquare> getListSquare() {
		return listSquare;
	}

	public void setListSquare(ArrayList<mapSquare> listSquare) {
		this.listSquare = listSquare;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	
	public String toString(){
		return this.id+","+this.stationName+","+this.listSquare;
	}
}
