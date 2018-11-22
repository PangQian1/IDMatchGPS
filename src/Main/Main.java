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
	private static String outPath16Chongqing="G:/��ͼ/�շ�վ����/16PoiChongqing.csv";//16�������շ�վ��γ��
	
	private static String CJiangSuMid1="/home/pangqian/IDMatchGps/data/��ͼ/secret/road2018q2/jiangsu1/road/Cjiangsu1.mid";//2018���ͼ����1mid�ļ�
	private static String CJiangSuMif1="/home/pangqian/IDMatchGps/data/��ͼ/secret/road2018q2/jiangsu1/road/Cjiangsu1.mif";//2018���ͼ����1mif�ļ�
	private static String CJiangSuMid2="/home/pangqian/IDMatchGps/data/��ͼ/secret/road2018q2/jiangsu2/road/Cjiangsu2.mid";//2018���ͼ����2mid�ļ�
	private static String CJiangSuMif2="/home/pangqian/IDMatchGps/data/��ͼ/secret/road2018q2/jiangsu2/road/Cjiangsu2.mif";//2018���ͼ����2mif�ļ�
	private static String outPath18Jiangsu1="/home/pangqian/IDMatchGps/data/�շ�վ��Ϣ/18PoiJiangsu1.csv";//18�꽭��1�շ�վ��γ��
	private static String outPath18Jiangsu2="/home/pangqian/IDMatchGps/data/�շ�վ��Ϣ/18PoiJiangsu2.csv";//18�꽭��2�շ�վ��γ��
	private static String outPath18Jiangsu="/home/pangqian/IDMatchGps/data/�շ�վ��Ϣ/18PoiJiangsu.csv";//18�꽭��2�շ�վ��γ��
	
	private static String in="D:/�����켣���ݷ���";//GPSԭʼ�����ļ�
	private static String AllTollData="/home/pangqian/IDMatchGps/data/�շ�վ��Ϣ/�շ�����";//ȫ��5-18��5-24�ŵ��շ�����
	/*private static String jsTollData="/home/pangqian/IDMatchGps/data/�շ�վ��Ϣ/jsTollData.csv";//����5-18��5-24�ŵ��շ�����
	//private static String jsTollDataOut="";//����5-18��5-24�ŵ��շ�����
	private static String cqAllPassStation="/home/pangqian/IDMatchGps/data/�����켣���ݷ���/cqAllPassStation.csv";//���о������յ�gps�켣
	
	private static String matchedId="/home/pangqian/IDMatchGps/data/matchId.csv";//�������豸id��Ӧ��
	private static String cqGpsToStationId="/home/pangqian/IDMatchGps/data/cqGpsToStationId.csv";//�շ�վ������շ�վ��γ�ȶ�Ӧ��
	
	private static String oneIdData="/home/pangqian/IDMatchGps/data/�����켣���ݷ���/�������ٵ�id/";
	private static String matchData="/home/pangqian/IDMatchGps/data/matchedData.csv";*/
	
	private static String jsTollData="H:/�շ�վ��Ϣ/jsTollData.csv";//����5-18��5-24�ŵ��շ�����
    private static String cqAllPassStation="D:/�����켣���ݷ���/cqAllPassStation.csv";//���о������յ�gps�켣
	
	private static String matchedId="D:/matchId.csv";//�������豸id��Ӧ��
	private static String cqGpsToStationId="D:/cqGpsToStationId.csv";//�շ�վ������շ�վ��γ�ȶ�Ӧ��
	
	private static String oneIdData="D:/�����켣���ݷ���/�������ٵ�id/";
	private static String matchData="D:/matchedData.csv";
	
	public static void main(String[] args) throws IOException, ParseException{
		
		map.write16PoiGps(PNamechongqingMid,POIchongqingMid,outPath16Chongqing); //����õ�����ļ�16�������շ�վ��γ��

		//map.getTollGps(CJiangSuMid1,CJiangSuMif1,outPath18Jiangsu1); //ͨ��cmid��cmif�ļ�����õ�����1�շ�վ�ľ�γ��
		map.getTollGps(CJiangSuMid2,CJiangSuMif2,outPath18Jiangsu2); //ͨ��cmid��cmif�ļ�����õ�����2�շ�վ�ľ�γ��
		
		//map.getTollGps2(CJiangSuMid1, CJiangSuMif1, CJiangSuMid2, CJiangSuMif2, outPath18Jiangsu);
				
		//preProcess.processTraceData();//�õ�ÿ���־������ٵ�GPS�켣����
		
		//preProcess.readTollCollection(jsTollData,jsTollDataOut);//�õ�ָ������շ�����
		//preProcess.filterTollCollection(AllTollData, jsTollData);
		
		dataMatch.getMatch(cqAllPassStation,jsTollData,matchedId,cqGpsToStationId);//�豸id�복��ƥ��

		matchedData.getMatchedIdData(matchedId,oneIdData);//���켣id����켣���ݣ�ÿ�����ݰ�ʱ������
		//matchedData.reMatch(oneIdData,matchedId,jsTollData,matchData);//���ƥ�������
	}
}
