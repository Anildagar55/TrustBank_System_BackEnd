package com.example.Idea.ExceptionHandle;

public class InvalidAccountNumberException extends RuntimeException{
    public InvalidAccountNumberException(String mess){
        super(mess);
    }
}
