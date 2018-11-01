package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dao.serviceArea;
import dao.io;
import dao.passStation;

public class matchedData {
	private static String matchedId="D:/matchId.csv";
	private static String cqPassStation="D:/货车轨迹数据分析/cqAllPassStation.csv";
	private static String cqTollDataOut="D:/货车轨迹数据分析/cqTollDataOut.csv";
	
	private static String matchedData="D:/matchedData.csv";
	
	private static String oneIdData="D:/货车轨迹数据分析/经过高速的id/";
	public static Map<String,String> readMatchedId(String matchedId){
		Map<String,String> mapMatchedId=new HashMap<>();
		try{
			BufferedReader reader=io.getReader(matchedId, "gbk");
			String line="";
			while((line=reader.readLine())!=null){
				String[] data=line.split(",",3);
				String id=data[0];
				String plateCx=data[1]+","+data[2];
				mapMatchedId.put(id, plateCx);
				
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return mapMatchedId;
	}
	public static void mergeTwoFile(File fileIn,File fileOut){
		ArrayList<String> list=new ArrayList<>();
		try{
			BufferedReader reader=io.getReader(fileIn.getPath(), "gbk");
			String line="";
			while((line=reader.readLine())!=null){
				list.add(line);
			}
			reader.close();
			
			reader=io.getReader(fileOut.getPath(), "gbk");
			line="";
			while((line=reader.readLine())!=null){
				list.add(line);
			}
			reader.close();
			
			BufferedWriter writer=io.getWriter(fileOut.getPath(), "gbk");
			Collections.sort(list, new Comparator<String>(){

				@Override
				public int compare(String arg0, String arg1) {
					// TODO Auto-generated method stub
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date time0 = null;
					Date time1 = null;
					try {
						time0 = sdf.parse(arg0.split(",")[0]);
						time1=sdf.parse(arg1.split(",")[0]);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(time0.getTime()<time1.getTime()){
						return -1;
					}else if(time0.getTime()>time1.getTime()){
						return 1;
					}
					return 0;
				}
			});
			for(int i=0;i<list.size();i++){
				writer.write(list.get(i)+"\n");
			}
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void getMatchedIdData(String matchedId,String oneIdData) throws IOException{
		Map<String,String> mapMatchedId=readMatchedId(matchedId);
		for(int i=0;i<4;i++){
			String a="";
			if(i==0){
				a="一";
			}else if(i==1){
				a="二";
			}else if(i==2){
				a="三";
			}else{
				a="四";
			}
			String cqPlateAllTrace="D:/货车轨迹数据分析/第"+a+"部份/重庆每条轨迹所有天/";
			File file=new File(cqPlateAllTrace);
			List<String> listFile=Arrays.asList(file.list());
			for(int k=0;k<listFile.size();k++){
				String fileName=listFile.get(k);
				String path=cqPlateAllTrace+fileName;
				String id=fileName.split("\\.")[0];
				if(mapMatchedId.containsKey(id)){
					File fileIn=new File(path);
					String outPath=oneIdData+fileName;
					File fileOut=new File(outPath);
					if(!fileOut.exists()){
						Files.copy(fileIn.toPath(), fileOut.toPath());
					}else{
						mergeTwoFile(fileIn,fileOut);
					}
				}
			}
		}
	}
	
	public static Map<String,String> readCqPassStation(String cqPassStation,Map<String,String> mapMatchedId){
		Map<String,String> mapIdOriginTrace=new HashMap<>();
		try{
			BufferedReader reader=io.getReader(cqPassStation, "gbk");
			String line="";
			while((line=reader.readLine())!=null){
				String[] data=line.split(":",2);
				String id=data[0];
				String squareTrace=data[1];
				if(mapMatchedId.containsKey(id)){
					mapIdOriginTrace.put(id, squareTrace);
				}
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return mapIdOriginTrace;
	}
	
	public static Map<String,String> readTrace(String cqTollDataOut,Map<String,String> mapMatchedId){
		Map<String,String> mapCarTrace=new HashMap<>();
		Set<String> carSet=new HashSet<>();
		for(String car:mapMatchedId.values()){
			carSet.add(car);
		}
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
				String weight=data[7];
				if(carSet.contains(car)&&cx.matches("[0-9]")&&Integer.parseInt(cx)>=2&&inTime.length()>14&&outTime.length()>14){
					if(mapCarTrace.containsKey(car)){
						String message=mapCarTrace.get(plate+","+cx);
						message+="|"+inStation+","+inTime+","+weight+"|"+outStation+","+outTime+","+weight;
						mapCarTrace.put(car, message);
					}else{
						mapCarTrace.put(car,inStation+","+inTime+","+weight+"|"+outStation+","+outTime+","+weight);
					}
				}
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return mapCarTrace;
	}
	
	public static void reMatch(String oneIdData,String matchedId,String cqTollDataOut,String out) throws ParseException, IOException{
		Map<String,String> mapMatchedId=readMatchedId(matchedId);
		Map<String,String> mapCarTrace=readTrace(cqTollDataOut,mapMatchedId);
		
		Map<String,String> mapMatchedData=new HashMap<>();
		Map<String,ArrayList<String>> mapServiceXy=serviceArea.getCqServiceArea(serviceArea.serviceArea, serviceArea.cqServiceArea);
		BufferedWriter writer=io.getWriter(out, "gbk");
		File file=new File(oneIdData);
		List<String> listFile=Arrays.asList(file.list());
		for(int k=0;k<listFile.size();k++){
			String fileName=listFile.get(k);
			String path=oneIdData+fileName;
			String id=fileName.split("\\.")[0];
			if(mapMatchedId.containsKey(id)){
				processOneId(id,path,mapMatchedId,mapCarTrace,mapServiceXy,mapMatchedData);
			}
		}
		
		for(String key:mapMatchedData.keySet()){
			writer.write(key+";"+mapMatchedData.get(key)+"\n");
		}
		writer.flush();
		writer.close();
		System.out.println(mapMatchedId.size());
	}
	
	public static void processOneId(String id,String path,Map<String,String> mapMatchedId,Map<String,String> mapCarTrace,Map<String,ArrayList<String>> mapServiceXy,Map<String,String> mapMatchedData) throws ParseException{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuilder outData=new StringBuilder();
		String car=mapMatchedId.get(id);
		String carTrace=mapCarTrace.get(car);
		String[] carTraces=carTrace.split("\\|");
		ArrayList<String> listGps=new ArrayList<>();
		try{
			BufferedReader reader=io.getReader(path, "gbk");
			String line="";
			while((line=reader.readLine())!=null){
				String[] data=line.split(",");
				String gpsTime=data[0];
				String lat=data[1];
				String lng=data[2];
				listGps.add(gpsTime+","+lat+","+lng);
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		int b=0;
		int c=0;
		String serviceId="";
		int matchedCount=0;
		while(b<carTraces.length&&c<listGps.size()){
			String gpsTime=listGps.get(c).split(",")[0];
			String lat=listGps.get(c).split(",")[1];
			String lng=listGps.get(c).split(",")[2];
			String xy=passStation.getXY(Double.valueOf(lng), Double.valueOf(lat), 100);
			ArrayList<String> nearService=passStation.isPassToll(xy, mapServiceXy);
			if(nearService!=null){
				serviceId=serviceArea.getOneServiceArea(Double.valueOf(lat), Double.valueOf(lng), nearService);
				serviceId=serviceId+","+gpsTime;
			}
			
			String carStation=carTraces[b].split(",")[0];
			String carTime=carTraces[b].split(",")[1];
			String weight=carTraces[b].split(",")[2];
			boolean flag=dataMatch.isMatch(carTime, gpsTime, 1);
			if(flag){
				matchedCount++;
				int t=c;
				if(b%2==0){
					outData.append(carStation).append(",").append(gpsTime).append(",");
				}else{
					boolean flagT=true;
					while(t+1<listGps.size()&&flagT){
						gpsTime=listGps.get(t+1).split(",")[0];
						lat=listGps.get(t+1).split(",")[1];
						lng=listGps.get(t+1).split(",")[2];
						xy=passStation.getXY(Double.valueOf(lng), Double.valueOf(lat), 100);
						nearService=passStation.isPassToll(xy, mapServiceXy);
						if(nearService!=null){
							serviceId=serviceArea.getOneServiceArea(Double.valueOf(lat), Double.valueOf(lng), nearService);
							serviceId=serviceId+","+gpsTime;
						}
						flagT=dataMatch.isMatch(carTime, gpsTime, 1);
						t++;
					}
					if(serviceId.equals("")){
						outData.append(carStation).append(",").append(gpsTime).append(",").append(weight).append("|");
					}else{
						outData.append(carStation).append(",").append(gpsTime).append(",").append(weight).append(",").append(serviceId).append("|");
						serviceId="";
					}
				}
				b++;
				c=t;
			}else{
				Date gpsDate=sdf.parse(gpsTime);
				Date carDate=sdf.parse(carTime);
				if(gpsDate.before(carDate)){
					c++;
				}else{
					if(b%2==0){
						outData.append(carStation).append(",").append(gpsTime).append(",");
					}else{
						if(serviceId.equals("")){
							outData.append(carStation).append(",").append(gpsTime).append(",").append(weight).append("|");
						}else{
							outData.append(carStation).append(",").append(gpsTime).append(",").append(weight).append(",").append(serviceId).append("|");
							serviceId="";
						}
					}
					b++;
				}
			}
		}
		if(matchedCount>=1){
			String message=outData.toString();
			mapMatchedData.put(id+","+car,message.substring(0, message.length()-1));
		}
	}
	public static void main(String[] args) throws ParseException, IOException{
		getMatchedIdData(matchedId,oneIdData);
		
		reMatch(oneIdData,matchedId,cqTollDataOut,matchedData);
//		Map<String,String> mapMatchedId=readMatchedId(matchedId);
//		Map<String,String> mapCarTrace=readTrace(cqTollDataOut,mapMatchedId);
//		System.out.println(mapCarTrace.get("渝C85200,6"));
	}
}
