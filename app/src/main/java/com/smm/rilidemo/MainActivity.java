package com.smm.rilidemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.smm.rilidemo.customview.agenda.AgendaAdapter;
import com.smm.rilidemo.customview.agenda.AgendaView;
import com.smm.rilidemo.customview.calendar.CalendarView;
import com.smm.rilidemo.customview.render.DefaultEventRenderer;
import com.smm.rilidemo.customview.render.EventRenderer;
import com.smm.rilidemo.models.CalendarEvent;
import com.smm.rilidemo.models.DayItem;
import com.smm.rilidemo.utils.BusProvider;
import com.smm.rilidemo.utils.CalendarManager;
import com.smm.rilidemo.utils.Events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class MainActivity extends AppCompatActivity implements StickyListHeadersListView.OnStickyHeaderChangedListener {


    CalendarView calendarView;

    AgendaView agendaView;


    private int mCalendarHeaderColor, mCalendarDayTextColor, mCalendarPastDayTextColor, mCalendarCurrentDayColor;

    private int mAgendaCurrentDayTextColor ;
    public AgendaAdapter agendaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this);

        calendarView = (CalendarView)findViewById(R.id.calendar_view);
        agendaView = (AgendaView)findViewById(R.id.agenda_view);

//        mAgendaCurrentDayTextColor = getResources().getColor(R.color.calendar_agendaHead_current_color);
        mAgendaCurrentDayTextColor = getResources().getColor(R.color.calendar_text_current_day);
//        mCalendarHeaderColor = R.color.calendar_header_day_background;
        mCalendarHeaderColor = R.color.calendar_text_current_day;
        mCalendarDayTextColor = R.color.calendar_text_day;
        mCalendarCurrentDayColor = R.color.calendar_text_current_day;
        mCalendarPastDayTextColor = R.color.calendar_text_day;


        initCalendarInfo();

        calendarView.setBackgroundColor(getResources().getColor(mCalendarHeaderColor));

        // CalendarView 初始化,完成布局的初始化，数据的绑定,日期的显示
        calendarView.init(CalendarManager.getInstance(this), getResources().getColor(mCalendarDayTextColor), getResources().getColor(mCalendarCurrentDayColor), getResources().getColor(mCalendarPastDayTextColor));

        // 初始化AgendaView的数据适配器
        agendaAdapter = new AgendaAdapter(mAgendaCurrentDayTextColor);
        agendaView.getAgendaListView().setAdapter(agendaAdapter);

        //注册AgendaListView的OnStickyHeaderChanged监听事件
        agendaView.getAgendaListView().setOnStickyHeaderChangedListener(this);
        agendaView.getAgendaListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView alarm_id = (TextView) view.findViewById(R.id.Alarm_id);
                Toast.makeText(getApplicationContext(),position+"-"+alarm_id.getText().toString(),Toast.LENGTH_SHORT).show();
                if(!alarm_id.getText().toString().equals("0")){
//                    Intent intent = new Intent(getBaseContext(), ScheduleDetailActivity.class);
//                    intent.putExtra("id",alarm_id.getText().toString());
//                    mActivity.startActivity(intent);
//                    mActivity.finish();
                }

            }
        });
        List eventList = new ArrayList<>();
        //将日期与event进行匹配
        CalendarManager.getInstance().loadEvents(eventList);

        // 添加默认的 Renderer 渲染布局
        addEventRenderer(new DefaultEventRenderer());
        chooseDayFromClick();
    }


    //为event添加布局渲染
    public void addEventRenderer(@NonNull final EventRenderer<?> renderer) {
        AgendaAdapter adapter = (AgendaAdapter) agendaView.getAgendaListView().getAdapter();
        adapter.addEventRenderer(renderer);
    }
    /**
     * 初始化日历信息
     */
    private void initCalendarInfo() {
        // 设置日历显示的时间，最大为当前时间+1年，最小为当前时间-2月
        Calendar minDate = Calendar.getInstance();

        minDate.setFirstDayOfWeek(Calendar.MONDAY);

        Calendar maxDate = Calendar.getInstance();

        maxDate.setFirstDayOfWeek(Calendar.MONDAY);

        minDate.add(Calendar.MONTH, -10);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);
        //根据你传入的开始结束值，构建生成Calendar数据（各种Item，JavaBean）
        CalendarManager.getInstance(this).buildCal(minDate, maxDate, Locale.getDefault());
    }

    @Override
    public void onStickyHeaderChanged(StickyListHeadersListView l, View header, int itemPosition, long headerId) {
        if (CalendarManager.getInstance().getEvents().size() > 0) {
            CalendarEvent event = CalendarManager.getInstance().getEvents().get(itemPosition);
            if (event != null) {
                calendarView.scrollToDate(event);
            }
        }
    }
    /**
     * 点击 CalendarView 选择不同的日期
     */
    private void chooseDayFromClick(){
        //BusProvider处理事件
        BusProvider.getInstance().toObserverable()
                .subscribe(event -> {
                    if (event instanceof Events.DayClickedEvent) {
                        //Day日期点击事件
                        Events.DayClickedEvent clickedEvent = (Events.DayClickedEvent) event;
                        DayItem mDayItem = clickedEvent.getDay();
//                        if(mDayItem.isToday()){
//                            mDayItem.setSelected(true);
//                        }else
//                            mDayItem.setSelected(false);
//                                setDateToShow(clickedEvent.getCalendar(),list);


                    }else if(event instanceof Events.GoBackToDay){
                        Calendar calendar = Calendar.getInstance();

                        calendar.setFirstDayOfWeek(Calendar.MONDAY);

                        calendar.setTimeInMillis(System.currentTimeMillis());
//                        list= new AlarmDBSupport(mActivity).getDataByDay(calendar);
//                        setDateToShow(calendar,list);

                    }

                });
    }
}
