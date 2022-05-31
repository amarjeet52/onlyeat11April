package com.codebrew.clikat.data

enum class AppDataType(val type: Int) {
    Ecom(2),
    Food(1),
    HomeServ(8),
    CarRental(5),
    Beauty(6),
    Custom(10),
}

enum class FoodAppType(val foodType: Int) {
    Pickup(1),
    Delivery(0),
    Both(2),
    DineIn(3)

}

enum class DeliveryType(val type: String) {
    PickupOrder("PICKUP"),
    DeliveryOrder("DELIVERY"),
    DineIn("DINEIN")
}
enum class NearBySupplierType(val type: Int) {
    GRID(1),
    LIST(2)
}
enum class SearchType(val type: String) {
    TYPE_PROD("product"),
    TYPE_RESTU("restaurant")
}

enum class VendorAppType(val appType: Int) {
    Single(1),
    Multiple(0)
}

enum class PaymentType(val payType: Int) {
    CASH(0),
    ONLINE(1),
    BOTH(2)
}


enum class OrderStatus(val orderStatus: Double) {
    Pending(0.0),
    Approved(1.0),
    Confirmed(1.0),
    Rejected(2.0),
    Packed(2.5),
    In_Kitchen(2.5),
    In_Kitchen_CRAVE(11.0),
    PICKUP_ON_THE_WAY(2.51),
    On_The_Way(3.0),
    Started(3.0),
    Near_You(4.0),
    Ended(5.0),
    Delivered(5.0),
    Rating_Given(6.0),
    Track(7.0),
    Customer_Canceled(8.0),
    Scheduled(9.0),
    Ready_to_be_picked(2.6),
    Reached(2.6),
    Shipped(2.61),
    PickUp(5.0),
    Arrived(4.0),
    Admin_Canceled(12.0),
    /*ready to be picked used in pickup status*/
    READY_TO_BE_PICKED(10.0)
}

enum class ReturnStatus(val returnStatus: Int) {
    Return_requested(0),
    Agent_on_the_way(1),
    Product_picked(2),
    returned(3)
}

enum class RequestsStatus(val status: Double) {
    Pending(0.0),
    Approved(1.0),
    AdminRejected(2.0),
    UserCancelled(3.0),
    Cancelled(4.0)
}

enum class SearchDataType(val searchType: Int) {
    Product(0),
    Supplier(1),
    Both(2)
}

enum class OrderPayment(val payment: Int) {
    ReceiptOrder(1),
    EditOrder(2),
    PaymentAfterConfirm(4)
}

enum class SortBy(val sortBy: Int) {
    SortByDistance(1),
    SortByRating(2),
    SortByATZ(3),
    SortByZTA(4),
    SortByOpen(5),
    SortByClose(6),

    SortByFreeDelivery(7),
    SortByPrepTime(8),
    SortByNew(0)
}
enum class FilterBy(val filterBy: Int) {
    FilterByFreeDelivery(1),
    FilterByPrepTime(2),

}
enum class BranchCartFlow(val branchFlowType: Int) {
    SingleBranchFlow(0),
    MultipleBranchFlow(1)
}

enum class WalletAmountStatus(val added_deduct_through: Int) {
    ByAccount(0),
    ByShare(1),
    ByRefundAdded(2),
    ByOrderPlaced(3)

}

enum class BannerRedirection(val type: String) {
    NoRedirection("2"),
    ForCategory("0"),
    ForSupplier("1"),
    ForSubscription("3")
}

enum class DgFlow(val dgFlowType: Int) {
    TextLeftType(1),
    TextRightType(2),
    ListLeftType(3),
    PopUpLeftType(4),
    PopUpAdrsType(5),
    PopUpAmtType(6),
}

enum class ReceiverType(val type: String) {
    AGENT("1"),
    SUPPLIER("3"),
    ADMIN("4")
}

enum class MessageStatus {
    SENDING,
    SENT,
    ERROR
}

enum class SPType(val type: Int) {
    SupplierType(1),
    ProductType(2),
    LikeType(3),
    CommentType(4)
}

enum class AppVersionUpdate(val update: String) {
    NoUpdate("0"),
    NormalUpdate("1"),
    ForceUpdate("2")

}

enum class TableBookingStatus(val tableStatus: Double) {
    Pending(0.0),
    Approved(1.0),
    Rejected(2.0),
    Completed(3.0),
}

enum class SkipAppScreens(val type: String) {
    MAIN("MAIN"),
    ABOUT_APP("ABOUT_APP"),
    ACCOUNT("ACCOUNT"),
    PROFILE("PROFILE"),
    GET_HELP("GET_HELP")
}

enum class FilterByData(val type: Int) {
    Pickup(1),
    Delivery(0),
    DineIn(2)

}
