package com.example.taskmanager.calendar

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.R
import com.example.taskmanager.calendar
import com.example.taskmanager.databinding.CalendarDayLayoutBinding
import com.example.taskmanager.databinding.CalendarFragmentBinding
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var binding: CalendarFragmentBinding
    private lateinit var viewModel: CalendarViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.calendar_fragment, container, false)
        viewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)
        binding.viewModel = viewModel

        val calendarView = binding.calendarView
        calendarView.dayBinder = object : DayBinder<DayViewContainer> {

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.textView.text = day.date.dayOfMonth.toString()
            }

            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view)
            }

        }

        val curMonth = YearMonth.now()
        var firstMonth = curMonth.minusMonths(10)
        var lastMonth = curMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView.scrollToMonth(curMonth)


        return binding.root
    }

    class DayViewContainer(view: View): ViewContainer(view) {
        val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
    }

}