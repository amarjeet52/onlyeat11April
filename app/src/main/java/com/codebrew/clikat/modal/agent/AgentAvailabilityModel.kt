package com.codebrew.clikat.modal.agent

class AgentAvailabilityModel(
        /**
         * statusCode : 200
         * success : 1
         * message : Success
         * data : [{"name":"shyam","cbl_user_availablities":[{"day_id":0,"id":385,"status":0},{"day_id":1,"id":386,"status":0},{"day_id":2,"id":387,"status":0},{"day_id":3,"id":388,"status":0},{"day_id":4,"id":389,"status":1},{"day_id":5,"id":390,"status":0},{"day_id":6,"id":391,"status":0}],"cbl_user_times":[{"id":126,"start_time":"17:38:00","end_time":"20:38:00","offset":"","date_id":50},{"id":128,"start_time":"17:38:00","end_time":"19:38:00","offset":"","date_id":0},{"id":129,"start_time":"20:39:00","end_time":"22:39:00","offset":"","date_id":0},{"id":130,"start_time":"14:41:00","end_time":"16:41:00","offset":"","date_id":0},{"id":131,"start_time":"21:41:00","end_time":"22:41:00","offset":"","date_id":0}],"cbl_user_avail_dates":[{"id":49,"status":1,"from_date":"2019-05-15","to_date":"2019-05-15"},{"id":50,"status":1,"from_date":"2019-05-16","to_date":"2019-05-16"}]}]
         */
        var statusCode: Int? = null,
        var success: Int? = null,
        var message: String? = null,
        var data: MutableList<AgentDataBean>? = null)

class AgentDataBean(
        /**
         * name : shyam
         * cbl_user_availablities : [{"day_id":0,"id":385,"status":0},{"day_id":1,"id":386,"status":0},{"day_id":2,"id":387,"status":0},{"day_id":3,"id":388,"status":0},{"day_id":4,"id":389,"status":1},{"day_id":5,"id":390,"status":0},{"day_id":6,"id":391,"status":0}]
         * cbl_user_times : [{"id":126,"start_time":"17:38:00","end_time":"20:38:00","offset":"","date_id":50},{"id":128,"start_time":"17:38:00","end_time":"19:38:00","offset":"","date_id":0},{"id":129,"start_time":"20:39:00","end_time":"22:39:00","offset":"","date_id":0},{"id":130,"start_time":"14:41:00","end_time":"16:41:00","offset":"","date_id":0},{"id":131,"start_time":"21:41:00","end_time":"22:41:00","offset":"","date_id":0}]
         * cbl_user_avail_dates : [{"id":49,"status":1,"from_date":"2019-05-15","to_date":"2019-05-15"},{"id":50,"status":1,"from_date":"2019-05-16","to_date":"2019-05-16"}]
         */
        var name: String? = null,
        var cbl_user_availablities: List<CblUserAvailablitiesBean>? = null,
        var cbl_user_times: List<CblUserTimesBean>? = null,
        var cbl_user_avail_dates: List<CblUserAvailDatesBean>? = null)

class CblUserAvailablitiesBean(
        /**
         * day_id : 0
         * id : 385
         * status : 0
         */
        var day_id: Int? = null,
        var id: Int? = null,
        var status: Int? = null

)

class CblUserTimesBean(
        /**
         * id : 126
         * start_time : 17:38:00
         * end_time : 20:38:00
         * offset :
         * date_id : 50
         */
        var id: Int? = null,
        var start_time: String? = null,
        var end_time: String? = null,
        var offset: String? = null,
        var date_id: Int? = null)

class CblUserAvailDatesBean(
        /**
         * id : 49
         * status : 1
         * from_date : 2019-05-15
         * to_date : 2019-05-15
         */
        var id: Int? = null,
        var status: Int? = null,
        var from_date: String? = null,
        var to_date: String? = null

)
