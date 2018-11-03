package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dao.io;
import dao.passStation;
import data.gps;

public class preProcess {
	private static String outPath18Chongqing="G:/地图/收费站数据/18PoiChongqing.csv";
	private static String in="D:/货车轨迹数据分析";
	private static String cqAllPassStation="D:/货车轨迹数据分析/cqAllPassStation.csv";
	/**
	 * 判断经纬度是否在一定范围内
	 * @param lat	维度 wgs84
	 * @param lng	经度 wgs84
	 * @return 在范围内返回true，否则返回false
	 */
	public static boolean isInCity(double lat,double lng){
		if(lat>=28.1446523718&&lat<=32.249984145&&lng>=105.3039432470&&lng<=110.1873857815){
			return true;
		}
		return false;
	}
	/**
	 * 将轨迹数据中的时间转换为标准格式
	 * @param s	输入以毫秒表示的字符串
	 * @return	输出标准格式时间字符串
	 * @throws ParseException 
	 */
	public static String stampToDate(String s) throws ParseException{
		String rs;
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=sdf1.parse(s);
		rs=sdf2.format(date);
		return rs;
	}
	
	
	/**
	 * 
	 * @param path 对应于每一部分每一天的路径名
	 * @param setId 用于存放筛选出的位于指定范围内的车辆ID
	 * @param listIn path路径下所有txt轨迹文件
	 */
	public static void readIdDay(String path,Set<String> setId,List<String> listIn){
		
		for(int j=0;j<listIn.size();j++){
			String p=path+"/"+listIn.get(j);
			BufferedReader reader=io.getReader(p,"GBK");
			try{
				String line="";
				while((line=reader.readLine())!=null){
					String[] data=line.split(",",8);
					String time=stampToDate(data[0]+data[1]);
					String id=data[2];
					double lngWgs=Double.parseDouble(data[3]);
					double latWgs=Double.parseDouble(data[4]);
					String v=data[5];
					String direction=data[6];
					boolean flag=isInCity(latWgs,lngWgs);
					if(flag){
						setId.add(id);
					}
				}
				reader.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println(p+" read finish!");
		}
	}
	
	/**
	 * 
	 * @param path 对应于每一部分每一天文件夹的路径
	 * @param setId 存放了经过指定范围车辆ID
	 * @param listIn 对应于path路径下的文件列表
	 * @param writer
	 */
	public static void writeTraceDay(String path,Set<String> setId,List<String> listIn,BufferedWriter writer){
		for(int j=0;j<listIn.size();j++){
			String p=path+"/"+listIn.get(j);
			BufferedReader reader=io.getReader(p,"GBK");
			try{
				String line="";
				while((line=reader.readLine())!=null){
					String[] data=line.split(",",8);
					String time=stampToDate(data[0]+data[1]);
					String id=data[2];
					double lngWgs=Double.parseDouble(data[3]);
					double latWgs=Double.parseDouble(data[4]);
					double[] point=gps.transform(latWgs, lngWgs);
					double latGcj=point[0];
					double lngGcj=point[1];
					String v=data[5];
					String direction=data[6];
					if(setId.contains(id)){
						writer.write(id+","+time+","+latGcj+","+lngGcj+","+v+","+direction+"\n");
					}
				}
				reader.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println(p+" write finish!");
		}
	}
	/**
	 * 读取原始货车轨迹数据，筛选出某个省内的轨迹数据
	 * @param path	输入文件夹路径
	 * @param outPath	输出文件夹路径
	 */
	public static void readId(String path,String out){//读取原始货车轨迹数据，筛选出某个省内的轨迹数据,将每个id一天内的数据放在一个文件内
		
		//oriDir="D:/货车轨迹数据/第"+a+"部份";
		//cqTrace="D:/货车轨迹数据分析/第"+a+"部份/重庆每天所有轨迹/";
		
		File file=new File(path);
		List<String> list=Arrays.asList(file.list());
		for(int i=0;i<list.size();i++){
			//具体到每一天
			String pathIn=path+"/"+list.get(i);
			File fileIn=new File(pathIn);
			List<String> listIn=Arrays.asList(fileIn.list());
			Set<String> setId=new HashSet<>();//记录一天内经过某省的所有Id
			readIdDay(pathIn,setId,listIn);
			String outPath=out+list.get(i)+".csv";
			BufferedWriter writer=io.getWriter(outPath, "gbk");
			try{
				writeTraceDay(pathIn,setId,listIn,writer);
				writer.flush();
				writer.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public static void readTrace(String path,String out){
		
		//cqTrace="D:/货车轨迹数据分析/第"+a+"部份/重庆每天所有轨迹/";
		//cqPlateTrace="D:/货车轨迹数据分析/第"+a+"部份/重庆每天每条轨迹/";
		
//		Map<String,List<String>> mapGrid=getGrid(tollSquareGps);
		
		File file=new File(path);
		List<String> list=Arrays.asList(file.list());
		
		for(int i=0;i<list.size();i++){
			String p=path+list.get(i);
			BufferedReader reader=io.getReader(p, "gbk");
			String outDir=out+list.get(i).split("\\.")[0];//获得每部分每天的路径名
			File fileOutDir=new File(outDir);
			if(!fileOutDir.exists()){
				fileOutDir.mkdirs();
			}
			String line="";
			try{
				Map<String,LinkedList<String>> map=new HashMap<>();
				while((line=reader.readLine())!=null){
				
					String[] data=line.split(",");
					if(data.length==6){
						String id=data[0];
						String time=data[1];
						String lat=data[2];
						String lng=data[3];
						String v=data[4];
						String d=data[5];
						if(map.containsKey(id)){
							LinkedList<String> listTrace=map.get(id);
							listTrace.add(time+","+lat+","+lng+","+v+","+d);
							map.put(id, listTrace);
						}else{ 
							LinkedList<String> listTrace=new LinkedList<>();
							listTrace.add(time+","+lat+","+lng+","+v+","+d);
							map.put(id, listTrace);
						}
					}
				}
				reader.close();
				for(String id:map.keySet()){
					LinkedList<String> listTrace=map.get(id);
					String outPath=outDir+"/"+id+".csv";
					BufferedWriter writer=io.getWriter(outPath, "gbk");
					for(int j=0;j<listTrace.size();j++){
						writer.write(listTrace.get(j)+"\n");
					}
					writer.flush();
					writer.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			} finally {
				
			}
		}
	}
	
	public static void moveToOneDir(String in,String out){
		File file=new File(in);
		List<String> list=Arrays.asList(file.list());
		for(int i=0;i<list.size();i++){
			String path=in+list.get(i);
			File fileIn=new File(path);
			List<String> listIn=Arrays.asList(fileIn.list());
			for(int j=0;j<listIn.size();j++){
				String p=path+"/"+listIn.get(j);
				File fileCq=new File(p);
				String id=listIn.get(j).split("\\.",2)[0];
				File outDir=new File(out+id);
				if(!outDir.exists()){
					outDir.mkdirs();
				}
				File newFile=new File(outDir+"/"+list.get(i)+"-"+fileCq.getName());
				fileCq.renameTo(newFile);
			}
		}
	}
	
	public static void moveToOneFile(String in,String out){
		try{
			File file=new File(in);
			List<String> list=Arrays.asList(file.list());
			for(int i=0;i<list.size();i++){
				String path=in+list.get(i);
				File fileIn=new File(path);
				String outPath=out+list.get(i)+".csv";
				BufferedWriter writer=io.getWriter(outPath, "gbk");
				List<String> listIn=Arrays.asList(fileIn.list());
				for(int j=0;j<listIn.size();j++){
					String pathIn=path+"/"+listIn.get(j);
					BufferedReader reader=io.getReader(pathIn, "gbk");
					String line="";
					while((line=reader.readLine())!=null){
						writer.write(line+"\n");
					}
					reader.close();
				}
				writer.flush();
				writer.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 返回车辆的车型 
	 * @param khFlag 客货标志位
	 * @param vehClass 车型代码
	 * @return 0（小客车） 1（大中型客车）    2（1型货车）   3（2型货车） 4（3型货车） 5（4型货车） 6（5型货车）  
	 */
	public static int getVehicleType(String khFlag,String vehClass){
		if(khFlag.equals("0")&&Integer.valueOf(vehClass)<=1)return 0;
		if(khFlag.equals("0")&&Integer.valueOf(vehClass)>1)return 1;
		if(!khFlag.equals("0")&&Integer.valueOf(vehClass)<=1)return 2;
		if(!khFlag.equals("0")&&Integer.valueOf(vehClass)==2)return 3;
		if(!khFlag.equals("0")&&Integer.valueOf(vehClass)==3)return 4;
		if(!khFlag.equals("0")&&Integer.valueOf(vehClass)==4)return 5;
		if(!khFlag.equals("0")&&Integer.valueOf(vehClass)==5)return 6;
		return -1;
	}
	
	public static String correctTimeCq(String time) throws ParseException{
		if(time.length()<19){
			return "0";
		}else{
			String a=time.substring(0, 19);
			DateFormat df=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date=df.parse(a);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String outTime=sdf.format(date);
			return outTime;
		}
	}
	public static void readTollCollection(String in,String out) throws ParseException{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d1=sdf.parse("2018-05-17 23:59:59");
		Date d2=sdf.parse("2018-05-25 00:00:00");
		try{
			BufferedWriter writer=io.getWriter(out, "gbk");
			
			BufferedReader reader=io.getReader(in, "gbk");
			String line="";
			reader.readLine();
			while((line=reader.readLine())!=null){
				String[] data=line.split(";");
				if(data.length==51){
					String cardId=data[15].trim();
					String exStation=data[0].trim();
					String enStation=data[9].trim();
					String exTime=correctTimeCq(data[2].trim());
					String enTime=correctTimeCq(data[10].trim());
					String enPlate=data[20].trim();
					String exPlate=data[21].trim();
					String exClass=data[11].trim();
					String exFlag=data[28].trim();
					String enClass=data[18].trim();
					String enFlag=data[29].trim();
					String distance=data[36].trim();
					String weight=data[41].trim();
					if(exFlag.length()<10&&exClass.length()<10){
						String enCx=""+getVehicleType(enFlag,enClass);
						String exCx=""+getVehicleType(exFlag,exClass);
						String isEtc=data[48].trim();
						if(exPlate.length()>5){
							Date dEnTime=sdf.parse(enTime);
							Date dExTime=sdf.parse(exTime);
							if(dEnTime.after(d1)&&dEnTime.before(d2)&&dExTime.after(d1)&&dExTime.before(d2)){
								writer.write(cardId+","+exPlate+","+exCx+","+enTime+","+exTime+","+enStation+","+exStation+","+weight+","+isEtc+"\n");
							}
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
	}
	
	public static void checkExsistence(String path){
		File file=new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
	}
	public static void processTraceData(){
		String oriDir="";
		String cqTrace="";
		String cqPlateTrace="";
		String cqPlateAllTrace="";
		String cqPlateAllTrace1="";
		String cqPassStation="";
		
		for(int i=0;i<1;i++){
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
			oriDir="D:/货车轨迹数据/第"+a+"部份";
			cqTrace="D:/货车轨迹数据分析/第"+a+"部份/重庆每天所有轨迹/";
			cqPlateTrace="D:/货车轨迹数据分析/第"+a+"部份/重庆每天每条轨迹/";
			cqPlateAllTrace="D:/货车轨迹数据分析/第"+a+"部份/重庆每条轨迹不同天/";
			cqPlateAllTrace1="D:/货车轨迹数据分析/第"+a+"部份/重庆每条轨迹所有天/";
			cqPassStation="D:/货车轨迹数据分析/第"+a+"部份/cqPassStation.csv";
			checkExsistence(cqTrace);//检查是否存在，如果不存在，建立对应路径
			checkExsistence(cqPlateTrace);
			checkExsistence(cqPlateAllTrace);
			checkExsistence(cqPlateAllTrace1);
			
			System.out.println(oriDir);
			System.out.println(cqTrace);
			System.out.println(cqPlateTrace);
			System.out.println(cqPlateAllTrace);
			System.out.println(cqPlateAllTrace1);
			
			//readId(oriDir,cqTrace);//读取原始货车轨迹数据，筛选出某个省内的轨迹数据,将所有id一天内的数据放在一个文件内
			readTrace(cqTrace,cqPlateTrace);//读取每天的数据，按天建立文件夹，每个文件夹下，以id为key值，输出每个id在这一天的轨迹数据，以id名称为文件名
			//moveToOneDir(cqPlateTrace,cqPlateAllTrace); //以id名建立文件夹，将id名一样的不同天的文件移到相同id文件夹下
			//moveToOneFile(cqPlateAllTrace,cqPlateAllTrace1);//读取上一步每一个id文件夹，合并为一个文件，输出
			//passStation.getPassStation(cqPlateAllTrace1,outPath18Chongqing,cqPassStation);//得到经过收费站的车辆id，经过的收费广场经纬度，经过的时间
		}
	}
	
	public static void getAllCqPassStation(String in,String out) throws IOException{
		BufferedWriter writer=io.getWriter(out, "gbk");
		File file=new File(in);
		List<String> list=Arrays.asList(file.list());
		for(int i=0;i<list.size();i++){
			String path=in+"/"+list.get(i);
			File fileIn=new File(path);
			if(fileIn.isDirectory()){
				String pathIn=path+"/"+"cqPassStation.csv";
				try{
					BufferedReader reader=io.getReader(pathIn, "gbk");
					String line="";
					while((line=reader.readLine())!=null){
						writer.write(line+"\n");
					}
					reader.close();
				}catch(Exception e){
					e.printStackTrace();
				}
				System.out.println(pathIn);
			}
		}
		writer.flush();
		writer.close();
	}
	
	public static void main(String[] args) throws ParseException, IOException{		
//		readTollCollection(cqTollData,cqTollDataOut);//得到指定天的收费数据
//		processTraceData();//得到每部分经过收费站的数据
//		getAllCqPassStation(in,cqAllPassStation);//合并所有部分经过收费站的GPS数据
	}
}
