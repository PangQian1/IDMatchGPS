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
	private static String PNamechongqingMid="D:/mapinfo_ditu/��ͼ/level2/level2/chongqing/other/PNamechongqing.mid";
	private static String POIchongqingMid="D:/mapinfo_ditu/��ͼ/level2/level2/chongqing/index/POIchongqing.mid";
	private static String CChongqingMid="G:/���µ�ͼ/road2018q2/chongqing/road/Cchongqing.mid";
	private static String CChongqingMif="G:/���µ�ͼ/road2018q2/chongqing/road/Cchongqing.mif";
	private static String outPath16Chongqing="G:/��ͼ/�շ�վ����/16PoiChongqing.csv";
	private static String outPath18Chongqing="G:/��ͼ/�շ�վ����/18PoiChongqing.csv";
	
//	private static String CChongqingMid16="D:/mapinfo_ditu/��ͼ/level2/level2/chongqing/road/Cchongqing.mid";
//	private static String CChongqingMif16="D:/mapinfo_ditu/��ͼ/level2/level2/chongqing/road/Cchongqing.mif";
//	private static String outPath16Chongqing2="G:/��ͼ/�շ�վ����/16PoiChongqing2.csv";
	private static String cqStationName="G:/�½��ļ���/����/����վ���.csv";
	private static String allStation="G:/�½��ļ���/����/allStation_0608.txt";
	/**
	 * ��ȡCmid�ļ�
	 * @param pathCmid Cmid�ļ�·��
	 * @return �����к�Ϊkey���Ըõ�Ϊ��ڵ�·��id���Ըõ�Ϊ���ڵ�·��id�������͵�ֵΪvalue��map��
	 * �÷�������mid�ļ�����ȡ·���ϵ������շ�վ����Ϣ
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
	 * �ϲ�cmid��cmif�ļ��������ͼ���շ�վ�ľ�γ�ȡ�����������շ�վ��ÿ��·����ţ��͸��շ�վ�ľ�γ��
	 * @param pathCmid cmid�ļ�·��
	 * @param pathCmif	cmif�ļ�·��
	 * @param outPath18 ���·��
	 */
	public static void getTollGps(String pathCmid,String pathCmif,String outPath18){
		BufferedReader reader=io.getReader(pathCmif,"GBK");
		BufferedWriter writer=io.getWriter(outPath18, "GBK");
		String line="";
		int lineId=0;
		try{
			writer.write("linkId,lng,lat\n");
			Map<Integer,String> idWithPointMessage=readCMid(pathCmid);
			
		/*	//����map			
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
				String inLinkId=v[0]; //�����ߺ���
				String outLinkId=v[1];  //�˳��ߺ���
				String contentType=v[2]; //��ͨ��������
				String gps=v[3]; //��γ��
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
				String inLinkId=v[0]; //�����ߺ���
				String outLinkId=v[1];  //�˳��ߺ���
				String contentType=v[2]; //��ͨ��������
				String gps=v[3]; //��γ��
				String lngLat=gps.split(" ",2)[0]+","+gps.split(" ",2)[1];
				writer.write(inLinkId+","+lngLat+"\n");
				writer.write(outLinkId+","+lngLat+"\n");
			}
			
			Set setPoint2=idWithPointMessage2.entrySet();
			Map.Entry[] entriesPoint2 = (Map.Entry[])setPoint2.toArray(new Map.Entry[setPoint2.size()]);
			for(int i=0;i<entriesPoint2.length;i++){
				String value=entriesPoint2[i].getValue().toString();
				String[] v=value.split(";",4);
				String inLinkId=v[0]; //�����ߺ���
				String outLinkId=v[1];  //�˳��ߺ���
				String contentType=v[2]; //��ͨ��������
				String gps=v[3]; //��γ��
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
	 * ��ȡpNameMid�ļ����ҳ�����Ϊ�շ�վ��poiId������
	 * @param pNameMidPath	pNameMid�ļ�����·��
	 * @return	������poiIdΪkey������Ϊֵ��map
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
				if(name.endsWith("�շ�վ")){
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
	 * ��poiMid�ļ����ҵ���Ӧ���շ�վ�ĵ�ľ�γ��
	 * @param pNameMidPath	pNameMid�ļ�·��
	 * @param poiMidPath	poiMid�ļ�·��
	 * @param outPath16		���·��
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
	 * ��ȡ18���շ�վGPS��16��poi��Ϣ���жԱȣ�ƥ�����γ����ͬ���շ�վ���õ��侭γ�ȶ�Ӧ������
	 * @param tollStation18Path	18���շ�վGPS�ļ�
	 * @param poi16Path			16��poi�����ļ�
	 * @return	����ƥ���ϵ��շ�վ�����շ�վ��gpsΪkey������Ϊvalue��map
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
			System.out.println("2018�����շ�վ����"+setToll.size());
			System.out.println("2016�����շ�վ����"+count);
			System.out.println("2018��2016�շ�վƥ������"+gpsTollName.size());
		}catch(Exception e){
			e.printStackTrace();
		}
		return gpsTollName;
	}
	
	/**
	 * 16��18��gpsƥ���ϵ��շ�վ�����շ�վ��gpsΪkey������Ϊvalue��map�������շ�վ����Ϊkey�����շ��������շ�վidΪvalue��map����ƥ��
	 * @param tollStation18Path	18���շ�վGPS�ļ�
	 * @param poi16Path			16��poi�����ļ�
	 * @param cqStationName		�շ��������շ�վ���ƺ��շ�վid���ļ�
	 * @return	������ƥ���ϵ��շ�վgpsΪkey���շ�վidΪvalue��map
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
				String name=data[1]+"�շ�վ";
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
			System.out.println("����ƥ���ϵ��շ�վ������"+count);
		}catch(Exception e){
			e.printStackTrace();
		}
		return mapGpsToStationId;
	}
	
	public static void main(String[] args){
		write16PoiGps(PNamechongqingMid,POIchongqingMid,outPath16Chongqing);
		getTollGps(CChongqingMid,CChongqingMif,outPath18Chongqing);//���18���ͼ�շ�վ��γ��
//		matchGps(outPath18Chongqing,outPath16Chongqing);
//		matchGpsToStationId(outPath18Chongqing,outPath16Chongqing,cqStationName);
	}
}
