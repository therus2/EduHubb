package com.example.eduhub.network.dto;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("refreshToken")
    private String refreshToken;

    @SerializedName("userId")
    private int userId;

    @SerializedName("studentId")
    private Integer studentId;

    @SerializedName("teacherId")
    private Integer teacherId;

    @SerializedName("role")
    private String role;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("patronymic")
    private String patronymic;

    @SerializedName("groupId")
    private Integer groupId;

    @SerializedName("groupName")
    private String groupName;

    @SerializedName("email")
    private String email;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    @SerializedName("isActive")
    private Boolean isActive;

    public String getToken() { return token; }
    public String getRefreshToken() { return refreshToken; }
    public int getUserId() { return userId; }
    public Integer getStudentId() { return studentId; }
    public Integer getTeacherId() { return teacherId; }
    public String getRole() { return role; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPatronymic() { return patronymic; }
    public Integer getGroupId() { return groupId; }
    public String getGroupName() { return groupName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAvatarUrl() { return avatarUrl; }
    public Boolean getIsActive() { return isActive; }
}
