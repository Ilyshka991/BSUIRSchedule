package com.pechuro.bsuirschedule.feature.flow

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.navOptions
import com.pechuro.bsuirschedule.R
import com.pechuro.bsuirschedule.common.BaseEvent
import com.pechuro.bsuirschedule.common.EventBus
import com.pechuro.bsuirschedule.common.base.BaseFragment
import com.pechuro.bsuirschedule.domain.common.Logger
import com.pechuro.bsuirschedule.domain.entity.Schedule
import com.pechuro.bsuirschedule.domain.entity.ScheduleDisplayType
import com.pechuro.bsuirschedule.domain.entity.ScheduleItem
import com.pechuro.bsuirschedule.ext.setSafeClickListener
import com.pechuro.bsuirschedule.feature.addschedule.AddScheduleCompleteEvent
import com.pechuro.bsuirschedule.feature.displayschedule.DisplayScheduleContainerArgs
import com.pechuro.bsuirschedule.feature.displayschedule.DisplayScheduleEvent
import com.pechuro.bsuirschedule.feature.loadinfo.LoadInfoCompleteEvent
import com.pechuro.bsuirschedule.feature.navigation.NavigationSheetEvent
import com.pechuro.bsuirschedule.feature.scheduleitemdetails.ScheduleItemDetailsFragmentArgs
import com.pechuro.bsuirschedule.feature.updateschedule.UpdateScheduleSheetArgs
import kotlinx.android.synthetic.main.fragment_flow.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FlowFragment : BaseFragment() {

    override val layoutId: Int = R.layout.fragment_flow

    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        initViewModel(FlowViewModel::class)
    }

    private val navController: NavController by lazy(LazyThreadSafetyMode.NONE) {
        Navigation.findNavController(requireActivity(), R.id.navigationFragmentHost)
    }
    private val defaultNavOptions = navOptions {
        anim {
            enter = R.anim.fragment_open_enter
            exit = R.anim.fragment_open_exit
            popEnter = R.anim.fragment_close_enter
            popExit = R.anim.fragment_close_exit
        }
        launchSingleTop = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNavigation()
        setStartDestination()
        lifecycleScope.launch(Dispatchers.IO) {
            if (savedInstanceState == null && !viewModel.isInfoLoaded()) openLoadInfo()
            checkScheduleUpdates()
        }
        initViews()
        receiveEvents()
    }

    override fun onResume() {
        super.onResume()
        requireView().post { updateLayoutState() }
    }

    override fun onBackPressed(): Boolean {
        when (navController.currentDestination?.id) {
            R.id.loadInfoDestination -> return false
        }
        if (navController.navigateUp()) return true
        return false
    }

    private fun initNavigation() {
        navController.addOnDestinationChangedListener { _, _, _ ->
            requireView().post { updateLayoutState() }
        }
    }

    private fun initViews() {
        bottomBarMenuButton.setSafeClickListener {
            openNavigationSheet()
        }
        bottomBarDisplayOptionsButton.setSafeClickListener {
            openDisplayScheduleOptions()
        }
        bottomBarGoToDateButton.setSafeClickListener {
            //TODO: implement
        }
        bottomBarDisplayAddButton.setSafeClickListener {
            EventBus.send(FlowFragmentEvent.DisplayScheduleAddItem)
        }
    }

    private suspend fun checkScheduleUpdates() {
        val availableForUpdateSchedules = viewModel.getAvailableForUpdateSchedules()
        if (availableForUpdateSchedules.isNotEmpty()) {
            openUpdateSchedules(availableForUpdateSchedules)
        }
    }

    private fun receiveEvents() {
        EventBus.receive<BaseEvent>(lifecycleScope) { event ->
            when (event) {
                is LoadInfoCompleteEvent -> popFragment()
                is AddScheduleCompleteEvent -> {
                    event.schedules.firstOrNull()?.let { schedule ->
                        openViewSchedule(schedule, skipIfOpened = false)
                    }
                }
                is NavigationSheetEvent.OnAddSchedule -> openAddSchedule()
                is NavigationSheetEvent.OnScheduleClick -> openViewSchedule(event.schedule)
                is NavigationSheetEvent.OnScheduleDeleted -> onScheduleDeleted(event.schedule)
                is NavigationSheetEvent.OnOpenSettings -> {
                }
                is DisplayScheduleEvent.OpenScheduleItem -> openScheduleItemDetails(event.scheduleItem)
                is DisplayScheduleEvent.OnPositionChanged -> onDisplaySchedulePositionChanged(event.position)
            }
        }
    }

    private fun openScheduleItemDetails(scheduleItem: ScheduleItem) {
        val args = ScheduleItemDetailsFragmentArgs(scheduleItem).toBundle()
        navController.navigate(R.id.scheduleItemDestination, args, defaultNavOptions)
    }

    private fun openNavigationSheet() {
        navController.navigate(R.id.navigationSheetDestination)
    }

    private fun openLoadInfo() {
        navController.navigate(R.id.loadInfoDestination, null, defaultNavOptions)
    }

    private fun openUpdateSchedules(schedules: List<Schedule>) {
        val currentDestinationId = navController.currentDestination?.id
        if (currentDestinationId == R.id.navigationSheetDestination ||
                currentDestinationId == R.id.updateScheduleSheetDestination) return
        navController.navigate(
                R.id.updateScheduleSheetDestination,
                UpdateScheduleSheetArgs(schedules.toTypedArray()).toBundle(),
                defaultNavOptions
        )
    }

    private fun openAddSchedule() {
        popFragment()
        navController.navigate(R.id.addScheduleDestination, null, defaultNavOptions)
    }

    private fun openDisplayScheduleOptions() {
        navController.navigate(R.id.displayScheduleOptionsDestination, null, defaultNavOptions)
    }

    private fun openViewSchedule(schedule: Schedule, skipIfOpened: Boolean = true) {
        if (skipIfOpened && viewModel.getLastOpenedSchedule() == schedule) return
        viewModel.setLastOpenedSchedule(schedule)
        setViewScheduleStartDestination(schedule)
        val arguments = DisplayScheduleContainerArgs(schedule).toBundle()
        navController.navigate(R.id.viewScheduleDestination, arguments, defaultNavOptions)
    }

    private fun onScheduleDeleted(schedule: Schedule) {
        if (viewModel.getLastOpenedSchedule() == schedule) {
            viewModel.setLastOpenedSchedule(null)
            setDefaultStartDestination()
        }
    }

    private fun setStartDestination() {
        val lastOpenedSchedule = viewModel.getLastOpenedSchedule()
        if (lastOpenedSchedule == null) {
            setDefaultStartDestination()
        } else {
            setViewScheduleStartDestination(lastOpenedSchedule)
        }
    }

    private fun setViewScheduleStartDestination(schedule: Schedule) {
        val navGraph = navController.navInflater.inflate(R.navigation.navigation_graph)
        val arguments = DisplayScheduleContainerArgs(schedule).toBundle()
        navGraph.startDestination = R.id.viewScheduleDestination
        try {
            navController.setGraph(navGraph, arguments)
        } catch (e: IllegalStateException) {
            Logger.e(e)
            //TODO: Possible bug: DialogFragment doesn't exist in the FragmentManager
        }
        requireView().post { updateLayoutState() }

    }

    private fun setDefaultStartDestination() {
        val navGraph = navController.navInflater.inflate(R.navigation.navigation_graph)
        navGraph.startDestination = R.id.startDestination
        try {
            navController.graph = navGraph
        } catch (e: IllegalStateException) {
            Logger.e(e)
            //TODO: Possible bug: DialogFragment doesn't exist in the FragmentManager
        }
        requireView().post { updateLayoutState() }
    }

    private fun popFragment() = navController.popBackStack()

    private fun updateLayoutState() {
        val isControlsVisible = when (navController.currentDestination?.id) {
            R.id.addScheduleDestination,
            R.id.scheduleItemDestination,
            R.id.loadInfoDestination -> false
            else -> true
        }
        updateFabState()
        if (!isControlsVisible) bottomBarFab.hide()
        updateBottomBarState()
        bottomBarParentView.isVisible = isControlsVisible
    }

    private fun updateFabState() {
        val fabState = when (viewModel.getLastOpenedSchedule()) {
            is Schedule.EmployeeClasses, is Schedule.GroupClasses -> FabActionState.DISPLAY_SCHEDULE_BACK
            is Schedule.EmployeeExams, is Schedule.GroupExams -> FabActionState.ADD_EXAM
            else -> FabActionState.ADD_SCHEDULE
        }
        if (fabState != FabActionState.DISPLAY_SCHEDULE_BACK) bottomBarFab.show()
        bottomBarFab.setImageDrawable(ContextCompat.getDrawable(requireContext(), fabState.iconRes))
        bottomBarFab.setSafeClickListener {
            when (fabState) {
                FabActionState.ADD_EXAM -> EventBus.send(FlowFragmentEvent.DisplayScheduleAddItem)
                FabActionState.DISPLAY_SCHEDULE_BACK -> EventBus.send(FlowFragmentEvent.DisplayScheduleSetToday)
                FabActionState.ADD_SCHEDULE -> openAddSchedule()
            }
        }
    }

    private fun updateBottomBarState() {
        when (viewModel.getLastOpenedSchedule()) {
            is Schedule.EmployeeClasses, is Schedule.GroupClasses -> {
                bottomBarDisplayOptionsButton.isVisible = true
                bottomBarGoToDateButton.isVisible = viewModel.getScheduleDisplayType() == ScheduleDisplayType.DAYS
                bottomBarDisplayAddButton.isVisible = viewModel.getScheduleDisplayType() == ScheduleDisplayType.DAYS
            }
            else -> {
                bottomBarDisplayOptionsButton.isVisible = false
                bottomBarGoToDateButton.isVisible = false
                bottomBarDisplayAddButton.isVisible = false
            }
        }
    }

    private fun onDisplaySchedulePositionChanged(position: Int) {
        if (position != 0) {
            bottomBarFab.show()
        } else {
            bottomBarFab.hide()
        }
    }
}