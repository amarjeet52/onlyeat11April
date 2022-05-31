package com.codebrew.clikat.data.network;

import java.io.IOException;

public class NoConnectivityException extends IOException {

    @Override
    public String getMessage() {
        return "No Network Connection";
    }

}
