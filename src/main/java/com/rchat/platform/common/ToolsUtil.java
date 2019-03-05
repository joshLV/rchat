package com.rchat.platform.common;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

/**
 * 
 * @ClassName: ToolsUtil
 * @Description: 工具类
 * @author wukeyang
 * @date 2018年12月12日 
 *
 */
@SuppressWarnings("unchecked")
public class ToolsUtil {

	/**
	 * 
	 * @Title: dateToString
	 * @Description: 日期转为字符串(YYYY-MM-dd)
	 * @param @param date
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String dateToString(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String str = format.format(date);
		return str;
	}
	/**
	 * 
	 * @Title: stringToDate
	 * @Description: 日期转为字符串(YYYY-MM-dd)
	 * @param @param string
	 * @param @return 设定文件
	 * @return date 返回类型
	 * @throws
	 */
	public static Date stringToDate(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date=new Date();
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 
	 * @Title: isEmpty
	 * @Description: 对象非空判断
	 * @param @param value
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public static boolean isEmpty(Object value) {
		if (value == null)
			return true;
		if (value instanceof String)
			return ((String) value).length() == 0;
		if (value instanceof Object[])
			return ((Object[]) value).length == 0;
		if (value instanceof Collection)
			return ((Collection<? extends Object>) value).size() == 0;
		if (value instanceof Map)
			return ((Map<? extends Object, ? extends Object>) value).size() == 0;
		if (value instanceof CharSequence)
			return ((CharSequence) value).length() == 0;
		if (value instanceof Boolean)
			return false;
		if (value instanceof Number)
			return false;
		if (value instanceof Character)
			return false;
		if (value instanceof java.util.Date)
			return false;

		return false;
	}

	/**
	 * 
	 * @Title: isNotEmpty
	 * @Description: 对象非空判断
	 * @param @param o
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}

	/**
	 * 日期转换
	 * 
	 * @Title: formatTime
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param format
	 * @param @param v
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String formatTime(String format, Object v) {
		if (v == null) {
			return null;
		}
		if (v.equals("")) {
			return "";
		}
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(v);
	}

	/**
	 * 
	 * @Title: resizeImage
	 * @Description: 图片缩略方法
	 * @param @param srcFile
	 * @param @param width
	 * @param @param height
	 * @param @return
	 * @param @throws IOException 设定文件
	 * @return BufferedImage 返回类型
	 * @throws
	 */
	public static BufferedImage resizeImage(File srcFile, int width, int height)
			throws IOException {
		Image srcImg = ImageIO.read(srcFile);
		BufferedImage buffImg = null;
		buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		buffImg.getGraphics().drawImage(
				srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,
				0, null);
		return buffImg;
	}

	/**
	 * 
	 * @Title: returnDateStartOrEnd
	 * @Description: 返回当天开始时间或结束时间
	 * @param @param hour
	 * @param @param minute
	 * @param @param second
	 * @param @return 设定文件
	 * @return Date 返回类型
	 * @throws
	 */
	public static Date returnDateStartOrEnd(int hour, int minute, int second) {
		Calendar currentDate = new GregorianCalendar();
		currentDate.set(Calendar.HOUR_OF_DAY, hour);
		currentDate.set(Calendar.MINUTE, minute);
		currentDate.set(Calendar.SECOND, second);
		return currentDate.getTime();
	}
	
	

}
