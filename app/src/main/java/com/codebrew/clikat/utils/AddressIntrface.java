package com.codebrew.clikat.utils;

import com.codebrew.clikat.data.model.api.AddressBean;

public interface AddressIntrface {

    void onAddressAdd(AddressBean addressBean );

    void onAddressUpdate(AddressBean addressBean);

    void onDismiss();
}
