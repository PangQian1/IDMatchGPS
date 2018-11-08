package Main;

import java.io.IOException;
import java.text.ParseException;

import dao.map;
import data.dataMatch;
import data.matchedData;
import data.preProcess;

public class Main {
	private static String PNamechongqingMid="D:/mapinfo_ditu/地图/level2/level2/chongqing/other/PNamechongqing.mid";//2016年地图重庆pName文件
	private static String POIchongqingMid="D:/mapinfo_ditu/地图/level2/level2/chongqing/index/POIchongqing.mid";//2016年地图重庆poi文件
	private static String CJiangSuMid="/home/pangqian/IDMatchGps/data/地图/secret/road2018q2/jiangsu1/road/Cjiangsu1.mid";//2018年地图江苏mid文件
	private static String CJiangSuMif="/home/pangqian/IDMatchGps/data/地图/secret/road2018q2/jiangsu1/road/Cjiangsu1.mif";//2018年地图江苏mif文件
	private static String outPath16Chongqing="G:/地图/收费站数据/16PoiChongqing.csv";//16年重庆收费站经纬度
	private static String outPath18Jiangsu="/home/pangqian/IDMatchGps/data/收费站信息/18PoiJiangsu.csv";//18年江苏收费站经纬度
	
	private static String in="D:/货车轨迹数据分析";//GPS原始数据文件
	private static String cqTollData="";//重庆收费数据
	private static String cqTollDataOut="";//重庆5-18到5-24号的收费数据
	private static String cqAllPassStation="D:/货车轨迹数据分析/cqAllPassStation.csv";//所有经过重庆的gps轨迹
	
	private static String matchedId="D:/matchId.csv";//车牌与设备id对应表
	private static String cqGpsToStationId="D:/cqGpsToStationId.csv";//收费站编号与收费站经纬度对应表
	
	private static String oneIdData="D:/货车轨迹数据分析/经过高速的id/";
	private static String matchData="D:/matchedData.csv";
	public static void main(String[] args) throws IOException, ParseException{
		
		//map.write16PoiGps(PNamechongqingMid,POIchongqingMid,outPath16Chongqing); //计算得到输出文件16年重庆收费站经纬度
		//map.getTollGps(CJiangSuMid,CJiangSuMif,outPath18Jiangsu); //通过cmid和cmif文件处理得到所有收费站的经纬度
				
		preProcess.processTraceData();//得到每部分经过高速的GPS轨迹数据
//		
//		preProcess.readTollCollection(cqTollData,cqTollDataOut);//得到指定天的收费数据
//		dataMatch.getMatch(cqAllPassStation,cqTollDataOut,matchedId,cqGpsToStationId);//设备id与车牌匹配
//		
//
//		matchedData.getMatchedIdData(matchedId,oneIdData);//按轨迹id输出轨迹数据，每条数据按时间排序
//		matchedData.reMatch(oneIdData,matchedId,cqTollDataOut,matchData);//输出匹配的数据
	}
}
