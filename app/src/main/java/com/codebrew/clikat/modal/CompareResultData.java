package com.codebrew.clikat.modal;

/*
 * Crated by cbl80 on 8/7/16.
 */
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class CompareResultData {

    @SerializedName("details")
    @Expose
    private List<CompareResultDetail> details = new ArrayList<CompareResultDetail>();

    /**
     *
     * @return
     * The details
     */
    public List<CompareResultDetail> getDetails() {
        return details;
    }

    /**
     *
     * @param details
     * The details
     */
    public void setDetails(List<CompareResultDetail> details) {
        this.details = details;
    }

}
