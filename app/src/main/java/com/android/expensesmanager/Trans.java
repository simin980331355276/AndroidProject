package com.android.expensesmanager;

public class Trans {

     String userAmount;
     String amountType;
     String categories;
     String date;
     String record;
     String Ref;
    public Trans(){

    }



    public String getUserAmount(){
        return userAmount;
    }
    public void setUserAmount(String user_Amount){
        userAmount = user_Amount;
    }



    public String getAmountType(){
        return amountType;
    }

    public void setAmountType(String amount_Type){
        amountType = amount_Type;
    }

    public String getCategories(){
        return categories;
    }

    public String getDate() {
        return date;
    }

    public String getRecord() {
        return record;
    }

    public void setCategories(String Categories){
        categories = Categories;
    }

    public String getRef() {
        return Ref;
    }

    public void setRef(String ref) {
        Ref = ref;
    }


}
