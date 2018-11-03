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
	private static String outPath18Chongqing="G:/��ͼ/�շ�վ����/18PoiChongqing.csv";
	private static String in="D:/�����켣���ݷ���";
	private static String cqAllPassStation="D:/�����켣���ݷ���/cqAllPassStation.csv";
	/**
	 * �жϾ�γ���Ƿ���һ����Χ��
	 * @param lat	ά�� wgs84
	 * @param lng	���� wgs84
	 * @return �ڷ�Χ�ڷ���true�����򷵻�false
	 */
	public static boolean isInCity(double lat,double lng){
		if(lat>=28.1446523718&&lat<=32.249984145&&lng>=105.3039432470&&lng<=110.1873857815){
			return true;
		}
		return false;
	}
	/**
	 * ���켣�����е�ʱ��ת��Ϊ��׼��ʽ
	 * @param s	�����Ժ����ʾ���ַ���
	 * @return	�����׼��ʽʱ���ַ���
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
	 * @param path ��Ӧ��ÿһ����ÿһ���·����
	 * @param setId ���ڴ��ɸѡ����λ��ָ����Χ�ڵĳ���ID
	 * @param listIn path·��������txt�켣�ļ�
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
	 * @param path ��Ӧ��ÿһ����ÿһ���ļ��е�·��
	 * @param setId ����˾���ָ����Χ����ID
	 * @param listIn ��Ӧ��path·���µ��ļ��б�
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
	 * ��ȡԭʼ�����켣���ݣ�ɸѡ��ĳ��ʡ�ڵĹ켣����
	 * @param path	�����ļ���·��
	 * @param outPath	����ļ���·��
	 */
	public static void readId(String path,String out){//��ȡԭʼ�����켣���ݣ�ɸѡ��ĳ��ʡ�ڵĹ켣����,��ÿ��idһ���ڵ����ݷ���һ���ļ���
		
		//oriDir="D:/�����켣����/��"+a+"����";
		//cqTrace="D:/�����켣���ݷ���/��"+a+"����/����ÿ�����й켣/";
		
		File file=new File(path);
		List<String> list=Arrays.asList(file.list());
		for(int i=0;i<list.size();i++){
			//���嵽ÿһ��
			String pathIn=path+"/"+list.get(i);
			File fileIn=new File(pathIn);
			List<String> listIn=Arrays.asList(fileIn.list());
			Set<String> setId=new HashSet<>();//��¼һ���ھ���ĳʡ������Id
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
		
		//cqTrace="D:/�����켣���ݷ���/��"+a+"����/����ÿ�����й켣/";
		//cqPlateTrace="D:/�����켣���ݷ���/��"+a+"����/����ÿ��ÿ���켣/";
		
//		Map<String,List<String>> mapGrid=getGrid(tollSquareGps);
		
		File file=new File(path);
		List<String> list=Arrays.asList(file.list());
		
		for(int i=0;i<list.size();i++){
			String p=path+list.get(i);
			BufferedReader reader=io.getReader(p, "gbk");
			String outDir=out+list.get(i).split("\\.")[0];//���ÿ����ÿ���·����
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
	 * ���س����ĳ��� 
	 * @param khFlag �ͻ���־λ
	 * @param vehClass ���ʹ���
	 * @return 0��С�ͳ��� 1�������Ϳͳ���    2��1�ͻ�����   3��2�ͻ����� 4��3�ͻ����� 5��4�ͻ����� 6��5�ͻ�����  
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
				a="һ";
			}else if(i==1){
				a="��";
			}else if(i==2){
				a="��";
			}else{
				a="��";
			}
			oriDir="D:/�����켣����/��"+a+"����";
			cqTrace="D:/�����켣���ݷ���/��"+a+"����/����ÿ�����й켣/";
			cqPlateTrace="D:/�����켣���ݷ���/��"+a+"����/����ÿ��ÿ���켣/";
			cqPlateAllTrace="D:/�����켣���ݷ���/��"+a+"����/����ÿ���켣��ͬ��/";
			cqPlateAllTrace1="D:/�����켣���ݷ���/��"+a+"����/����ÿ���켣������/";
			cqPassStation="D:/�����켣���ݷ���/��"+a+"����/cqPassStation.csv";
			checkExsistence(cqTrace);//����Ƿ���ڣ���������ڣ�������Ӧ·��
			checkExsistence(cqPlateTrace);
			checkExsistence(cqPlateAllTrace);
			checkExsistence(cqPlateAllTrace1);
			
			System.out.println(oriDir);
			System.out.println(cqTrace);
			System.out.println(cqPlateTrace);
			System.out.println(cqPlateAllTrace);
			System.out.println(cqPlateAllTrace1);
			
			//readId(oriDir,cqTrace);//��ȡԭʼ�����켣���ݣ�ɸѡ��ĳ��ʡ�ڵĹ켣����,������idһ���ڵ����ݷ���һ���ļ���
			readTrace(cqTrace,cqPlateTrace);//��ȡÿ������ݣ����콨���ļ��У�ÿ���ļ����£���idΪkeyֵ�����ÿ��id����һ��Ĺ켣���ݣ���id����Ϊ�ļ���
			//moveToOneDir(cqPlateTrace,cqPlateAllTrace); //��id�������ļ��У���id��һ���Ĳ�ͬ����ļ��Ƶ���ͬid�ļ�����
			//moveToOneFile(cqPlateAllTrace,cqPlateAllTrace1);//��ȡ��һ��ÿһ��id�ļ��У��ϲ�Ϊһ���ļ������
			//passStation.getPassStation(cqPlateAllTrace1,outPath18Chongqing,cqPassStation);//�õ������շ�վ�ĳ���id���������շѹ㳡��γ�ȣ�������ʱ��
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
//		readTollCollection(cqTollData,cqTollDataOut);//�õ�ָ������շ�����
//		processTraceData();//�õ�ÿ���־����շ�վ������
//		getAllCqPassStation(in,cqAllPassStation);//�ϲ����в��־����շ�վ��GPS����
	}
}
