package nu.yakutomi.campuscafe;

public class OrderHistoryModel {
    private String mItem;
    private String mQuantity;
//    private String mUid;

    OrderHistoryModel() {}  // Needed for Firebase

    public OrderHistoryModel(String item, String quantity) {
        mItem = item;
        mQuantity = quantity;
  //      mUid = uid;
    }



    public String getItem() { return mItem; }
    public void setItem(String item) { this.mItem = item; }
    public String getQuantity() { return mQuantity; }
    public void setQuantity(String quantity) { this.mQuantity = quantity; }
    //public String getUid() { return mUid; }

}