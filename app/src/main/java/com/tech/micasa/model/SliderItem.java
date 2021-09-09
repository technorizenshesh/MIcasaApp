package com.tech.micasa.model;

public class SliderItem {

    private String SlderId;
    private String SlderName;
    private String SliderImageUrl;
    private String SliderStatus;

    public SliderItem(String slderId, String slderName, String sliderImageUrl, String sliderStatus) {
        SlderId = slderId;
        SlderName = slderName;
        SliderImageUrl = sliderImageUrl;
        SliderStatus = sliderStatus;
    }

    public String getSlderId() {
        return SlderId;
    }

    public void setSlderId(String slderId) {
        SlderId = slderId;
    }

    public String getSlderName() {
        return SlderName;
    }

    public void setSlderName(String slderName) {
        SlderName = slderName;
    }

    public String getSliderImageUrl() {
        return SliderImageUrl;
    }

    public void setSliderImageUrl(String sliderImageUrl) {
        SliderImageUrl = sliderImageUrl;
    }

    public String getSliderStatus() {
        return SliderStatus;
    }

    public void setSliderStatus(String sliderStatus) {
        SliderStatus = sliderStatus;
    }

}