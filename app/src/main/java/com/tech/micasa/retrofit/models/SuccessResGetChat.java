package com.tech.micasa.retrofit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SuccessResGetChat implements Serializable {
    @SerializedName("result")
    @Expose
    public List<Result> result = null;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("status")
    @Expose
    public String status;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public class ReceiverDetail {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("mobile")
        @Expose
        private String mobile;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("register_id")
        @Expose
        private String registerId;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("lat")
        @Expose
        private String lat;
        @SerializedName("lon")
        @Expose
        private String lon;
        @SerializedName("date_time")
        @Expose
        private String dateTime;
        @SerializedName("receiver_image")
        @Expose
        private String receiverImage;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getReceiverImage() {
            return receiverImage;
        }

        public void setReceiverImage(String receiverImage) {
            this.receiverImage = receiverImage;
        }

    }

    public class Result {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("sender_id")
        @Expose
        private String senderId;
        @SerializedName("receiver_id")
        @Expose
        private String receiverId;
        @SerializedName("item_id")
        @Expose
        private String itemId;
        @SerializedName("chat_message")
        @Expose
        private String chatMessage;
        @SerializedName("chat_image")
        @Expose
        private String chatImage;
        @SerializedName("chat_audio")
        @Expose
        private String chatAudio;
        @SerializedName("chat_video")
        @Expose
        private String chatVideo;
        @SerializedName("chat_document")
        @Expose
        private String chatDocument;
        @SerializedName("lat")
        @Expose
        private String lat;
        @SerializedName("lon")
        @Expose
        private String lon;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("contact")
        @Expose
        private String contact;
        @SerializedName("clear_chat")
        @Expose
        private String clearChat;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("time_ago")
        @Expose
        private String timeAgo;
        @SerializedName("result")
        @Expose
        private String result;
        @SerializedName("sender_detail")
        @Expose
        private SenderDetail senderDetail;
        @SerializedName("receiver_detail")
        @Expose
        private ReceiverDetail receiverDetail;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(String receiverId) {
            this.receiverId = receiverId;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getChatMessage() {
            return chatMessage;
        }

        public void setChatMessage(String chatMessage) {
            this.chatMessage = chatMessage;
        }

        public String getChatImage() {
            return chatImage;
        }

        public void setChatImage(String chatImage) {
            this.chatImage = chatImage;
        }

        public String getChatAudio() {
            return chatAudio;
        }

        public void setChatAudio(String chatAudio) {
            this.chatAudio = chatAudio;
        }

        public String getChatVideo() {
            return chatVideo;
        }

        public void setChatVideo(String chatVideo) {
            this.chatVideo = chatVideo;
        }

        public String getChatDocument() {
            return chatDocument;
        }

        public void setChatDocument(String chatDocument) {
            this.chatDocument = chatDocument;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getClearChat() {
            return clearChat;
        }

        public void setClearChat(String clearChat) {
            this.clearChat = clearChat;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTimeAgo() {
            return timeAgo;
        }

        public void setTimeAgo(String timeAgo) {
            this.timeAgo = timeAgo;
        }


        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public SenderDetail getSenderDetail() {
            return senderDetail;
        }

        public void setSenderDetail(SenderDetail senderDetail) {
            this.senderDetail = senderDetail;
        }

        public ReceiverDetail getReceiverDetail() {
            return receiverDetail;
        }

        public void setReceiverDetail(ReceiverDetail receiverDetail) {
            this.receiverDetail = receiverDetail;
        }

    }

    public class SenderDetail {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("mobile")
        @Expose
        private String mobile;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("register_id")
        @Expose
        private String registerId;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("lat")
        @Expose
        private String lat;
        @SerializedName("lon")
        @Expose
        private String lon;
        @SerializedName("date_time")
        @Expose
        private String dateTime;
        @SerializedName("sender_image")
        @Expose
        private String senderImage;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getSenderImage() {
            return senderImage;
        }

        public void setSenderImage(String senderImage) {
            this.senderImage = senderImage;
        }

    }

}