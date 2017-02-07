package com.lefu.es.wheelview;

import java.util.Arrays;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lefu.es.application.IwellnessApplication;
import com.lefu.es.util.otherUtil;
import com.lefu.iwellness.newes.cn.system.R;

public class WheelMain {
	private View view;
	private WheelView wv_year;
	private WheelView wv_month;
	private WheelView wv_day;
	private WheelView wv_hours;
	private WheelView wv_mins;
	public int screenheight;

	private boolean isLinter;
	private static int START_YEAR = 1915, END_YEAR = 2049;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public static int getSTART_YEAR() {
		return START_YEAR;
	}

	public static void setSTART_YEAR(int sTART_YEAR) {
		START_YEAR = sTART_YEAR;
	}

	public static int getEND_YEAR() {
		return END_YEAR;
	}

	public static void setEND_YEAR(int eND_YEAR) {
		END_YEAR = eND_YEAR;
	}

	public WheelMain(View view) {
		super();
		this.view = view;
		setView(view);
		viewInit();
	}

	public void viewInit() {
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_month = (WheelView) view.findViewById(R.id.month);
		wv_day = (WheelView) view.findViewById(R.id.day);
		wv_hours = (WheelView) view.findViewById(R.id.hours);
		wv_mins = (WheelView) view.findViewById(R.id.min);

	}

	/**
	 * 时分选择器
	 * 
	 */
	public void showHours(int hours, int min) {
		wv_year.setVisibility(View.GONE);
		wv_month.setVisibility(View.GONE);
		wv_day.setVisibility(View.GONE);
		wv_hours.setVisibility(View.VISIBLE);
		wv_mins.setVisibility(View.VISIBLE);
		// 时

		wv_hours.setAdapter(new NumericWheelAdapter(0, 23));// 设置"年"的显示数据
		wv_hours.setCyclic(true);// 可循环滚动
		wv_hours.setLabel(IwellnessApplication.app.getResources().getString(R.string.hour));// 添加文字
		wv_hours.setCurrentItem(hours);// 初始化时显示的数据

		// 分

		wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
		wv_mins.setCyclic(true);
		wv_mins.setLabel(IwellnessApplication.app.getResources().getString(R.string.mins));
		wv_mins.setCurrentItem(min);

		int textSize = 0;
		textSize = (screenheight / 100) * 4;
		wv_hours.TEXT_SIZE = textSize;
		wv_mins.TEXT_SIZE = textSize;
	}

	/***
	 * 
	 * 弹出农历日期时间选择器
	 * 
	 */
	public void showlunarTimePicker() {

		wv_year.setVisibility(View.VISIBLE);
		wv_month.setVisibility(View.VISIBLE);
		wv_day.setVisibility(View.VISIBLE);
		wv_hours.setVisibility(View.GONE);
		wv_mins.setVisibility(View.GONE);
		String year[] = otherUtil.getYera();

		final String monthOfAlmanac[] = {"正月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "冬月", "腊月"};
		final String daysOfAlmanac[] = {"初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"}; // 农历的天数

		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setVisibleItems(5);
		wv_year.setLabel("");
		wv_year.setAdapter(new ArrayWheelAdapter<String>(year, 10));// 设置"年"的显示数据

		// 月

		wv_month.setCyclic(true);
		wv_month.setVisibleItems(5);
		wv_month.setLabel("");
		wv_month.setAdapter(new ArrayWheelAdapter<String>(monthOfAlmanac, 5));

		// 日

		wv_day.setCyclic(true);
		wv_day.setVisibleItems(5);
		wv_day.setLabel("");
		wv_day.setAdapter(new ArrayWheelAdapter<String>(daysOfAlmanac, 5));

	}

	public void setTime(final int month, final int day, final int year) {

		int textSize = 0;
		textSize = (screenheight / 100) * 2;
		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;

		isLinter = true;
		initDateTimePicker(month, day, year);

		// showlunarTimePicker();
		final Button btn_solar = (Button) view.findViewById(R.id.ig_solar);
		final Button btn_lunar = (Button) view.findViewById(R.id.ig_lunar);

		// 公历按钮监听
		btn_solar.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				isLinter = true;
				btn_solar.setBackgroundResource(R.drawable.orgbtn);
				// btn_solar.setTextColor(R.color.white);
				btn_lunar.setBackgroundResource(R.drawable.whtbtn);
				// btn_lunar.setTextColor(R.color.orange);
				initDateTimePicker(month, day, year);
			}

		});
		// 农历按钮监听
		btn_lunar.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				isLinter = false;
				btn_lunar.setBackgroundResource(R.drawable.orgbtn);

				btn_solar.setBackgroundResource(R.drawable.whtbtn);
				showlunarTimePicker();
			}
		});
	}

	/**
	 * @Description: TODO 弹出阳历日期时间选择器
	 */
	public void initDateTimePicker(int month, int day, int year) {

		wv_year.setVisibility(View.VISIBLE);
		wv_month.setVisibility(View.VISIBLE);
		wv_day.setVisibility(View.VISIBLE);
		wv_hours.setVisibility(View.GONE);
		wv_mins.setVisibility(View.GONE);

		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
		String[] months_little = {"4", "6", "9", "11"};

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// 年
		// wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setLabel(IwellnessApplication.app.getResources().getString(R.string.yeat_date));// 添加文字
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据
		wv_year.setVisibleItems(5);
		// 月
		// wv_month = (WheelView) view.findViewById(R.id.month);
		wv_month.setLabel(IwellnessApplication.app.getResources().getString(R.string.month_date));
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		wv_month.setCurrentItem(month);
		wv_month.setVisibleItems(5);

		// 日
		// wv_day = (WheelView) view.findViewById(R.id.day);
		wv_day.setCyclic(true);
		wv_day.setLabel(IwellnessApplication.app.getResources().getString(R.string.day_date));
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (isLinter) {
			if (list_big.contains(String.valueOf(month + 1))) {
				wv_day.setAdapter(new NumericWheelAdapter(1, 31));
			} else if (list_little.contains(String.valueOf(month + 1))) {
				wv_day.setAdapter(new NumericWheelAdapter(1, 30));
			} else {
				// 闰年
				if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
					wv_day.setAdapter(new NumericWheelAdapter(1, 29));
				else
					wv_day.setAdapter(new NumericWheelAdapter(1, 28));
			}
		}
		wv_day.setCurrentItem(day - 1);
		wv_day.setVisibleItems(5);

		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (isLinter) {
					int year_num = newValue + START_YEAR;
					// 判断大小月及是否闰年,用来确定"日"的数据
					if (list_big.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 31));
					} else if (list_little.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 30));
					} else {
						if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0)
							wv_day.setAdapter(new NumericWheelAdapter(1, 29));
						else
							wv_day.setAdapter(new NumericWheelAdapter(1, 28));
					}
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (isLinter) {
					int month_num = newValue + 1;
					// 判断大小月及是否闰年,用来确定"日"的数据
					if (list_big.contains(String.valueOf(month_num))) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 31));
					} else if (list_little.contains(String.valueOf(month_num))) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 30));
					} else {
						if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year.getCurrentItem() + START_YEAR) % 100 != 0) || (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
							wv_day.setAdapter(new NumericWheelAdapter(1, 29));
						else
							wv_day.setAdapter(new NumericWheelAdapter(1, 28));
					}
				}
			}
		};

		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);
	}

	public String getTime() {
		StringBuffer sb = new StringBuffer();
		sb.append((wv_month.getCurrentItem() + 1)).append("-").append((wv_day.getCurrentItem() + 1)).append("-").append((wv_year.getCurrentItem() + START_YEAR));
		return sb.toString();
	}

	public int getYear() {

		return (wv_year.getCurrentItem() + START_YEAR);
	}

	public int getMonth() {
		return (wv_month.getCurrentItem() + 1);
	}

	public int getDay() {
		return (wv_day.getCurrentItem() + 1);
	}

	public int getHours() {
		return (wv_hours.getCurrentItem());
	}

	public int getMin() {
		return (wv_mins.getCurrentItem());
	}
}
