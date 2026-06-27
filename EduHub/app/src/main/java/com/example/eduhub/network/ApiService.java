package com.example.eduhub.network;

import com.example.eduhub.network.dto.AuthRequest;
import com.example.eduhub.network.dto.AuthResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @GET("auth/me")
    Call<AuthResponse> getAuthMe();

    @POST("auth/refresh")
    Call<AuthResponse> refresh(@Body Map<String, String> body);

    @POST("auth/logout")
    Call<Void> logout(@Body Map<String, String> body);

    @GET("user")
    Call<List<Map<String, Object>>> getAllUsers();

    @GET("user/{id}")
    Call<Map<String, Object>> getUserById(@Path("id") int id);

    @POST("user")
    Call<Map<String, Object>> createUser(@Body Map<String, Object> body);

    @PUT("user/{id}")
    Call<Map<String, Object>> updateUser(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("user/{id}")
    Call<Void> deleteUser(@Path("id") int id);

    @GET("student")
    Call<List<Map<String, Object>>> getAllStudents(@Query("groupId") String groupId);

    @GET("student/{id}")
    Call<Map<String, Object>> getStudentById(@Path("id") int id);

    @GET("student/info/{id}")
    Call<Map<String, Object>> getStudentInfo(@Path("id") int id);

    @POST("student")
    Call<Map<String, Object>> createStudent(@Body Map<String, Object> body);

    @PUT("student/{id}")
    Call<Map<String, Object>> updateStudent(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("student/{id}")
    Call<Void> deleteStudent(@Path("id") int id);

    @GET("teacher")
    Call<List<Map<String, Object>>> getAllTeachers();

    @GET("teacher/{id}")
    Call<Map<String, Object>> getTeacherById(@Path("id") int id);

    @GET("teacher/{id}/groups")
    Call<List<Map<String, Object>>> getTeacherGroups(@Path("id") int id);

    @GET("teacher/{id}/subjects")
    Call<List<Map<String, Object>>> getTeacherSubjects(@Path("id") int id);

    @POST("teacher")
    Call<Map<String, Object>> createTeacher(@Body Map<String, Object> body);

    @PUT("teacher/{id}")
    Call<Map<String, Object>> updateTeacher(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("teacher/{id}")
    Call<Void> deleteTeacher(@Path("id") int id);

    @GET("group")
    Call<List<Map<String, Object>>> getAllGroups();

    @GET("group/{id}")
    Call<Map<String, Object>> getGroupById(@Path("id") int id);

    @POST("group")
    Call<Map<String, Object>> createGroup(@Body Map<String, Object> body);

    @PUT("group/{id}")
    Call<Map<String, Object>> updateGroup(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("group/{id}")
    Call<Void> deleteGroup(@Path("id") int id);

    @GET("subject")
    Call<List<Map<String, Object>>> getAllSubjects();

    @GET("subject/{id}")
    Call<Map<String, Object>> getSubjectById(@Path("id") int id);

    @POST("subject")
    Call<Map<String, Object>> createSubject(@Body Map<String, Object> body);

    @PUT("subject/{id}")
    Call<Map<String, Object>> updateSubject(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("subject/{id}")
    Call<Void> deleteSubject(@Path("id") int id);

    @GET("lesson")
    Call<List<Map<String, Object>>> getLessons(@QueryMap Map<String, String> filters);

    @GET("lesson/{id}")
    Call<Map<String, Object>> getLessonById(@Path("id") int id);

    @POST("lesson")
    Call<Map<String, Object>> createLesson(@Body Map<String, Object> body);

    @PUT("lesson/{id}")
    Call<Map<String, Object>> updateLesson(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("lesson/{id}")
    Call<Void> deleteLesson(@Path("id") int id);

    @GET("grade")
    Call<List<Map<String, Object>>> getGrades(@QueryMap Map<String, String> filters);

    @GET("grade/{id}")
    Call<Map<String, Object>> getGradeById(@Path("id") int id);

    @GET("grade/averages")
    Call<List<Map<String, Object>>> getGradeAverages(@Query("studentId") int studentId);

    @GET("grade/journal")
    Call<List<Map<String, Object>>> getGradeJournal(@Query("lessonId") int lessonId, @Query("groupId") int groupId);

    @POST("grade")
    Call<Map<String, Object>> createGrade(@Body Map<String, Object> body);

    @POST("grade/batch")
    Call<List<Map<String, Object>>> createGradesBatch(@Body List<Map<String, Object>> body);

    @PUT("grade/{id}")
    Call<Map<String, Object>> updateGrade(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("grade/{id}")
    Call<Void> deleteGrade(@Path("id") int id);

    @GET("attendance")
    Call<List<Map<String, Object>>> getAttendance(@QueryMap Map<String, String> filters);

    @GET("attendance/{id}")
    Call<Map<String, Object>> getAttendanceById(@Path("id") int id);

    @POST("attendance")
    Call<Map<String, Object>> createAttendance(@Body Map<String, Object> body);

    @PUT("attendance/{id}")
    Call<Map<String, Object>> updateAttendance(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("attendance/{id}")
    Call<Void> deleteAttendance(@Path("id") int id);

    @GET("assignment")
    Call<List<Map<String, Object>>> getAssignments(@QueryMap Map<String, String> filters);

    @GET("assignment/{id}")
    Call<Map<String, Object>> getAssignmentById(@Path("id") int id);

    @GET("assignment/{id}/stats")
    Call<Map<String, Object>> getAssignmentStats(@Path("id") int id);

    @POST("assignment")
    Call<Map<String, Object>> createAssignment(@Body Map<String, Object> body);

    @PUT("assignment/{id}")
    Call<Map<String, Object>> updateAssignment(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("assignment/{id}")
    Call<Void> deleteAssignment(@Path("id") int id);

    @GET("homework-completion")
    Call<List<Map<String, Object>>> getHomeworkCompletions(@QueryMap Map<String, String> filters);

    @GET("homework-completion/{id}")
    Call<Map<String, Object>> getHomeworkCompletionById(@Path("id") int id);

    @POST("homework-completion")
    Call<Map<String, Object>> createHomeworkCompletion(@Body Map<String, Object> body);

    @PUT("homework-completion/{id}")
    Call<Map<String, Object>> updateHomeworkCompletion(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("homework-completion/{id}")
    Call<Void> deleteHomeworkCompletion(@Path("id") int id);

    @GET("announcement")
    Call<List<Map<String, Object>>> getAnnouncements(@QueryMap Map<String, String> filters);

    @GET("announcement/{id}")
    Call<Map<String, Object>> getAnnouncementById(@Path("id") int id);

    @POST("announcement")
    Call<Map<String, Object>> createAnnouncement(@Body Map<String, Object> body);

    @PUT("announcement/{id}")
    Call<Map<String, Object>> updateAnnouncement(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("announcement/{id}")
    Call<Void> deleteAnnouncement(@Path("id") int id);

    @GET("announcement-read")
    Call<List<Map<String, Object>>> getAllAnnouncementReads();

    @GET("announcement-read/{id}")

    Call<Map<String, Object>> getAnnouncementReadById(@Path("id") int id);

    @POST("announcement-read")
    Call<Map<String, Object>> createAnnouncementRead(@Body Map<String, Object> body);

    @PUT("announcement-read/{id}")
    Call<Map<String, Object>> updateAnnouncementRead(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("announcement-read/{id}")
    Call<Void> deleteAnnouncementRead(@Path("id") int id);

    @GET("notification")
    Call<List<Map<String, Object>>> getNotifications(@QueryMap Map<String, String> filters);

    @GET("notification/{id}")
    Call<Map<String, Object>> getNotificationById(@Path("id") int id);

    @POST("notification")
    Call<Map<String, Object>> createNotification(@Body Map<String, Object> body);

    @PUT("notification/{id}")
    Call<Map<String, Object>> updateNotification(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("notification/{id}")
    Call<Void> deleteNotification(@Path("id") int id);

    @GET("user-settings")
    Call<List<Map<String, Object>>> getAllUserSettings();

    @GET("user-settings/{userId}")
    Call<Map<String, Object>> getUserSettingsByUserId(@Path("userId") int userId);

    @POST("user-settings")
    Call<Map<String, Object>> createUserSettings(@Body Map<String, Object> body);

    @PUT("user-settings/{userId}")
    Call<Map<String, Object>> updateUserSettings(@Path("userId") int userId, @Body Map<String, Object> body);

    @DELETE("user-settings/{userId}")
    Call<Void> deleteUserSettings(@Path("userId") int userId);

    @GET("schedule-break")
    Call<List<Map<String, Object>>> getAllScheduleBreaks();

    @GET("schedule-break/{id}")
    Call<Map<String, Object>> getScheduleBreakById(@Path("id") int id);

    @POST("schedule-break")
    Call<Map<String, Object>> createScheduleBreak(@Body Map<String, Object> body);

    @PUT("schedule-break/{id}")
    Call<Map<String, Object>> updateScheduleBreak(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE("schedule-break/{id}")
    Call<Void> deleteScheduleBreak(@Path("id") int id);
}
