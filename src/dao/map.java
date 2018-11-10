package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dao.io;
import dao.mapSquare;
import dao.mapStation;

public class map {
	private static String PNamechongqingMid="D:/mapinfo_ditu/地图/level2/level2/chongqing/other/PNamechongqing.mid";
	private static String POIchongqingMid="D:/mapinfo_ditu/地图/level2/level2/chongqing/index/POIchongqing.mid";
	private static String CChongqingMid="G:/最新地图/road2018q2/chongqing/road/Cchongqing.mid";
	private static String CChongqingMif="G:/最新地图/road2018q2/chongqing/road/Cchongqing.mif";
	private static String outPath16Chongqing="G:/地图/收费站数据/16PoiChongqing.csv";
	private static String outPath18Chongqing="G:/地图/收费站数据/18PoiChongqing.csv";
	
//	private static String CChongqingMid16="D:/mapinfo_ditu/地图/level2/level2/chongqing/road/Cchongqing.mid";
//	private static String CChongqingMif16="D:/mapinfo_ditu/地图/level2/level2/chongqing/road/Cchongqing.mif";
//	private static String outPath16Chongqing2="G:/地图/收费站数据/16PoiChongqing2.csv";
	private static String cqStationName="G:/新建文件夹/重庆/重庆站点表.csv";
	private static String allStation="G:/新建文件夹/重庆/allStation_0608.txt";
	/**
	 * 读取Cmid文件
	 * @param pathCmid Cmid文件路径
	 * @return 返回行号为key，以该点为入口的路链id、以该点为出口的路链id，点类型等值为value的map，
	 * 该方法处理mid文件，获取路链上的所有收费站点信息
	 */
	public static Map<Integer,String> readCMid(String pathCmid){
		Map<Integer,String> idWithPointMessage=new HashMap<>();
		BufferedReader reader=io.getReader(pathCmid,"GBK");
		String line="";
		int lineId=0;
		try{
			while((line=reader.readLine())!=null){
				lineId++;
				String[] data=line.split(",",10);
				String inLinkId=data[3].replaceAll("\"", "").trim();
				String outLinkId=data[4].replaceAll("\"", "").trim();
				String CondType=data[5].replaceAll("\"", "").trim();
				if(CondType.equals("3")){
					idWithPointMessage.put(lineId, inLinkId+";"+outLinkId+";"+CondType);
				}
			}
			reader.close();
//			System.out.println("Rhebei.mid:"+lineId);
		}catch(Exception e){
			e.printStackTrace();
		}
		return idWithPointMessage;
	}
	/**
	 * 合并cmid和cmif文件，输出地图中收费站的经纬度。输出包含有收费站的每个路链编号，和该收费站的经纬度
	 * @param pathCmid cmid文件路径
	 * @param pathCmif	cmif文件路径
	 * @param outPath18 输出路径
	 */
	public static void getTollGps(String pathCmid,String pathCmif,String outPath18){
		BufferedReader reader=io.getReader(pathCmif,"GBK");
		BufferedWriter writer=io.getWriter(outPath18, "GBK");
		String line="";
		int lineId=0;
		try{
			writer.write("linkId,lng,lat\n");
			Map<Integer,String> idWithPointMessage=readCMid(pathCmid);
			
		/*	//遍历map			
			Set<Entry<Integer, String>> entrySet = idWithPointMessage.entrySet();
			Iterator<Entry<Integer, String>> iterator = entrySet.iterator();
			while(iterator.hasNext()) {
				Entry<Integer, String> entry = iterator.next();
				Integer key = entry.getKey();
				String value = entry.getValue();
				System.out.println(key + " = " + value);
			}*/
			
			while((line=reader.readLine())!=null){
				if(line.startsWith("Point")){
					String f="";
					lineId++;
					if(idWithPointMessage.containsKey(lineId)){
						String[] data=line.split(" ",3);
						f+=data[1]+" "+data[2];
						String value=idWithPointMessage.get(lineId);
						value=value+";"+f;
						idWithPointMessage.put(lineId, value);
					}
				}
			}
			reader.close();
			System.out.println("mif:"+lineId);
			
			Set setPoint=idWithPointMessage.entrySet();
			Map.Entry[] entriesPoint = (Map.Entry[])setPoint.toArray(new Map.Entry[setPoint.size()]);
			for(int i=0;i<entriesPoint.length;i++){
				String value=entriesPoint[i].getValue().toString();
				String[] v=value.split(";",4);
				String inLinkId=v[0]; //进入线号码
				String outLinkId=v[1];  //退出线号码
				String contentType=v[2]; //交通限制类型
				String gps=v[3]; //经纬度
				String lngLat=gps.split(" ",2)[0]+","+gps.split(" ",2)[1];
				writer.write(inLinkId+","+lngLat+"\n");
				writer.write(outLinkId+","+lngLat+"\n");
			}
			writer.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void getTollGps2(String pathCmid1,String pathCmif1,String pathCmid2,String pathCmif2,String outPath18){
		BufferedReader reader1=io.getReader(pathCmif1,"GBK");
		BufferedWriter writer=io.getWriter(outPath18, "GBK");
		String line="";
		int lineId=0;
		try{
			writer.write("linkId,lng,lat\n");
			Map<Integer,String> idWithPointMessage1=readCMid(pathCmid1);
			
			while((line=reader1.readLine())!=null){
				if(line.startsWith("Point")){
					String f="";
					lineId++;
					if(idWithPointMessage1.containsKey(lineId)){
						String[] data=line.split(" ",3);
						f+=data[1]+" "+data[2];
						String value=idWithPointMessage1.get(lineId);
						value=value+";"+f;
						idWithPointMessage1.put(lineId, value);
					}
				}
			}
			reader1.close();
			System.out.println("mif1:"+lineId);
			
			BufferedReader reader2=io.getReader(pathCmif2,"GBK");
			line="";
			lineId=0;
			Map<Integer,String> idWithPointMessage2=readCMid(pathCmid2);
			
			while((line=reader2.readLine())!=null){
				if(line.startsWith("Point")){
					String f="";
					lineId++;
					if(idWithPointMessage2.containsKey(lineId)){
						String[] data=line.split(" ",3);
						f+=data[1]+" "+data[2];
						String value=idWithPointMessage2.get(lineId);
						value=value+";"+f;
						idWithPointMessage2.put(lineId, value);
					}
				}
			}
			reader2.close();
			System.out.println("mif2:"+lineId);
			
			Set setPoint1=idWithPointMessage1.entrySet();
			Map.Entry[] entriesPoint1 = (Map.Entry[])setPoint1.toArray(new Map.Entry[setPoint1.size()]);
			for(int i=0;i<entriesPoint1.length;i++){
				String value=entriesPoint1[i].getValue().toString();
				String[] v=value.split(";",4);
				String inLinkId=v[0]; //进入线号码
				String outLinkId=v[1];  //退出线号码
				String contentType=v[2]; //交通限制类型
				String gps=v[3]; //经纬度
				String lngLat=gps.split(" ",2)[0]+","+gps.split(" ",2)[1];
				writer.write(inLinkId+","+lngLat+"\n");
				writer.write(outLinkId+","+lngLat+"\n");
			}
			
			Set setPoint2=idWithPointMessage2.entrySet();
			Map.Entry[] entriesPoint2 = (Map.Entry[])setPoint2.toArray(new Map.Entry[setPoint2.size()]);
			for(int i=0;i<entriesPoint2.length;i++){
				String value=entriesPoint2[i].getValue().toString();
				String[] v=value.split(";",4);
				String inLinkId=v[0]; //进入线号码
				String outLinkId=v[1];  //退出线号码
				String contentType=v[2]; //交通限制类型
				String gps=v[3]; //经纬度
				String lngLat=gps.split(" ",2)[0]+","+gps.split(" ",2)[1];
				writer.write(inLinkId+","+lngLat+"\n");
				writer.write(outLinkId+","+lngLat+"\n");
			}
			
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 读取pNameMid文件，找出其中为收费站的poiId和名称
	 * @param pNameMidPath	pNameMid文件输入路径
	 * @return	返回以poiId为key，名称为值的map
	 */
	public static Map<String,String> getTollStationPoiId(String pNameMidPath){
		Map<String,String> poiIdToll=new HashMap<>();
		BufferedReader reader=io.getReader(pNameMidPath,"GBK");
		String line="";
		try{
			while((line=reader.readLine())!=null){
				String[] data=line.split(",",10);
				String poiId=data[0].replaceAll("\"", "").trim();
				String name=data[2].replaceAll("\"", "").trim();
				if(name.endsWith("收费站")){
					poiIdToll.put(poiId, name);
				}
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return poiIdToll;
	}
	/**
	 * 在poiMid文件中找到对应的收费站的点的经纬度
	 * @param pNameMidPath	pNameMid文件路径
	 * @param poiMidPath	poiMid文件路径
	 * @param outPath16		输出路径
	 */
	public static void write16PoiGps(String pNameMidPath,String poiMidPath,String outPath16){
		BufferedReader reader=io.getReader(poiMidPath,"GBK");
		BufferedWriter writer=io.getWriter(outPath16, "GBK");
		String line="";
		try{
			Map<String,String> poiIdToll=getTollStationPoiId(pNameMidPath);
			writer.write("poiId,stationName,lng,lat\n");
			while((line=reader.readLine())!=null){
				String[] data=line.split(",",23);
				String poiId=data[7].replaceAll("\"", "").trim();
				String linkId=data[12].replaceAll("\"", "").trim();
				String lng=data[5].replaceAll("\"", "").trim();
				String lat=data[6].replaceAll("\"", "").trim();
				if(poiIdToll.containsKey(poiId)){
					String tollName=poiIdToll.get(poiId);
					writer.write(poiId+","+tollName+","+lng+","+lat+"\n");
				}
			}
			reader.close();
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 读取18年收费站GPS与16年poi信息进行对比，匹配出经纬度相同的收费站，得到其经纬度对应的名称
	 * @param tollStation18Path	18年收费站GPS文件
	 * @param poi16Path			16年poi数据文件
	 * @return	返回匹配上的收费站，以收费站的gps为key，名称为value的map
	 */
	public static Map<String,String> matchGps(String tollStation18Path,String poi16Path){
		Set<String> setToll=new HashSet<>();
		Map<String,String> gpsTollName=new HashMap<>();
		try{
			BufferedReader readerTollStation=io.getReader(tollStation18Path, "GBK");
			String line="";
			readerTollStation.readLine();
			while((line=readerTollStation.readLine())!=null){
				String[] data=line.split(",",3);
				String lng=data[1];
				String lat=data[2];
				setToll.add(lat+";"+lng);
			}
			readerTollStation.close();
			
			BufferedReader readerPoi=io.getReader(poi16Path, "GBK");
			line="";
			readerPoi.readLine();
			int count=0;
			while((line=readerPoi.readLine())!=null){
				count++;
				String[] data=line.split(",", 4);
				String stationName=data[1];
				String lng=data[2];
				String lat=data[3];
				String key=lat+";"+lng;
				if(setToll.contains(key)){
					gpsTollName.put(key, stationName);
				}
			}
			readerPoi.close();
			System.out.println("2018年总收费站数："+setToll.size());
			System.out.println("2016年总收费站数："+count);
			System.out.println("2018与2016收费站匹配数："+gpsTollName.size());
		}catch(Exception e){
			e.printStackTrace();
		}
		return gpsTollName;
	}
	
	/**
	 * 16与18年gps匹配上的收费站，以收费站的gps为key，名称为value的map，与以收费站名称为key，以收费数据中收费站id为value的map进行匹配
	 * @param tollStation18Path	18年收费站GPS文件
	 * @param poi16Path			16年poi数据文件
	 * @param cqStationName		收费数据中收费站名称和收费站id对文件
	 * @return	返回以匹配上的收费站gps为key，收费站id为value的map
	 */
	public static Map<String,String> matchGpsToStationId(String tollStation18Path,String poi16Path,String cqStationName){
		Map<String,String> mapGpsToStationId=new HashMap<>();
		try{
			Map<String,String> gpsTollName=matchGps(tollStation18Path,poi16Path);
			Map<String,String> nameToStationId=new HashMap<>();
			BufferedReader reader=io.getReader(cqStationName, "GBK");
			String line="";
			
			while((line=reader.readLine())!=null){
				String[] data=line.split(",",2);
				String name=data[1]+"收费站";
				String stationId=data[0];
				nameToStationId.put(name, stationId);
			}
			reader.close();
			
			int count=0;
			for(String gps:gpsTollName.keySet()){
				String name=gpsTollName.get(gps);
				if(nameToStationId.containsKey(name)){
					count++;
					mapGpsToStationId.put(gps.split(";",2)[0]+","+gps.split(";",2)[1], nameToStationId.get(name));
//					System.out.println(gps+":"+name+","+nameToStationId.get(name));
				}
			}
			System.out.println("名字匹配上的收费站数量："+count);
		}catch(Exception e){
			e.printStackTrace();
		}
		return mapGpsToStationId;
	}
	
	public static void main(String[] args){
		write16PoiGps(PNamechongqingMid,POIchongqingMid,outPath16Chongqing);
		getTollGps(CChongqingMid,CChongqingMif,outPath18Chongqing);//输出18年地图收费站经纬度
//		matchGps(outPath18Chongqing,outPath16Chongqing);
//		matchGpsToStationId(outPath18Chongqing,outPath16Chongqing,cqStationName);
	}
}
