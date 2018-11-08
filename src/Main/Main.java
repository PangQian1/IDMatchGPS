package Main;

import java.io.IOException;
import java.text.ParseException;

import dao.map;
import data.dataMatch;
import data.matchedData;
import data.preProcess;

public class Main {
	private static String PNamechongqingMid="D:/mapinfo_ditu/��ͼ/level2/level2/chongqing/other/PNamechongqing.mid";//2016���ͼ����pName�ļ�
	private static String POIchongqingMid="D:/mapinfo_ditu/��ͼ/level2/level2/chongqing/index/POIchongqing.mid";//2016���ͼ����poi�ļ�
	private static String CJiangSuMid="/home/pangqian/IDMatchGps/data/��ͼ/secret/road2018q2/jiangsu1/road/Cjiangsu1.mid";//2018���ͼ����mid�ļ�
	private static String CJiangSuMif="/home/pangqian/IDMatchGps/data/��ͼ/secret/road2018q2/jiangsu1/road/Cjiangsu1.mif";//2018���ͼ����mif�ļ�
	private static String outPath16Chongqing="G:/��ͼ/�շ�վ����/16PoiChongqing.csv";//16�������շ�վ��γ��
	private static String outPath18Jiangsu="/home/pangqian/IDMatchGps/data/�շ�վ��Ϣ/18PoiJiangsu.csv";//18�꽭���շ�վ��γ��
	
	private static String in="D:/�����켣���ݷ���";//GPSԭʼ�����ļ�
	private static String cqTollData="";//�����շ�����
	private static String cqTollDataOut="";//����5-18��5-24�ŵ��շ�����
	private static String cqAllPassStation="D:/�����켣���ݷ���/cqAllPassStation.csv";//���о��������gps�켣
	
	private static String matchedId="D:/matchId.csv";//�������豸id��Ӧ��
	private static String cqGpsToStationId="D:/cqGpsToStationId.csv";//�շ�վ������շ�վ��γ�ȶ�Ӧ��
	
	private static String oneIdData="D:/�����켣���ݷ���/�������ٵ�id/";
	private static String matchData="D:/matchedData.csv";
	public static void main(String[] args) throws IOException, ParseException{
		
		//map.write16PoiGps(PNamechongqingMid,POIchongqingMid,outPath16Chongqing); //����õ�����ļ�16�������շ�վ��γ��
		//map.getTollGps(CJiangSuMid,CJiangSuMif,outPath18Jiangsu); //ͨ��cmid��cmif�ļ�����õ������շ�վ�ľ�γ��
				
		preProcess.processTraceData();//�õ�ÿ���־������ٵ�GPS�켣����
//		
//		preProcess.readTollCollection(cqTollData,cqTollDataOut);//�õ�ָ������շ�����
//		dataMatch.getMatch(cqAllPassStation,cqTollDataOut,matchedId,cqGpsToStationId);//�豸id�복��ƥ��
//		
//
//		matchedData.getMatchedIdData(matchedId,oneIdData);//���켣id����켣���ݣ�ÿ�����ݰ�ʱ������
//		matchedData.reMatch(oneIdData,matchedId,cqTollDataOut,matchData);//���ƥ�������
	}
}
