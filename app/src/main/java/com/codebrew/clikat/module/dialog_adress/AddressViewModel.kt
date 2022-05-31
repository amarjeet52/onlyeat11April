package com.codebrew.clikat.module.dialog_adress

import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.*
import com.codebrew.clikat.data.model.api.vehicleDetails.*
import com.codebrew.clikat.modal.DataCommon
import com.codebrew.clikat.modal.ExampleCommon
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class AddressViewModel(dataManager: DataManager) : BaseViewModel<AddressNavigator>(dataManager) {

    val liveUserDeatil: MutableLiveData<List<VehicleData>> by lazy { MutableLiveData<List<VehicleData>>() }

    val vehicleDeleteLiveData: MutableLiveData<DeleteData> by lazy { MutableLiveData<DeleteData>() }

    val addressLiveData by lazy { SingleLiveEvent<DataBean>() }
    val addAdrsLiveData by lazy { SingleLiveEvent<AddressBean>() }
    val updateAdrsLiveData by lazy { SingleLiveEvent<AddressBean>() }
    val deleteAdrsData by lazy { SingleLiveEvent<DataCommon>() }


    fun getAddressList(supplierBranch: Int,zoneInfo:String?=null,value:String?=null) {
        //  setIsLoading(true)

        val param = dataManager.updateUserInf()
        if (dataManager.getCurrentUserLoggedIn()) {
            param["accessToken"] = dataManager.getKeyValue(
                PrefenceConstants.ACCESS_TOKEN,
                PrefenceConstants.TYPE_STRING
            ).toString()
            param["zone_id"]=zoneInfo.toString()
        }
        param["supplierBranchId"] = supplierBranch.toString()

        compositeDisposable.add(
            dataManager.getAllAddress(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }

    fun getAddressListUnderZone(supplierBranch: Int,zoneInfo:String?=null,value:String?=null) {
        //  setIsLoading(true)

        val param = dataManager.updateUserInf()
        if (dataManager.getCurrentUserLoggedIn()) {
            param["accessToken"] = dataManager.getKeyValue(
                    PrefenceConstants.ACCESS_TOKEN,
                    PrefenceConstants.TYPE_STRING
            ).toString()
            param["zone_id"]=zoneInfo.toString()
        }
        param["supplierBranchId"] = supplierBranch.toString()

        compositeDisposable.add(
                dataManager.getAllAddressnew(param)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }



    fun getAddressList1(supplierBranch: Int,zoneInfo:String?=null,value:String?=null) {
        //  setIsLoading(true)

        val param = dataManager.updateUserInf()
        if (dataManager.getCurrentUserLoggedIn()) {
            param["accessToken"] = dataManager.getKeyValue(
                    PrefenceConstants.ACCESS_TOKEN,
                    PrefenceConstants.TYPE_STRING
            ).toString()
            param["zone_id"]=zoneInfo.toString()
        }
        param["supplierBranchId"] = supplierBranch.toString()

        compositeDisposable.add(
                dataManager.getAllAddress(param)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }

    //user vehicle detail

    fun getUserVehicle(user_id: String) {
        setIsLoading(true)

        val param = dataManager.updateUserInfnew()

        param["user_id"] = user_id.toString()


        compositeDisposable.add(
            dataManager.getUserDEtails(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.userDetailResponsenew(it) }, { this.handleError(it) })
        )
    }


    private fun userDetailResponsenew(it: VehicalDetailsExample?) {
        setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {

            liveUserDeatil.value = it.data

        }
    }

    //post vehicle details
    fun deleteUserVehicle(id: String, user_id: String) {
        setIsLoading(true)

        val param = HashMap<String, String>()

        param["user_id"] = user_id.toString()
        param["id"] = id.toString()
        compositeDisposable.add(
            dataManager.deleteUser(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.deleteDetailResponsenew(it) }, { this.handleError(it) })
        )

//        compositeDisposable.add(dataManager.deleteUserDEtails(id.toString(),user_id.toString())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe({ this.deleteDetailResponsenew(it) }, { this.handleError(it) })
//        )
    }

    private fun deleteDetailResponsenew(it: DeleteVehicleDetails?) {
        setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {

            vehicleDeleteLiveData.value = it.data

        }
    }

    fun addAddress(param: HashMap<String, String>) {

        if (dataManager.getCurrentUserLoggedIn()) {
            param["accessToken"] = dataManager.getKeyValue(
                PrefenceConstants.ACCESS_TOKEN,
                PrefenceConstants.TYPE_STRING
            ).toString()
        }
        compositeDisposable.add(
            dataManager.addAddress(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateAddAdrs(it) }, { this.handleError(it) })
        )
    }

    fun editAddress(param: HashMap<String, String>) {

        if (dataManager.getCurrentUserLoggedIn()) {
            param["accessToken"] = dataManager.getKeyValue(
                PrefenceConstants.ACCESS_TOKEN,
                PrefenceConstants.TYPE_STRING
            ).toString()
        }
        compositeDisposable.add(
            dataManager.updateAddress(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.updateAdrs(it) }, { this.handleError(it) })
        )
    }

    fun deleteAddress(adressId: String) {
        val param = HashMap<String, String>()
        param["addressId"] = adressId
        if (dataManager.getCurrentUserLoggedIn()) {
            param["accessToken"] = dataManager.getKeyValue(
                PrefenceConstants.ACCESS_TOKEN,
                PrefenceConstants.TYPE_STRING
            ).toString()
        }
        compositeDisposable.add(
            dataManager.deleteAddress(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.deleteAdrs(it) }, { this.handleError(it) })
        )
    }

    private fun deleteAdrs(it: ExampleCommon?) {
        if (it?.status == NetworkConstants.SUCCESS) {
            deleteAdrsData.value = it.data
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    private fun updateAdrs(it: AddAddressModel?) {
        if (it?.status == NetworkConstants.SUCCESS) {
            updateAdrsLiveData.value = it.data
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    private fun validateAddAdrs(it: AddAddressModel?) {
        if (it?.status == NetworkConstants.SUCCESS) {
            addAdrsLiveData.value = it.data
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    private fun validateResponse(it: CustomerAddressModel?) {
        setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {
            addressLiveData.value = it.data
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur("Something went wrong") }
        }
    }

    fun getAddressList(param: HashMap<String, String>,isAddressUpdate: Boolean) {
            setIsLoading(true)

            compositeDisposable.add(
            dataManager.getZoneList(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.addressResponseList(it,isAddressUpdate) },
                    { this.handleError(it) })
        )
    }

    private fun addressResponseList(it: CustomerAddressListModel?,isAddressUpdate: Boolean) {
        setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {
            if(it.message?.isNotEmpty()==true)
            {
                navigator.onZoneData(it.message?.firstOrNull(),isAddressUpdate)
            }
        }
    }

    fun getAddressJson(param: HashMap<String, String>,isAddressUpdate:Boolean) {
        setIsLoading(true)

        compositeDisposable.add(
            dataManager.getAllAddressnew(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.addressResponseJson(it,isAddressUpdate) },
                    { this.handleError(it) })
        )
    }

    private fun addressResponseJson(it: CustomerAddressModel?, isAddressUpdate: Boolean) {
        setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {
            if (it.data?.zoneInformation?.isNotEmpty() == true) {
                navigator.onZoneData(it.data?.zoneInformation?.firstOrNull(), isAddressUpdate)
            }
        }
    }


    private fun handleError(e: Throwable) {
        setIsLoading(false)
        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            } else {
                navigator.onErrorOccur(it)
            }
        }


    }

}
