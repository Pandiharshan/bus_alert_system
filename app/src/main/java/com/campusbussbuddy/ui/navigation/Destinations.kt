package com.campusbussbuddy.ui.navigation

object Destinations {
    const val AUTH = "auth"
    const val STUDENT = "student"
    const val DRIVER = "driver"
    
    // Auth destinations
    const val LOGIN = "login"
    const val REGISTER = "register"
    
    // Student destinations
    const val STUDENT_HOME = "student_home"
    const val BUS_MAP = "bus_map"
    const val QR_SCANNER = "qr_scanner"
    const val ABSENT_CALENDAR = "absent_calendar"
    const val STUDENT_PROFILE = "student_profile"
    
    // Driver destinations
    const val DRIVER_PORTAL = "driver_portal"
    const val BUS_LOGIN = "bus_login"
    const val DRIVER_BUS_HOME = "driver_bus_home"
    const val TRIP_SCREEN = "trip_screen"
    const val BUS_MEMBERS = "bus_members"
    const val ATTENDANCE = "attendance"
    const val BUS_PROFILE = "bus_profile"
    
    // Legacy driver destinations (for backward compatibility)
    const val DRIVER_HOME = "driver_home"
    const val TRIP_MANAGEMENT = "trip_management"
    const val QR_GENERATOR = "qr_generator"
    const val DRIVER_PROFILE = "driver_profile"
}