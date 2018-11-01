package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import data.gps;

public class passStation {
	/**
	 * ��������������շ�վ��γ�ȣ������շ�վ����
	 * @param bLng	�������꾭��
	 * @param bLat	��������γ��
	 * @param lng	�շ�վ����
	 * @param lat	�շ�վγ��
	 * @return ����x��y�ַ���
	 */
	public static String getXY(double lng,double lat,int d){
		double[] a=gps.transform(Double.parseDouble("28.1446523718"), Double.parseDouble("105.3039432470"));
		double bLng=a[1];
		double bLat=a[0];
		double x=Math.round(gps.getDistance(bLng, lng, lat, lat)/d);
		double y=Math.round(gps.getDistance(lng, lng, bLat, lat)/d);
		return x+","+y;
	}
	/**
	 * �������ͼ�����½ǵĵ�Ϊ�������ģ����ݾ��룬����ÿ���շ�վ��Ӧ��x��y���꣬��100��Ϊ��λ����
	 * @param tollCollection
	 * @return ����ÿ���շ�վ������
	 */
	public static Map<String,ArrayList<String>> getGrid(String tollCollection){
		double[] a=gps.transform(Double.parseDouble("28.1446523718"), Double.parseDouble("105.3039432470"));
		double bLng=a[1];
		double bLat=a[0];
		Map<String,ArrayList<String>> mapGrid=new HashMap<>();
		try{
			BufferedReader reader=io.getReader(tollCollection,"GBK");
			reader.readLine();
			String line="";
			while((line=reader.readLine())!=null){
				String[] data=line.split(",",3);
				double lng=Double.parseDouble(data[1]);
				double lat=Double.parseDouble(data[2]);
				String coordinate=getXY(lng,lat,50);
				if(mapGrid.containsKey(coordinate)){
					ArrayList<String> list=mapGrid.get(coordinate);
					list.add(lat+";"+lng);
					mapGrid.put(coordinate, list);
				}else{
					ArrayList<String> list=new ArrayList<>();
					list.add(lat+";"+lng);
					mapGrid.put(coordinate, list);
				}
				reader.readLine();
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return mapGrid;
	}
	/**
	 * �ж��ٶ�Ϊ0��ͣ�����Ƿ���һ���շ�վ����Χ
	 * @param x	�켣���x����
	 * @param y	�켣���y����
	 * @param mapGrid	�շ�վ��Ӧ�����λ��
	 * @return	����켣�����շ�վ��Χ�򷵻��շ�վ��list�����û�ڷ��ؿ�
	 */
	public static ArrayList<String> isPassToll(String xy,Map<String,ArrayList<String>> mapGrid){
		double x=Double.valueOf(xy.split(",",2)[0]);
		double y=Double.valueOf(xy.split(",",2)[1]);
		for(double i=x-1;i<=x+1;i++){
			for(double j=y-1;j<=y+1;j++){
				if(mapGrid.containsKey(i+","+j)){
					return mapGrid.get(i+","+j);
				}
			}
		}
		return null;
	}
	public static String getOneStation(double lat,double lng,ArrayList<String> isPassingToll){
		double min=Double.MAX_VALUE;
		String tollGps="";
		for(int i=0;i<isPassingToll.size();i++){
			String stationGps=isPassingToll.get(i);
			double latToll=Double.parseDouble(stationGps.split(";",2)[0]);
			double lngToll=Double.parseDouble(stationGps.split(";",2)[1]);
			double distance=gps.getDistance(lng, lat, lngToll, latToll);
			if(min>distance){
				min=distance;
				tollGps=latToll+","+lngToll;
			}
		}
		return tollGps;
	}
	public static LinkedList<String> bindMap(LinkedList<String> listData) throws ParseException{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		LinkedList<String> list=new LinkedList<>();
		if(listData.size()==1){
			return listData;
		}
		int k=0;
		for(int i=0;i<listData.size();i++){
			if(list.size()==0){
				list.add(listData.get(i));
			}else{
				String[] data1=list.get(k).split(",",3);
				String[] data2=listData.get(i).split(",",3);
				String gps1=data1[0]+","+data1[1];
				String gps2=data2[0]+","+data2[1];
				String t1="";
				String t2="";
				if(data1[2].contains(";")){
					t1=data1[2].split(";",2)[1];
				}else{
					t1=data1[2];
				}
				if(data2[2].contains(";")){
					t2=data2[2].split(";",2)[0];
				}else{
					t2=data2[2];
				}
//				long timeInterval=(sdf.parse(t2).getTime()-sdf.parse(t1).getTime())/1000/60;//��������gps��ʱ���С��3����
				String rsData="";
				if(!gps1.equals(gps2)){
					list.add(listData.get(i));
					k++;
				}else{
					String time1=data1[2];
					String time2=data2[2];
					boolean f1=time1.contains(";");
					boolean f2=time2.contains(";");
					if(!f1&&!f2){
						rsData=gps1+","+time1+";"+time2;
					}else if(!f1&&f2){
						rsData=gps1+","+time1+";"+time2.split(";",2)[1];
					}else if(f1&&!f2){
						rsData=gps1+","+time1.split(";",2)[0]+";"+time2;
					}else{
						rsData=gps1+","+time1.split(";",2)[0]+";"+time2.split(";",2)[1];
					}
					list.set(k, rsData);
				}
			}
		}
		return list;
	}
	//ֻ�����շѹ㳡GPS
	public static void getPassStation(String in,String tollGps,String outPath){
		Map<String,ArrayList<String>> mapGrid=getGrid(tollGps);
		int count=0;
		try{
			File file=new File(in);
			List<String> list=Arrays.asList(file.list());
			BufferedWriter writer=io.getWriter(outPath, "gbk");
			for(int i=0;i<list.size();i++){
				String path=in+list.get(i);
				File fileIn=new File(path);
				if(fileIn.getName().endsWith(".csv")){
					String id=list.get(i).split("\\.",2)[0];
					BufferedReader reader=io.getReader(path, "gbk");
					String line="";
					String gps="";
					List<String> timeList=new ArrayList<>();//��¼ʱ��
					LinkedList<String> stationTimeList=new LinkedList<>();
//					List<String> listStationTrace=new ArrayList<>();
					while((line=reader.readLine())!=null){
						String[] data=line.split(",",5);
						String time=data[0];
						String lat=data[1];
						String lng=data[2];
						String v=data[3];
						String d=data[4];
						double vi=Double.parseDouble(v);
						if(vi<15){
							String xy=getXY(Double.valueOf(lng),Double.valueOf(lat),50);
							ArrayList<String> isPassingToll=isPassToll(xy,mapGrid);
							if(isPassingToll!=null){
								if(gps.equals("")){
									gps=lat+","+lng;
									if(timeList.size()<2){
										timeList.add(time);
									}else{
										timeList.set(1, time);
									}
								}else if(!gps.equals(lat+","+lng)){
									String latGps=gps.split(",",2)[0];
									String lngGps=gps.split(",",2)[1];
									xy=getXY(Double.valueOf(lngGps),Double.valueOf(latGps),50);
									isPassingToll=isPassToll(xy,mapGrid);
									String squareGps=getOneStation(Double.valueOf(latGps),Double.valueOf(lngGps),isPassingToll);
									if(timeList.size()==1){
										stationTimeList.add(squareGps+","+timeList.get(0));
									}else{
										stationTimeList.add(squareGps+","+timeList.get(0)+";"+timeList.get(1));
									}
									timeList.clear();
									timeList.add(time);
									gps=lat+","+lng;
								}else{
									if(timeList.size()<2){
										timeList.add(time);
									}else{
										timeList.set(1, time);
									}
								}
//								listStationTrace.add(gps+","+time);
							}
						}
					}
					
					if(!timeList.isEmpty()){
						count++;
						String latGps=gps.split(",",2)[0];
						String lngGps=gps.split(",",2)[1];
						String xy=getXY(Double.valueOf(lngGps),Double.valueOf(latGps),50);
						ArrayList<String> isPassingToll=isPassToll(xy,mapGrid);
						String squareGps=getOneStation(Double.valueOf(latGps),Double.valueOf(lngGps),isPassingToll);
						if(timeList.size()==1){
							stationTimeList.add(squareGps+","+timeList.get(0));
						}else{
							stationTimeList.add(squareGps+","+timeList.get(0)+";"+timeList.get(1));
						}
					}
					reader.close();
					String outData="";
					if(stationTimeList.size()!=0){
						LinkedList<String> listOut=bindMap(stationTimeList);
						for(int j=0;j<listOut.size();j++){
							if(j==listOut.size()-1){
								outData+=listOut.get(j);
							}else{
								outData+=listOut.get(j)+"|";
							}
						}
						writer.write(id+":"+outData+"\n");
					}
				}
			}
			writer.flush();
			writer.close();
			System.out.println(count);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		String outPath18Chongqing="G:/��ͼ/�շ�վ����/18PoiChongqing.csv";
		getPassStation("C:/Users/pengrui/Desktop/�½��ļ��� (2)/ԭʼ�켣/",outPath18Chongqing,"C:/Users/pengrui/Desktop/�½��ļ��� (2)/�����շ�վ����/1.csv");
		
//		System.out.println();
//		double[] a=gps.transform(Double.parseDouble("28.1446523718"), Double.parseDouble("105.3039432470"));
//		double bLng=a[1];
//		double bLat=a[0];
//		System.out.println(getXY(bLng,bLat,107.2992066282886,29.253603179943614));
//		System.out.println(getXY(bLng,bLat,106.14348370038037,29.057738682735348));
	}
}
