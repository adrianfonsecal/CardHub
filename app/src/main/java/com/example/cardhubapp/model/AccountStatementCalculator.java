package com.example.cardhubapp.model;


public class AccountStatementCalculator {
    public static Float calculateCurrentDebt(Float periodPayment, Float currentDebt, TransactionType transactionType){
        Float newCurrentDebt = null;
        if(transactionType == TransactionType.EXPENSE){
            newCurrentDebt = currentDebt + periodPayment;
        }else{
            newCurrentDebt = currentDebt - periodPayment;
        }
        return validateQuantity(newCurrentDebt);
    }

    public static Float calculatePaymentForNoInterest(Float periodPayment, Float paymentForNoInterest, TransactionType transactionType){
        Float newPaymentForNoInterest = null;
        if(transactionType == TransactionType.EXPENSE){
            newPaymentForNoInterest = paymentForNoInterest + periodPayment;
        }else{
            newPaymentForNoInterest = paymentForNoInterest - periodPayment;
        }
        return validateQuantity(newPaymentForNoInterest);
    }

    public static Float calculateExpiredCurrentDebt(Float periodPayment, Float currentDebt, Float paymentForNoInterest, Float interestRate, TransactionType transactionType){
        Float newCurrentDebt = null;
        if(transactionType == TransactionType.EXPENSE){
            newCurrentDebt = (currentDebt + (paymentForNoInterest * interestRate)) + periodPayment;
        }else{
            newCurrentDebt = (currentDebt + (paymentForNoInterest * interestRate)) - periodPayment;
        }
        return validateQuantity(newCurrentDebt);
    }
    public static Float calculateExpiredPaymentForNoInterest(Float periodPayment, Float paymentForNoInterest, Float interestRate, TransactionType transactionType){
        Float newPaymentForNoInterest = null;
        if(transactionType == TransactionType.EXPENSE){
            newPaymentForNoInterest = (paymentForNoInterest + (paymentForNoInterest * interestRate)) + periodPayment;
        }else{
            newPaymentForNoInterest = (paymentForNoInterest + (paymentForNoInterest * interestRate)) - periodPayment;
        }
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
