package com.codebrew.clikat.modal

import com.google.gson.annotations.SerializedName

data class ResponseZoomCall(

	@field:SerializedName("data")
	val data: DataZoom? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class Settings(

	@field:SerializedName("join_before_host")
	val joinBeforeHost: Boolean? = null,

	@field:SerializedName("contact_name")
	val contactName: String? = null,

	@field:SerializedName("cn_meeting")
	val cnMeeting: Boolean? = null,

	@field:SerializedName("watermark")
	val watermark: Boolean? = null,

	@field:SerializedName("registrants_email_notification")
	val registrantsEmailNotification: Boolean? = null,

	@field:SerializedName("use_pmi")
	val usePmi: Boolean? = null,

	@field:SerializedName("approval_type")
	val approvalType: Int? = null,

	@field:SerializedName("close_registration")
	val closeRegistration: Boolean? = null,

	@field:SerializedName("host_video")
	val hostVideo: Boolean? = null,

	@field:SerializedName("auto_recording")
	val autoRecording: String? = null,

	@field:SerializedName("registrants_confirmation_email")
	val registrantsConfirmationEmail: Boolean? = null,

	@field:SerializedName("enforce_login")
	val enforceLogin: Boolean? = null,

	@field:SerializedName("contact_email")
	val contactEmail: String? = null,

	@field:SerializedName("meeting_authentication")
	val meetingAuthentication: Boolean? = null,

	@field:SerializedName("waiting_room")
	val waitingRoom: Boolean? = null,

	@field:SerializedName("alternative_hosts")
	val alternativeHosts: String? = null,

	@field:SerializedName("participant_video")
	val participantVideo: Boolean? = null,

	@field:SerializedName("audio")
	val audio: String? = null,

	@field:SerializedName("in_meeting")
	val inMeeting: Boolean? = null,

	@field:SerializedName("mute_upon_entry")
	val muteUponEntry: Boolean? = null,

	@field:SerializedName("enforce_login_domains")
	val enforceLoginDomains: String? = null,

	@field:SerializedName("request_permission_to_unmute_participants")
	val requestPermissionToUnmuteParticipants: Boolean? = null
)

data class DataZoom(

	@field:SerializedName("settings")
	val settings: Settings? = null,

	@field:SerializedName("join_url")
	val joinUrl: String? = null,

	@field:SerializedName("pstn_password")
	val pstnPassword: String? = null,

	@field:SerializedName("timezone")
	val timezone: String? = null,

	@field:SerializedName("start_url")
	val startUrl: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("type")
	val type: Int? = null,

	@field:SerializedName("uuid")
	val uuid: String? = null,

	@field:SerializedName("host_id")
	val hostId: String? = null,

	@field:SerializedName("duration")
	val duration: Int? = null,

	@field:SerializedName("start_time")
	val startTime: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("h323_password")
	val h323Password: String? = null,

	@field:SerializedName("topic")
	val topic: String? = null,

	@field:SerializedName("id")
	val id: Long? = null,

	@field:SerializedName("host_email")
	val hostEmail: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("encrypted_password")
	val encryptedPassword: String? = null
)
