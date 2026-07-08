package com.example.Idea.ExceptionHandle;

public class InvalidLoanStatusException extends RuntimeException{
   public InvalidLoanStatusException(String mess){
        super(mess);
    }
}
