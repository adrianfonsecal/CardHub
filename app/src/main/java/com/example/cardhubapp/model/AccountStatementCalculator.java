package com.example.cardhubapp.model;


public class AccountStatementCalculator {
    public static Float calculateCurrentDebt(Float periodPayment, Float currentDebt){
        Float newCurrentDebt = currentDebt - periodPayment;
        return validateQuantity(newCurrentDebt);
    }

    public static Float calculatePaymentForNoInterest(Float periodPayment, Float paymentForNoInterest){
        Float newPaymentForNoInterest = paymentForNoInterest - periodPayment;
        return validateQuantity(newPaymentForNoInterest);
    }

    public static Float calculateExpiredCurrentDebt(Float periodPayment, Float currentDebt, Float interestRate){
        Float newCurrentDebt = (currentDebt + calculateExpiredPaymentForNoInterest(periodPayment, currentDebt, interestRate)) - periodPayment;
        return validateQuantity(newCurrentDebt);
    }
    public static Float calculateExpiredPaymentForNoInterest(Float periodPayment, Float paymentForNoInterest, Float interestRate){
        Float newPaymentForNoInterest = (paymentForNoInterest + (paymentForNoInterest * interestRate)) - periodPayment;
        return validateQuantity(newPaymentForNoInterest);

    }
    private static float validateQuantity(Float quantity) {
        if(quantity > 0){
            return quantity;
        }else{
            return 0.0f;
        }
    }
}
