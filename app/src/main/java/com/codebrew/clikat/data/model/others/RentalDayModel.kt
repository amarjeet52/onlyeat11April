package com.codebrew.clikat.data.model.others

import java.util.*

data class RentalDayModel(var dayName:String,var dayStatus:Boolean,var startDate:String, var endDate:String, var dayId:Int,var dayFormt:Calendar) {
    constructor():this("",false,"","",-1, Calendar.getInstance())
}