package com.codebrew.clikat.modal.other;

import javax.annotation.Nullable;

public class InstructionDtaModel {

    private String title;
    private String body;
    private @Nullable Integer image;
    private @Nullable String description;
    private @Nullable String description2;
    private @Nullable String imageUrl;

    public InstructionDtaModel(String title, String body, @Nullable  Integer image, @Nullable String description,@Nullable String description2,@Nullable String imageUrl) {
        this.title = title;
        this.body = body;
        this.image = image;
        this.description = description;
        this.description2 = description2;
        this.imageUrl=imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public @Nullable Integer getImage() {
        return image;
    }

    public @Nullable String getDescription() { return description; }

    public void setDescription(@Nullable String description) { this.description = description; }

    public @Nullable String getDescription2() { return description2; }

    public void setDescription2(@Nullable String description2) { this.description2 = description2; }

    public void setImage(@Nullable Integer image) {
        this.image = image;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@Nullable String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
