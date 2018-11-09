package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dao.io;
import dao.map;

public class dataMatch {
	private static String cqAllPassStation="D:/货车轨迹数据分析/cqAllPassStation.csv";//每一部分经过高速的gps轨迹数据
	private static String cqTollDataOut="D:/货车轨迹数据分析/cqTollDataOut.csv";//重庆高速收费数据
	
	private static String outPath16Chongqing="G:/地图/收费站数据/16PoiChongqing.csv";//16年重庆收费站经纬度
	private static String outPath18Chongqing="G:/地图/收费站数据/18PoiChongqing.csv";//18年重庆收费站经纬度
	private static String cqStationName="G:/新建文件夹/重庆/重庆站点表.csv";
	
	private static String matchedId="D:/matchId.csv";//车牌与设备id对应表
	private static String cqGpsToStationId="D:/cqGpsToStationId.csv";//收费站编号与收费站经纬度对应表
	
	private static String cChongqingMif="G:/最新地图/road2018q2/chongqing/road/Cchongqing.mif";
	private static String cChongqingMid="G:/最新地图/road2018q2/chongqing/road/Cchongqing.mid";
	private static String allStation="G:/新建文件夹/重庆/allStation_0608.txt";
	
	/**
	 * 读取GPS轨迹数据，将id经过的连续两个有收费站编号的数据保存下来，记录进出的时间。
	 * @param cqPassStation	轨迹数据文件路径
	 * @param mapGpsStationId	已有的收费广场对应的收费站编号
	 * @param mapIdOriginTrace	保存每个id的原始经过的收费广场gps信息
	 * @param timeAquire		记录进出的时间范围，精确到小时
	 * @return					返回每个id经过的连续两个收费站id，经过的时间
	 */
	public static Map<String,Map<String,ArrayList<String>>> getIdStationIdTimeRange(String cqPassStation,Map<String,String> mapGpsStationId,Map<String,String> mapIdOriginTrace,Set<String> timeAquire){
		Map<String,Map<String,ArrayList<String>>> mapIdStationIdTimeRange=new HashMap<>();
		try{
			BufferedReader reader=io.getReader(cqPassStation, "gbk");
			String line="";
			while((line=reader.readLine())!=null){
				String[] data=line.split(":",2);
				String id=data[0];
				Map<String,ArrayList<String>> mapStationIdTimeRange=new HashMap<>();
				String squareTrace=data[1];
				String[] squares=squareTrace.split("\\|");
				mapIdOriginTrace.put(id, squareTrace);
				for(int i=0;i<squares.length-1;i++){
					String square1=squares[i];
					String square2=squares[i+1];
					String squareGps1=square1.split(",",3)[0]+","+square1.split(",",3)[1];
					String time1=square1.split(",",3)[2];
					String squareGps2=square2.split(",",3)[0]+","+square2.split(",",3)[1];
					String time2=square2.split(",",3)[2];	
					if(mapGpsStationId.containsKey(squareGps1)&&mapGpsStationId.containsKey(squareGps2)){
						timeAquire.add(time1.substring(0, 13));
						timeAquire.add(time2.substring(0, 13));
						String station1=mapGpsStationId.get(squareGps1);
						String station2=mapGpsStationId.get(squareGps2);
						if(mapStationIdTimeRange.containsKey(station1+","+station2)){
							ArrayList<String> listTimeRange=mapStationIdTimeRange.get(station1+","+station2);
							listTimeRange.add(time1+","+time2);
							mapStationIdTimeRange.put(station1+","+station2, listTimeRange);
						}else{
							ArrayList<String> listTimeRange=new ArrayList<>();
							listTimeRange.add(time1+","+time2);
							mapStationIdTimeRange.put(station1+","+station2, listTimeRange);
						}
					}
				}
				mapIdStationIdTimeRange.put(id, mapStationIdTimeRange);
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return mapIdStationIdTimeRange;
	}
	
	/**
	 * 判断gps经过的时间与收费数据经过的时间是否匹配
	 * @param inTime	收费站记录的时间
	 * @param inGpsTime	gps记录的时间
	 * @return			匹配上了返回true，否则返回false
	 * @throws ParseException
	 */
	public static boolean isMatch(String inTime,String inGpsTime,int timeInterval) throws ParseException{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long inTimeLong=sdf.parse(inTime).getTime()/1000/60;
		boolean in=false;
		if(inGpsTime.contains(";")){
			String time1=inGpsTime.split(";",2)[0];
			String time2=inGpsTime.split(";",2)[1];
			long time1Long=sdf.parse(time1).getTime()/1000/60;
			long time2Long=sdf.parse(time2).getTime()/1000/60;
			if(inTimeLong>=time1Long-timeInterval&&inTimeLong<=time2Long+timeInterval){
				in=true;
			}
		}else{
			long time=sdf.parse(inGpsTime).getTime()/1000/60;
			if(Math.abs(inTimeLong-time)<=timeInterval){
				in=true;
			}
		}
		return in;
	}
	/**
	 * 记录收费数据出入站编号、出入时间和车
	 * @param car		车牌车型
	 * @param inStation	入站编号
	 * @param outStation	出站编号
	 * @param inTime	入站时间
	 * @param outTime	出站时间
	 * @param mapStationIdDayTime	以Map<inStation+","+outStation,Map<Day,inTime+","+outTime+","+car>>
	 */
	public static void addMapStationIdDayTime(String car,String inStation,String outStation,String inTime,String outTime,Map<String,Map<String,ArrayList<String>>> mapStationIdDayTime){
		if(mapStationIdDayTime.containsKey(inStation+","+outStation)){
			Map<String,ArrayList<String>> mapDayTime=mapStationIdDayTime.get(inStation+","+outStation);
			String day=inTime.substring(0,10);
			if(mapDayTime.containsKey(day)){
				ArrayList<String> listTime=mapDayTime.get(day);
				listTime.add(inTime+","+outTime+","+car);
				mapDayTime.put(day, listTime);
			}else{
				ArrayList<String> listTime=new ArrayList<>();
				listTime.add(inTime+","+outTime+","+car);
				mapDayTime.put(day, listTime);
			}
			mapStationIdDayTime.put(inStation+","+outStation, mapDayTime);
		}else{
			Map<String,ArrayList<String>> mapDayTime=new HashMap<>();
			String day=inTime.substring(0,10);
			ArrayList<String> listTime=new ArrayList<>();
			listTime.add(inTime+","+outTime+","+car);
			mapDayTime.put(day, listTime);
			mapStationIdDayTime.put(inStation+","+outStation, mapDayTime);
		}
	}
	
	/**
	 * 读取收费数据，记录车辆进出站信息
	 * @param cqTollDataOut	收费数据
	 * @param mapCarTrace	记录每辆车按时间顺序经过的站点编号和时间
	 * @param timeAquire	时间限制
	 * @return
	 */
	public static Map<String,Map<String,ArrayList<String>>> readTrace(String cqTollDataOut,Map<String,String> mapCarTrace,Set<String> timeAquire){
		Map<String,Map<String,ArrayList<String>>> mapStationIdDayTime=new HashMap<>();
		
		try{
			BufferedReader reader=io.getReader(cqTollDataOut, "GBK");
			String line="";
			while((line=reader.readLine())!=null){
				String[] data=line.split(",",9);
				String plate=data[1];
				String cx=data[2];
				String inTime=data[3];
				String outTime=data[4];
				String inStation=data[5];
				String outStation=data[6];
				String car=plate+","+cx;
				if(cx.matches("[0-9]")&&Integer.parseInt(cx)>=2&&inTime.length()>14&&outTime.length()>14){
					String inTimeAquire=inTime.substring(0, 13);
					String outTimeAquire=outTime.substring(0, 13);
					if(timeAquire.contains(inTimeAquire)&&timeAquire.contains(outTimeAquire)){
						addMapStationIdDayTime(car,inStation,outStation,inTime,outTime,mapStationIdDayTime);
					}
					
					if(mapCarTrace.containsKey(plate+","+cx)){
						String message=mapCarTrace.get(plate+","+cx);
						message+="|"+inStation+","+inTime+"|"+outStation+","+outTime;
						mapCarTrace.put(plate+","+cx, message);
					}else{
						mapCarTrace.put(plate+","+cx,inStation+","+inTime+"|"+outStation+","+outTime);
					}
				}
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return mapStationIdDayTime;
	}
	
	/**
	 * 得到初步匹配上的结果，id匹配上多辆车
	 * @param mapIdStationIdTimeRange	保存的GPS数据中，每个id经过的连续收费站id和时间
	 * @param mapStationIdDayTime		收费数据中，每辆车经过的出入站和出入时间
	 * @param matchMap					上一个循环保存的已确定匹配上的id和车辆信息
	 * @return							返回新匹配上的id和车辆信息Map<id,ArrayList<car>>
	 * @throws ParseException
	 */
	public static Map<String,ArrayList<String>> getMapIdListCar(Map<String,Map<String,ArrayList<String>>> mapIdStationIdTimeRange,Map<String,Map<String,ArrayList<String>>> mapStationIdDayTime,Map<String,String> matchMap) throws ParseException{
		Map<String,ArrayList<String>> mapIdListCar=new HashMap<>();
		
		for(String id:mapIdStationIdTimeRange.keySet()){
			if(matchMap.containsKey(id)){
				continue;
			}
			Map<String,ArrayList<String>> mapStationIdTimeRange=mapIdStationIdTimeRange.get(id);
			for(String stationId:mapStationIdTimeRange.keySet()){
				ArrayList<String> listTimeGps=mapStationIdTimeRange.get(stationId);
				if(mapStationIdDayTime.containsKey(stationId)){
					Map<String,ArrayList<String>> mapDayTime=mapStationIdDayTime.get(stationId);
					for(int i=0;i<listTimeGps.size();i++){
						String inTimeGps=listTimeGps.get(i).split(",",2)[0];
						String outTimeGps=listTimeGps.get(i).split(",",2)[1];
						String day=inTimeGps.substring(0,10);
						if(mapDayTime.containsKey(day)){
							ArrayList<String> listTimeAndCar=mapDayTime.get(day);
							for(int j=0;j<listTimeAndCar.size();j++){
								String timeAndCar=listTimeAndCar.get(j);
								String inTime=timeAndCar.split(",",3)[0];
								String outTime=timeAndCar.split(",",3)[1];
								String car=timeAndCar.split(",",3)[2];
								boolean flag=isMatch(inTime,inTimeGps,3)&&isMatch(outTime,outTimeGps,3);
								if(flag){
									if(mapIdListCar.containsKey(id)){
										ArrayList<String> listCar=mapIdListCar.get(id);
										if(!listCar.contains(car)){
											listCar.add(car);
										}
										mapIdListCar.put(id, listCar);
									}else{
										ArrayList<String> listCar=new ArrayList<>();
										listCar.add(car);
										mapIdListCar.put(id, listCar);
									}
								}
							}
						}
					}
				}
			}
		}
		return mapIdListCar;
	}

	/**
	 * 更新收费广场对应的收费站id信息，当此信息不变时，输出匹配上的id和车
	 * @param mapIdListCar		每个id对应的多辆车
	 * @param mapIdOriginTrace	每个id的原始经过的收费广场gps和时间信息
	 * @param mapCarTrace		每辆车经过的收费站信息
	 * @param mapGpsStationId	收费广场对应的收费站id
	 * @param matchMap			匹配上的id和车辆信息
	 * @throws ParseException
	 */
	public static void updateMapGpsStationId(Map<String,ArrayList<String>> mapIdListCar,Map<String,String> mapIdOriginTrace,Map<String,String> mapCarTrace,Map<String,String> mapGpsStationId,Map<String,String> matchMap) throws ParseException{
		System.out.println("更新前："+mapGpsStationId.size());
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(String id:mapIdListCar.keySet()){
			ArrayList<String> listCar=mapIdListCar.get(id);
			String gpsTrace=mapIdOriginTrace.get(id);
			String[] gpsTraces=gpsTrace.split("\\|");
//			System.out.println(id+":"+gpsTrace);
			int maxCount=0;
			String maxCar="";
			Map<String,String> mapT=null;
			String matchData="";
			for(int i=0;i<listCar.size();i++){
				Map<String,String> mapTemp=new HashMap<>();
				String plateCx=listCar.get(i);
				String carTrace=mapCarTrace.get(plateCx);
//				System.out.println(plateCx+":"+carTrace);
				String[] carTraces=carTrace.split("\\|");
				int j=0;
				int k=0;
				int count=0;
				while(j<gpsTraces.length&&k<carTraces.length){
					String[] data=gpsTraces[j].split(",");
					String gpsStation="";
					String gpsTime="";
					gpsStation=data[0]+","+data[1];
					gpsTime=data[2];
					String carStation=carTraces[k].split(",")[0];
					String carTime=carTraces[k].split(",")[1];
					
					boolean flag=isMatch(carTime,gpsTime,3);
					if(flag){
						j++;
						k++;
						count++;
						if(!gpsStation.equals(carStation)){
//							System.out.println(gpsStation+":"+carStation);
							mapTemp.put(gpsStation, carStation);
						}
					}else{
						Date gpsDate=sdf.parse(gpsTime.contains(";")?gpsTime.split(";")[0]:gpsTime);
						Date carDate=sdf.parse(carTime);
						if(gpsDate.before(carDate)){
							j++;
						}else{
							k++;
						}
					}
				}
				if(maxCount<count){
					maxCount=count;
					maxCar=plateCx;
					mapT=mapTemp;
				}
			}
			matchMap.put(id, maxCar);
			if(mapT!=null){
				for(String key:mapT.keySet()){
					mapGpsStationId.put(key, mapT.get(key));
				}
			}
		}
		System.out.println("更新后："+mapGpsStationId.size());
	}
	
	public static void getMatch(String cqPassStation,String cqTollDataOut,String matchedId,String cqGpsToStationId) throws ParseException{
		Map<String,String> mapGpsStationId=map.matchGpsToStationId(outPath18Chongqing,outPath16Chongqing,cqStationName);
		
		Map<String,String> mapIdOriginTrace=new HashMap<>();
		Map<String,String> mapCarTrace=new HashMap<>();
		
		Set<String> timeAquire=new HashSet<>();
		
		Map<String,String> matchMap=new HashMap<>();
		int pre=0;
		while(pre!=mapGpsStationId.size()){
			pre=mapGpsStationId.size();
			Map<String,Map<String,ArrayList<String>>> mapIdStationIdTimeRange=getIdStationIdTimeRange(cqPassStation,mapGpsStationId,mapIdOriginTrace,timeAquire);
			System.out.println("read cqPassStation finish!");
			Map<String,Map<String,ArrayList<String>>> mapStationIdDayTime=readTrace(cqTollDataOut,mapCarTrace,timeAquire);
			System.out.println("read cqTollDataOut finish!");
			Map<String,ArrayList<String>> mapIdListCar=getMapIdListCar(mapIdStationIdTimeRange,mapStationIdDayTime,matchMap);
			updateMapGpsStationId(mapIdListCar,mapIdOriginTrace,mapCarTrace,mapGpsStationId,matchMap);
			System.out.println("总的id数："+mapIdOriginTrace.size());
			System.out.println("匹配上的车："+matchMap.size());
		}
		try{
			BufferedWriter writer=io.getWriter(matchedId, "gbk");
			for(String key:matchMap.keySet()){
				writer.write(key+","+matchMap.get(key)+"\n");
			}
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			BufferedWriter writer=io.getWriter(cqGpsToStationId, "gbk");
			for(String key:mapGpsStationId.keySet()){
				writer.write(mapGpsStationId.get(key)+","+key+"\n");
			}
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws ParseException{
		getMatch(cqAllPassStation,cqTollDataOut,matchedId,cqGpsToStationId);
	}
}
