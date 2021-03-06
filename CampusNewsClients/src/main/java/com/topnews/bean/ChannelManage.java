package com.topnews.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.topnews.ChannelActivity;
import com.topnews.dao.ChannelDao;
import com.topnews.db.SQLHelper;

import android.app.Activity;
import android.content.Context;
import android.database.SQLException;
import android.util.Log;

public class ChannelManage {
	public static ChannelManage channelManage;
	/**
	 * 默认的用户选择频道列表
	 * */
	public static List<ChannelItem> defaultUserChannels;
	/**
	 *  默认的其他频道列表
	 * */
	public static List<ChannelItem> defaultOtherChannels;
	private ChannelDao channelDao;

	public ChannelManage(Activity activity) {
		this.activity = activity;
		defaultUserChannels = new ArrayList<ChannelItem>();
		defaultOtherChannels = new ArrayList<ChannelItem>();
		for (int i = 1; i < 5; i++) {
			defaultUserChannels.add(new ChannelItem(i, activity.getSharedPreferences("channel", Context.MODE_PRIVATE).getString(String.valueOf(i), "error"), i, 1));
		}
		for (int i = 5; i < 11; i++) {
			defaultOtherChannels.add(new ChannelItem(i, activity.getSharedPreferences("channel", Context.MODE_PRIVATE).getString(String.valueOf(i), "error"), i-5, 0));
		}
	}

	private Activity activity;
	/** 判断数据库中是否存在用户数据 */
	private boolean userExist = false;
//	static {
//		defaultUserChannels = new ArrayList<ChannelItem>();
//		defaultOtherChannels = new ArrayList<ChannelItem>();
//		defaultUserChannels.add(ChannelActivity.this.getSharedPreferences());
//		defaultUserChannels.add(new ChannelItem(1, "推荐", 1, 1));
//		defaultUserChannels.add(new ChannelItem(2, "热门", 2, 1));
//		defaultUserChannels.add(new ChannelItem(3, "社会", 3, 1));
//		defaultUserChannels.add(new ChannelItem(4, "直播", 4, 1));
//		defaultUserChannels.add(new ChannelItem(5, "娱乐", 5, 1));
//		defaultUserChannels.add(new ChannelItem(6, "科技", 6, 1));
//		defaultUserChannels.add(new ChannelItem(7, "国际", 7, 1));
//		defaultOtherChannels.add(new ChannelItem(8, "军事", 1, 0));
//		defaultOtherChannels.add(new ChannelItem(9, "视频", 2, 0));
//		defaultOtherChannels.add(new ChannelItem(10, "体育", 3, 0));
//		defaultOtherChannels.add(new ChannelItem(11, "财经", 4, 0));
//		defaultOtherChannels.add(new ChannelItem(12, "旅游", 5, 0));
//		defaultOtherChannels.add(new ChannelItem(13, "游戏", 6, 0));
//		defaultOtherChannels.add(new ChannelItem(14, "历史", 7, 0));
//		defaultOtherChannels.add(new ChannelItem(15, "故事", 8, 0));
//		defaultOtherChannels.add(new ChannelItem(16, "情感", 9, 0));
//		defaultOtherChannels.add(new ChannelItem(17, "星座", 10, 0));
//		defaultOtherChannels.add(new ChannelItem(18, "教育", 11, 0));
//		defaultUserChannels.add(new ChannelItem(19, "文学", 12, 0));
//	}

	private ChannelManage(SQLHelper paramDBHelper) throws SQLException {
		if (channelDao == null)
			channelDao = new ChannelDao(paramDBHelper.getContext());
		// NavigateItemDao(paramDBHelper.getDao(NavigateItem.class));
		return;
	}

	/**
	 * 初始化频道管理类
	 * @param paramDBHelper
	 * @throws SQLException
	 */
	public static ChannelManage getManage(SQLHelper dbHelper)throws SQLException {
		if (channelManage == null)
			channelManage = new ChannelManage(dbHelper);
		return channelManage;
	}

	/**
	 *  清除所有的频道
	 */
	public void deleteAllChannel() {
		channelDao.clearFeedTable();
	}
	/**
	 * 获取选择的频道
	 * @return 数据库存在用户配置 ? 数据库内的用户选择频道 : 默认用户选择频道 ;
	 */
	public List<ChannelItem> getUserChannel() {
		Object cacheList = channelDao.listCache(SQLHelper.SELECTED + "= ?",new String[] { "1" });
		if (cacheList != null && !((List) cacheList).isEmpty()) {
			userExist = true;
			List<Map<String, String>> mapList = (List) cacheList;
			int count = mapList.size();
			List<ChannelItem> list = new ArrayList<ChannelItem>();
			for (int i = 0; i < count; i++) {
				ChannelItem navigate = new ChannelItem();
				navigate.setId(Integer.valueOf(mapList.get(i).get(SQLHelper.ID)));
				navigate.setName(mapList.get(i).get(SQLHelper.NAME));
				navigate.setOrderId(Integer.valueOf(mapList.get(i).get(SQLHelper.ORDERID)));
				navigate.setSelected(Integer.valueOf(mapList.get(i).get(SQLHelper.SELECTED)));
				list.add(navigate);
			}
			return list;
		}
		initDefaultChannel();
		return defaultUserChannels;
	}

	/**
	 * 获取其他的频道
	 * @return 数据库存在用户配置 ? 数据库内的其它频道 : 默认其它频道 ;
	 */
	public List<ChannelItem> getOtherChannel() {
		Object cacheList = channelDao.listCache(SQLHelper.SELECTED + "= ?" ,new String[] { "0" });
		List<ChannelItem> list = new ArrayList<ChannelItem>();
		if (cacheList != null && !((List) cacheList).isEmpty()){
			List<Map<String, String>> maplist = (List) cacheList;
			int count = maplist.size();
			for (int i = 0; i < count; i++) {
				ChannelItem navigate= new ChannelItem();
				navigate.setId(Integer.valueOf(maplist.get(i).get(SQLHelper.ID)));
				navigate.setName(maplist.get(i).get(SQLHelper.NAME));
				navigate.setOrderId(Integer.valueOf(maplist.get(i).get(SQLHelper.ORDERID)));
				navigate.setSelected(Integer.valueOf(maplist.get(i).get(SQLHelper.SELECTED)));
				list.add(navigate);
			}
			return list;
		}
		if(userExist){
			return list;
		}
		cacheList = defaultOtherChannels;
		return (List<ChannelItem>) cacheList;
	}

	/**
	 * 保存用户频道到数据库
	 * @param userList
	 */
	public void saveUserChannel(List<ChannelItem> userList) {
		for (int i = 0; i < userList.size(); i++) {
			ChannelItem channelItem = (ChannelItem) userList.get(i);
			channelItem.setOrderId(i);
			channelItem.setSelected(Integer.valueOf(1));
			channelDao.addCache(channelItem);
		}
	}

	/**
	 * 保存其他频道到数据库
	 * @param otherList
	 */
	public void saveOtherChannel(List<ChannelItem> otherList) {
		for (int i = 0; i < otherList.size(); i++) {
			ChannelItem channelItem = (ChannelItem) otherList.get(i);
			channelItem.setOrderId(i);
			channelItem.setSelected(Integer.valueOf(0));
			channelDao.addCache(channelItem);
		}
	}

	/**
	 * 初始化数据库内的频道数据
	 */
	private void initDefaultChannel(){
		Log.d("deleteAll", "deleteAll");
		deleteAllChannel();
		saveUserChannel(defaultUserChannels);
		saveOtherChannel(defaultOtherChannels);
	}
}
