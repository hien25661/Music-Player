package media.musicplayer.songs.mp3player.audio.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.utils.Constants;

public class TimerDialog extends Dialog {
	Context mContext;
	private clickListener listener;
	int positionSpinner = 0;
	SharedPreferences sharedPreferences;
	Spinner spinner;
	public TimerDialog(Context context) {
		super(context);
		this.mContext = context;
		sharedPreferences = context.getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_settimer);
		getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		final WheelView hours = (WheelView) findViewById(R.id.hour);
		NumericWheelAdapter hourAdapter = new NumericWheelAdapter(mContext, 0, 23,
				"%2d");
		hourAdapter.setItemResource(R.layout.wheel_text_item);
		hourAdapter.setItemTextResource(R.id.text);
		hours.setViewAdapter(hourAdapter);
		hours.setCyclic(true);

		final WheelView mins = (WheelView) findViewById(R.id.mins);
		final NumericWheelAdapter minAdapter = new NumericWheelAdapter(mContext, 0, 59,
				"%02d");
		minAdapter.setItemResource(R.layout.wheel_text_item);
		minAdapter.setItemTextResource(R.id.text);
		mins.setViewAdapter(minAdapter);
		mins.setCyclic(true);

		// set current time
		int mHourCurrent = sharedPreferences.getInt(Constants.HOURSET,0);
		int mMinuteCurrent = sharedPreferences.getInt(Constants.MINUTESET,1);

		/*Calendar calendar = Calendar.getInstance();
		hours.setCurrentItem(calendar.get(Calendar.HOUR));
		mins.setCurrentItem(calendar.get(Calendar.MINUTE));*/
		hours.setCurrentItem(mHourCurrent);
		mins.setCurrentItem(mMinuteCurrent);

		final TextView tvClose = (TextView)findViewById(R.id.btn_close);
		tvClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		spinner = (Spinner)findViewById(R.id.spinner_playback);
		ArrayList<String> mList = new ArrayList<>();
		mList.add("Stop Playback");
		mList.add("Start PlayBack");
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				positionSpinner = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,R.layout.spiner_item,mList);
		spinner.setAdapter(arrayAdapter);


		final TextView tvSet = (TextView)findViewById(R.id.btn_set);
		tvSet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(tvSet.getText().equals("SET")) {
					listener.clickOnSet(hours.getCurrentItem(), mins.getCurrentItem(), positionSpinner);
					sharedPreferences.edit().putInt(Constants.HOURSET,hours.getCurrentItem()).commit();
					sharedPreferences.edit().putInt(Constants.MINUTESET,mins.getCurrentItem()).commit();
				}else {
					listener.clickOnStop();
				}
			}
		});
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
		int isSetTimer = sharedPreferences.getInt("timerset",0);
		int actionTimer =  sharedPreferences.getInt("timer_action", 0);
		if(isSetTimer == 1){
			updateGUITimer(true,"--:--:--");
			spinner.setSelection(actionTimer);
		}
//		final WheelView day = (WheelView) findViewById(R.id.day);
//		day.setViewAdapter(new DayArrayAdapter(this, calendar));
//		day.setCyclic(true);
	}
	public interface clickListener{
		void clickOnSet(int hour, int minute, int action);
		void clickOnStop();
	}
	public void setClickListener(clickListener listener){
		this.listener = listener;
	}
	public void updateGUITimer(boolean isShow, String timer){
		if(isShow) {
			findViewById(R.id.mins).setVisibility(View.GONE);
			findViewById(R.id.hour).setVisibility(View.GONE);
			findViewById(R.id.liner_title).setVisibility(View.INVISIBLE);
			TextView tv_timer = (TextView) findViewById(R.id.tv_timer);
			tv_timer.setVisibility(View.VISIBLE);
			if(timer!=null) {
				tv_timer.setText(timer);
			}
			spinner.setEnabled(false);

			TextView tvSet = (TextView)findViewById(R.id.btn_set);
			tvSet.setText("STOP TIMER");
		}else {
			findViewById(R.id.mins).setVisibility(View.VISIBLE);
			findViewById(R.id.hour).setVisibility(View.VISIBLE);
			findViewById(R.id.liner_title).setVisibility(View.VISIBLE);
			findViewById(R.id.tv_timer).setVisibility(View.GONE);
			TextView tvSet = (TextView)findViewById(R.id.btn_set);
			tvSet.setText("SET");
			spinner.setEnabled(true);
		}
	}

}
