package com.codebrew.clikat.modal.eventBus;

public class UpdateCartEvent {

    private boolean isVisible;
    private boolean isDark;

    public UpdateCartEvent(boolean isVisible, boolean isDark) {
        this.isVisible=isVisible;
        this.isDark=isDark;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isDark() {
        return isDark;
    }

    public void setDark(boolean dark) {
        isDark = dark;
    }
}
