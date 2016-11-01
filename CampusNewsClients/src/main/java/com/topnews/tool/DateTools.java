package com.topnews.tool;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
/*
 * ʱ�乤����
 */
public class DateTools {
	/*
	 * ��ʱ���תΪ�ַ��� ����ʽ��yyyy-MM-dd HH:mm
	 */
	public static String getStrTime_ymd_hm(String cc_time) {
		String re_StrTime = "";
		if(TextUtils.isEmpty(cc_time) || "null".equals(cc_time)){
			return re_StrTime;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		// ���磺cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		return re_StrTime;

	}

	/*
	 * ��ʱ���תΪ�ַ��� ����ʽ��yyyy-MM-dd HH:mm:ss
	 */
	public static String getStrTime_ymd_hms(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// ���磺cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		return re_StrTime;

	}

	/*
	 * ��ʱ���תΪ�ַ��� ����ʽ��yyyy.MM.dd
	 */
	public static String getStrTime_ymd(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		// ���磺cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		return re_StrTime;
	}

	/*
	 * ��ʱ���תΪ�ַ��� ����ʽ��yyyy
	 */
	public static String getStrTime_y(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		// ���磺cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		return re_StrTime;
	}

	/*
	 * ��ʱ���תΪ�ַ��� ����ʽ��MM-dd
	 */
	public static String getStrTime_md(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		// ���磺cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		return re_StrTime;
	}

	/*
	 * ��ʱ���תΪ�ַ��� ����ʽ��HH:mm
	 */
	public static String getStrTime_hm(long cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		// ���磺cc_time=1291778220
//		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(cc_time ));
		return re_StrTime;
	}

	/*
	 * ����ˢ��ʱ��
	 */
	public static String refreshTime(long cc_time) {
		String re_StrTime = null;
		if (cc_time < 60000) {
			return "��ǰ";
		} else if (cc_time >= 6000 && cc_time <360000) {
			SimpleDateFormat sdf = new SimpleDateFormat("mm");
			// ���磺cc_time=1291778220
//		long lcc_time = Long.valueOf(cc_time);
			re_StrTime = sdf.format(new Date(cc_time ));
			return re_StrTime +"����ǰ";
		} else if (cc_time >= 360000 && cc_time < 8640000 ) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH");
			// ���磺cc_time=1291778220
//		long lcc_time = Long.valueOf(cc_time);
			re_StrTime = sdf.format(new Date(cc_time ));
			return re_StrTime +"Сʱǰ";
		} else if (cc_time >= 8640000 ) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd");
			// ���磺cc_time=1291778220
//		long lcc_time = Long.valueOf(cc_time);
			re_StrTime = sdf.format(new Date(cc_time ));
			return re_StrTime +"��ǰ";
		}
		return "ʱ�����";
	}

	/*
	 * ��ʱ���תΪ�ַ��� ����ʽ��HH:mm:ss
	 */
	public static String getStrTime_hms(long cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		// ���磺cc_time=1291778220
//		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(cc_time));
		return re_StrTime;
	}

	/*
	 * ��ʱ���תΪ�ַ��� ����ʽ��MM-dd HH:mm:ss
	 */
	public static String getNewsDetailsDate(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
		// ���磺cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		return re_StrTime;
	}

	/* 
	 * ���ַ���תΪʱ���
	 */
	public static String getTime() {
		String re_time = null;
		long currentTime = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		Date d;
		d = new Date(currentTime);
		long l = d.getTime();
		String str = String.valueOf(l);
		re_time = str.substring(0, 10);
		return re_time;
	}
	
	/*
	 * ��ʱ���תΪ�ַ��� ����ʽ��yyyy.MM.dd  ���ڼ�
	 */
	public static String getSection(long cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  EEEE");
//		���ڴ���SimpleDateFormat����Ĳ�����EEEE�������ڣ��硰�����ġ���MMMM���������·ݣ��硰ʮһ�¡���MM�����·ݣ��硰11����
//		yyyy������ݣ��硰2010����dd�����죬�硰25��
		// ���磺cc_time=1291778220
//		long lcc_time = Long.valueOf(cc_time);
//		re_StrTime = sdf.format(new Date(cc_time * 1000L));
		re_StrTime = sdf.format(new Date(cc_time ));
		return re_StrTime;
	}
	
//	public static String getTodayDate(){
//		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
//		String nowTime=format.format(new Date());
//		return 
//	}
}
