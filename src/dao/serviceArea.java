package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.gps;
import data.preProcess;

public class serviceArea {
	public static String serviceArea="D:/服务区/服务区.csv";
	public static String cqServiceArea="D:/服务区/重庆服务区.csv";
	public static Map<String,ArrayList<String>> getCqServiceArea(String serviceArea,String cqServiceArea){
		Map<String,ArrayList<String>> mapServiceXy=new HashMap<>();
		try{
			BufferedReader reader=io.getReader(serviceArea,"gbk");
			BufferedWriter writer=io.getWriter(cqServiceArea, "gbk");
			String line="";
			while((line=reader.readLine())!=null){
				String[] data=line.split(",",3);
				String serviceId=data[0];
				String lng=data[1];
				String lat=data[2];
				if(!lng.equals("")&&!lat.equals("")){
					double lngService=Double.valueOf(lng);
					double latService=Double.valueOf(lat);
					if(preProcess.isInCity(latService, lngService)){
						writer.write(serviceId+","+lat+","+lng+"\n");
						String xy=passStation.getXY(lngService, latService, 100);
						if(mapServiceXy.containsKey(xy)){
							ArrayList<String> listId=mapServiceXy.get(xy);
							listId.add(serviceId+","+lat+","+lng);
							mapServiceXy.put(xy, listId);
						}else{
							ArrayList<String> listId=new ArrayList<>();
							listId.add(serviceId+","+lat+","+lng);
							mapServiceXy.put(xy, listId);
						}
					}
				}
			}
			reader.close();
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return mapServiceXy;
	}
	
	public static String getOneServiceArea(double lat,double lng,ArrayList<String> nearService){
		double min=Double.MAX_VALUE;
		String finalServiceArea="";
		for(int i=0;i<nearService.size();i++){
			String service=nearService.get(i);
			String serviceId=service.split(",")[0];
			double latService=Double.parseDouble(service.split(",")[1]);
			double lngService=Double.parseDouble(service.split(",")[2]);
			double distance=gps.getDistance(lng, lat, lngService, latService);
			if(min>distance){
				min=distance;
				finalServiceArea=serviceId;
			}
		}
		return finalServiceArea;
	}
	
	public static Map<String,String> readCqServiceArea(String cqServiceArea){
		Map<String,String> map=new HashMap<>();
		try{
			BufferedReader reader=io.getReader(cqServiceArea, "gbk");
			String line="";
			while((line=reader.readLine())!=null){
				String[] data=line.split(",");
				String serviceId=data[0];
				String gps=data[1]+","+data[2];
				map.put(serviceId, gps);
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	public static void main(String[] args){
		getCqServiceArea(serviceArea,cqServiceArea);
	}
}
